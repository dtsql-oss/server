package org.tsdl.testutil.creation;

import javax.swing.SwingUtilities;
import org.tsdl.testutil.creation.ui.TestDataPainterWindow;

public class TestDataCreator {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(TestDataPainterWindow::new);
  }
}