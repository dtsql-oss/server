package org.tsdl.client;

import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.dto.QueryDto;
import org.tsdl.infrastructure.dto.QueryResultDto;

/**
 * Encapsulates common functionality for {@link TsdlClient} implementations.
 *
 * @param <T> type of {@link QueryClientSpecification} supported by the respective implementation
 */
@Slf4j
public abstract class BaseTsdlClient<T extends QueryClientSpecification> implements TsdlClient {
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private final OkHttpClient client = new OkHttpClient();

  abstract Class<T> configClass();

  abstract QueryClientResult query(T querySpecification, QueryResultDto serverResponse);

  @SuppressWarnings("unchecked") // type erasure - type compatibility ensured by 'checkIsTrue' call
  @Override
  public QueryClientResult query(QueryClientSpecification querySpecification) {
    try {
      Conditions.checkIsTrue(Condition.ARGUMENT, configClass().isAssignableFrom(querySpecification.getClass()),
          "Result type must be compatible with '%s', but is '%s'.", configClass().getName(), querySpecification.getClass().getName());

      var serviceResponse = query(querySpecification.serverUrl(), querySpecification.query());
      return query((T) querySpecification, serviceResponse);
    } catch (Exception e) {
      throw new TsdlClientException("An error occurred during consuming the TSDL Query API.");
    }
  }

  QueryResultDto query(String serverUrl, QueryDto querySpecification) throws IOException {
    var jsonBody = ClientCommons.OBJECT_MAPPER.writeValueAsString(querySpecification);
    var body = RequestBody.create(jsonBody, JSON);
    var request = new Request.Builder()
        .url(serverUrl)
        .post(body)
        .build();

    try (var response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("TSDL Query failed: {}", response.body() != null ? Objects.requireNonNull(response.body()).string() : "unknown reason");
        throw new IOException(
            String.format("Unexpected HTTP Status Code at '%s': %s", response.request().url(), response.code())
        );
      }

      var responseBody = Conditions.checkNotNull(Condition.STATE, response.body(), "Response body must not be null to process query result.");
      var responseString = responseBody.string();
      return ClientCommons.OBJECT_MAPPER.readValue(responseString, QueryResultDto.class);
    }
  }
}