package org.tsdl.testutil.creation.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.Instant;
import java.util.Objects;

public class TestDataPainterWindow extends JFrame {

    private final JSpinner numThickness = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));

    private final JSpinner numSampleRate = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
    private final PainterFrame painter = new PainterFrame((Integer) numThickness.getValue(), (Integer) numSampleRate.getValue());

    private final JComboBox<String> cmbOutput = new JComboBox<>(new String[]{"CSV", "Java"});

    private final JCheckBox chkTopmost = new JCheckBox("Always on Top", true);

    public TestDataPainterWindow() {
        super("Test Data Painter");

        getContentPane().add(settingsPanel(), BorderLayout.NORTH);
        getContentPane().add(generatePanel(), BorderLayout.SOUTH);
        getContentPane().add(painter, BorderLayout.CENTER);

        setAlwaysOnTop(chkTopmost.isSelected());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
        setVisible(true);

        setSize(1000, 800);
        setResizable(false);
    }

    private JPanel settingsPanel() {
        var lblThickness = new JLabel("Line Thickness:");
        var thicknessPanel = new JPanel(new FlowLayout());
        thicknessPanel.add(lblThickness);
        thicknessPanel.add(numThickness);
        numThickness.addChangeListener(e -> painter.setLineThickness((Integer) numThickness.getValue()));

        var lblSampleRate = new JLabel("Sample Rate:");
        var sampleRatePanel = new JPanel(new FlowLayout());
        sampleRatePanel.add(lblSampleRate);
        sampleRatePanel.add(numSampleRate);
        numSampleRate.addChangeListener(e -> painter.setSampleRate((Integer) numSampleRate.getValue()));


        var lblOutput = new JLabel("Output Type:");
        var outputPanel = new JPanel(new FlowLayout());
        outputPanel.add(lblOutput);
        outputPanel.add(cmbOutput);

        var topMostPanel = new JPanel(new FlowLayout());
        topMostPanel.add(chkTopmost);
        chkTopmost.addActionListener(e -> setAlwaysOnTop(chkTopmost.isSelected()));


        var settingsPanel = new JPanel(new GridLayout(1, 4));
        settingsPanel.add(thicknessPanel);
        settingsPanel.add(sampleRatePanel);
        settingsPanel.add(outputPanel);
        settingsPanel.add(topMostPanel);

        return settingsPanel;
    }

    private JPanel generatePanel() {
        var btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> painter.reset());

        var btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(e -> {
            var outputType = Objects.requireNonNull(cmbOutput.getSelectedItem()).toString();

            var instantUserInput = JOptionPane.showInputDialog(this,
              "Enter date-time of first data point ('%s').%nEmpty or invalid input leads to a random date-time being picked.".formatted(
                PainterFrame.INSTANT_PATTERN),
              PainterFrame.INSTANT_FORMATTER.format(Instant.now())
            );
            Instant referenceDate;
            try {
                referenceDate = PainterFrame.INSTANT_FORMATTER.parse(instantUserInput, Instant::from);
            } catch (Exception ignored) {
                referenceDate = null;
            }

            var output = painter.getOutput(outputType, referenceDate);
            setClipboardText(output);
            JOptionPane.showMessageDialog(this, "%s output has been copied to the clipboard.".formatted(outputType));
        });

        var footerPanel = new JPanel(new GridLayout(1, 2));
        footerPanel.add(btnReset);
        footerPanel.add(btnGenerate);

        return footerPanel;
    }

    private void setClipboardText(String txt) {
        var stringSelection = new StringSelection(txt);
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
