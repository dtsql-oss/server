package org.tsdl.storage;

import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.ThrowingRunnable;
import org.tsdl.infrastructure.common.ThrowingSupplier;

/**
 * An abstract base class providing utility methods for {@link StorageService} implementations.
 */
public abstract class BaseStorageService {
  protected <T> T safeStorageAccess(ThrowingSupplier<T, Exception> proc) {
    try {
      return proc.get();
    } catch (Exception e) {
      throw new TsdlStorageException("An error occurred during a storage access.", e);
    }
  }

  protected void safeStorageAccess(ThrowingRunnable<Exception> action) {
    try {
      action.run();
    } catch (Exception e) {
      throw new TsdlStorageException("An error occurred during a storage access.", e);
    }
  }
}
