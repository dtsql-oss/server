package org.tsdl.testutil.creation;

import javax.swing.SwingUtilities;
import org.tsdl.testutil.creation.ui.TestDataPainterWindow;

/**
 * Allows for interactive creation of resource files to be used as unit test inputs.
 */
public class TestDataCreator {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(TestDataPainterWindow::new);
  }
}