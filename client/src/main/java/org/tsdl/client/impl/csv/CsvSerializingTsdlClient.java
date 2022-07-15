package org.tsdl.client.impl.csv;

import org.tsdl.client.api.QueryClientResult;
import org.tsdl.client.api.TsdlClient;
import org.tsdl.client.impl.csv.reader.BaseReader;
import org.tsdl.client.util.QueryResultReaderFactory;
import org.tsdl.client.util.QueryResultWriterFactory;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.infrastructure.model.QueryResult;

/**
 * A {@link TsdlClient} which serializes responses obtained from the TSDL service into CSV files, to be used later on again.
 */
public class CsvSerializingTsdlClient extends BaseTsdlClient<CsvSerializingQueryClientSpecification> {
  @Override
  protected QueryClientResult query(CsvSerializingQueryClientSpecification querySpecification, QueryResultDto serverResponse) {
    Conditions.checkNotNull(Condition.ARGUMENT, querySpecification, "Query specification must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, serverResponse, "Server response must not be null.");

    var writer = QueryResultWriterFactory.getCsvWriter(serverResponse.getType());
    writer.write(serverResponse.getResult(), querySpecification);

    return new CsvSerializingTsdlClientResult();
  }

  @Override
  public QueryResult query(String filePath, Object arg1, Object arg2) {
    var resultType = BaseReader.peekType(filePath);
    var reader = QueryResultReaderFactory.getCsvReader(resultType);

    return reader.read(filePath);
  }

  @Override
  Class<CsvSerializingQueryClientSpecification> configClass() {
    return CsvSerializingQueryClientSpecification.class;
  }
}
