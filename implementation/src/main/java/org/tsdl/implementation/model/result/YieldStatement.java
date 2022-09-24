package org.tsdl.implementation.model.result;

import java.util.List;
import org.tsdl.implementation.model.common.TsdlIdentifier;

/**
 * The result declaration, i.e. the yield statement of a TSDL query. Determines the result format of a query.
 */
public interface YieldStatement {
  YieldFormat format();

  List<TsdlIdentifier> samples();
}
