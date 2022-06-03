package org.tsdl.testutil.creation;

import org.tsdl.testutil.creation.ui.TestDataPainterWindow;

import javax.swing.*;

public class TestDataCreator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TestDataPainterWindow::new);
    }
}