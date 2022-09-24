package org.tsdl.implementation.formatting;

import java.io.OutputStream;

/**
 * A special {@link OutputStream} that collects all the data written to it as stromg end exposes it via a {@link ListeningOutputStream#getData()}.
 */
public class ListeningOutputStream extends OutputStream {
  private final StringBuilder writtenData = new StringBuilder();

  @Override
  public void write(int dataByte) {
    writtenData.append(Character.toString(dataByte));
  }

  public String getData() {
    return writtenData.toString();
  }
}
