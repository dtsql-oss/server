package org.tsdl.client.impl.csv;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.tsdl.client.api.QueryClientResult;
import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.client.api.TsdlClient;
import org.tsdl.client.impl.csv.reader.BaseReader;
import org.tsdl.client.util.QueryResultWriterFactory;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.dto.QueryDto;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.infrastructure.dto.StorageDto;
import org.tsdl.infrastructure.model.QueryResultType;

/**
 * A {@link TsdlClient} which serializes responses obtained from the TSDL service into CSV files, to be used later on again.
 */
public class CsvSerializingTsdlClient extends BaseTsdlClient<CsvSerializingQueryClientSpecification> {
  @Override
  protected QueryClientResult query(CsvSerializingQueryClientSpecification querySpecification, QueryResultDto serverResponse, File targetCacheFile)
      throws IOException {
    Conditions.checkNotNull(Condition.ARGUMENT, querySpecification, "Query specification must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, serverResponse, "Server response must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, targetCacheFile, "Target cache file must not be null");

    var canonicalCachePath = targetCacheFile.getCanonicalPath();
    var writer = QueryResultWriterFactory.getCsvWriter(serverResponse.getType());
    writer.write(serverResponse.getResult(), querySpecification, canonicalCachePath);

    return new CsvSerializingTsdlClientResult(serverResponse.getResult(), canonicalCachePath);
  }

  @Override
  public QueryClientResult query(String cachedTimeSeriesPath, String queryEndpoint, String tsdlQuery) {
    var resultType = BaseReader.peekType(cachedTimeSeriesPath);
    Conditions.checkEquals(
        Condition.ARGUMENT,
        resultType,
        QueryResultType.DATA_POINTS,
        "Querying from cached TSDL results is only valid for cache files of type '%s', but found '%s'",
        QueryResultType.DATA_POINTS,
        resultType
    );

    var config = cacheLookupConfiguration(cachedTimeSeriesPath, queryEndpoint, tsdlQuery);
    return query(config);
  }

  private QueryClientSpecification cacheLookupConfiguration(String filePath, String queryEndpoint, String query) {
    return new CsvSerializingQueryClientSpecification(
        QueryDto.builder()
            .storage(
                StorageDto.builder()
                    .name("csv")
                    .serviceConfiguration(Map.of())
                    .lookupConfiguration(
                        Map.of(
                            "filePath", filePath,
                            "fieldSeparator", ';',
                            "skipHeaders", 3,
                            "customEndOfFileMarkers", new String[] {"#TSDL Query Evaluation Logs"}
                        )
                    )
                    .transformationConfiguration(
                        Map.of(
                            "valueColumn", 1,
                            "timeColumn", 0,
                            "timeFormat", "yyyy-MM-dd'T'HH:mm:ss[.SSS]XX"
                        )
                    )
                    .build()
            )
            .tsdlQuery(query)
            .build(),
        queryEndpoint
    );
  }

  @Override
  Class<CsvSerializingQueryClientSpecification> configClass() {
    return CsvSerializingQueryClientSpecification.class;
  }

  @Override
  File getCacheFile() throws IOException {
    return File.createTempFile("tsdl-client_", "_csv-serializing-client");
  }
}
