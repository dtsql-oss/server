package org.tsdl.service;


import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.parsing.TsdlQueryParser;
import org.tsdl.implementation.parsing.exception.TsdlParserException;

/**
 * Small utility application that validates the syntax of a given TSDL Query.
 */
public class QueryValidationApplication extends JFrame {
  private static final int INITIAL_HEIGHT = 800;

  private final JTextArea txtResult = new JTextArea();

  private final transient TsdlQueryParser queryParser = ObjectFactory.INSTANCE.queryParser();

  private QueryValidationApplication() {
    super("TSDL Syntax Checker");

    getContentPane().add(createContentPane(), BorderLayout.CENTER);

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(1000, INITIAL_HEIGHT);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private JComponent createContentPane() {
    var splitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        createTopPanel(),
        createBottomPanel()
    );

    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation((int) (INITIAL_HEIGHT * 0.8));

    return splitPane;
  }

  private JComponent createTopPanel() {
    var topPanel = new JPanel(new BorderLayout());
    topPanel.add(new JLabel("TSDL query to validate:"), BorderLayout.NORTH);

    var txtQuery = new JTextArea();
    topPanel.add(new JScrollPane(txtQuery), BorderLayout.CENTER);

    var cmdValidate = new JButton("Check Syntax");
    cmdValidate.addActionListener(a -> {
      try {
        queryParser.parseQuery(txtQuery.getText());
        txtResult.setText("SUCCESS!");
      } catch (TsdlParserException e) {
        var error = new StringBuilder();
        error.append(e.getMessage());

        //CHECKSTYLE.OFF: MatchXpath - false positive, 'var' cannot be used here (due to 'ex = e.getCause()')
        Throwable ex = e;
        //CHECKSTYLE.ON: MatchXpath
        while (ex.getCause() != null) {
          error.append("\n-> with cause:\n");
          error.append(ex.getCause().getMessage());
          ex = ex.getCause();
        }

        txtResult.setText(String.format("ERROR!%n%s", error));
      }
    });
    topPanel.add(cmdValidate, BorderLayout.SOUTH);

    return topPanel;
  }

  private JComponent createBottomPanel() {
    var bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(new JLabel("Validation result:"), BorderLayout.NORTH);

    txtResult.setBackground(new Color(235, 235, 235));
    txtResult.setEditable(false);
    bottomPanel.add(new JScrollPane(txtResult), BorderLayout.CENTER);

    return bottomPanel;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(QueryValidationApplication::new);
  }
}