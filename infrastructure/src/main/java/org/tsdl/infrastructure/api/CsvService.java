package org.tsdl.infrastructure.api;

import org.tsdl.infrastructure.model.TsdlResult;

public interface CsvService {

    <T> TsdlResult<T> execute();

}
