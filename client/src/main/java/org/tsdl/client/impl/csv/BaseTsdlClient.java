package org.tsdl.client.impl.csv;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.tsdl.client.api.QueryClientResult;
import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.client.api.TsdlClient;
import org.tsdl.client.util.ClientCommons;
import org.tsdl.client.util.TsdlClientException;
import org.tsdl.client.util.TsdlClientServiceException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.dto.QueryDto;
import org.tsdl.infrastructure.dto.QueryResultDto;

/**
 * Encapsulates common functionality for {@link TsdlClient} implementations.
 *
 * @param <T> type of {@link QueryClientSpecification} supported by the respective implementation
 */
public abstract class BaseTsdlClient<T extends QueryClientSpecification> implements TsdlClient {
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private final OkHttpClient client;

  public BaseTsdlClient() {
    this(10, TimeUnit.SECONDS);
  }

  public BaseTsdlClient(long timeout, TimeUnit unit) {
    client = new OkHttpClient.Builder()
        .readTimeout(timeout, unit)
        .build();
  }

  abstract Class<T> configClass();

  abstract File getCacheFile() throws IOException;

  abstract QueryClientResult query(T querySpecification, QueryResultDto serverResponse, File targetCacheFile) throws IOException;

  @SuppressWarnings("unchecked") // type erasure - type compatibility ensured by 'checkIsTrue' call
  @Override
  public QueryClientResult query(QueryClientSpecification querySpecification) {
    try {
      Conditions.checkNotNull(Condition.ARGUMENT, querySpecification, "Query specification must not be null.");
      Conditions.checkIsTrue(Condition.ARGUMENT, configClass().isAssignableFrom(querySpecification.getClass()),
          "Result type must be compatible with '%s', but is '%s'.", configClass().getName(), querySpecification.getClass().getName());

      var serviceResponse = query(querySpecification.serverUrl(), querySpecification.query());
      var cacheFile = getCacheFile();
      return query((T) querySpecification, serviceResponse, cacheFile);
    } catch (TsdlClientException e) {
      throw e;
    } catch (Exception e) {
      throw new TsdlClientException("An error occurred while consuming the TSDL Query API: %s".formatted(e.getMessage()), e);
    }
  }

  QueryResultDto query(String serverUrl, QueryDto querySpecification) throws Exception {
    var jsonBody = ClientCommons.OBJECT_MAPPER.writeValueAsString(querySpecification);
    var body = RequestBody.create(jsonBody, JSON);
    var request = new Request.Builder()
        .url(serverUrl)
        .post(body)
        .build();

    try (var response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw buildException(response);
      }

      var responseBody = Conditions.checkNotNull(Condition.STATE, response.body(), "Response body must not be null to process query result.");
      var responseString = responseBody.string();

      var result = ClientCommons.OBJECT_MAPPER.readValue(responseString, QueryResultDto.class);
      return Conditions.checkNotNull(Condition.STATE, result, "Result from TSDL Query service must not be null");
    }
  }

  private Exception buildException(Response response) throws IOException {
    Map<Integer, String> errorTrace = null;
    String errorBody = null;

    if (response.body() != null) {
      errorBody = Objects.requireNonNull(response.body()).string();
      var responseJson = ClientCommons.OBJECT_MAPPER.readTree(errorBody);
      errorTrace = getErrorTrace(responseJson);
    }

    return new TsdlClientServiceException(
        "Unexpected HTTP Status Code at '%s': %s".formatted(response.request().url(), response.code()),
        errorTrace,
        errorBody != null ? errorBody : "<not available>"
    );
  }

  private Map<Integer, String> getErrorTrace(JsonNode responseTree) {
    var traces = responseTree.get("errorTrace");
    if (traces == null || traces.size() <= 0) {
      return null;
    }

    var errorLogs = new TreeMap<Integer, String>();
    traces.fields().forEachRemaining(kvp -> errorLogs.put(Integer.parseInt(kvp.getKey()), kvp.getValue().textValue()));
    return errorLogs;
  }
}
