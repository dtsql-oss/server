package org.tsdl.infrastructure.extension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.tsdl.infrastructure.model.DataPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class JFreeChartTimeSeriesTestVisualizer implements TimeSeriesTestVisualizer {
    // TODO remove .getName() as soon as SLF4J logging is introduced
    private static final Logger LOGGER = Logger.getLogger(JFreeChartTimeSeriesTestVisualizer.class.getName());

    private TestVisualizationWindow visualizer;
    private final SingleActionAwait singleActionAwait = new SingleActionAwait();

    private String errorMessage;

    @Override
    public boolean visualizeBlocking(TsdlTestInfo testInformation, TsdlTestVisualization visualizationConfiguration) {
        if (visualizationConfiguration != null && visualizationConfiguration.skipVisualization()) {
            return true;
        }

        singleActionAwait.reset();
        var dataSet = createDataset(testInformation.timeSeries());
        SwingUtilities.invokeLater(() -> {
            try {
                visualizer = new TestVisualizationWindow(testInformation, visualizationConfiguration, dataSet, singleActionAwait);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                singleActionAwait.actionPerformed();
            }
        });

        // block until UI interaction is done, i.e. CountDownLatch has reached zero
        singleActionAwait.waitFor();

        if (errorMessage != null) {
            LOGGER.warning("Encountered problem while visualizing test: %s".formatted(errorMessage));
            LOGGER.warning("Executing test anyway, without prior visualization.");
            return true;
        }

        return visualizer.shouldExecuteTest;
    }

    private XYDataset createDataset(List<List<DataPoint>> timeSeries) {
        var dataset = new TimeSeriesCollection();

        for (var i = 0; i < timeSeries.size(); i++) {
            var series = timeSeries.get(i);

            var newSeries = new TimeSeries(String.format("Series%d", i));
            for (var dataPoint : series) {
                newSeries.add(new Millisecond(Date.from(dataPoint.getTimestamp())), dataPoint.asDecimal());
            }

            dataset.addSeries(newSeries);
        }

        return dataset;
    }

    private static class TestVisualizationWindow extends JFrame {
        private Boolean shouldExecuteTest;

        public TestVisualizationWindow(TsdlTestInfo testInfo, TsdlTestVisualization visualizationConfig, XYDataset data, SingleActionAwait clickAwaiter) {
            super(testInfo.longName());

            getContentPane().add(createChartPanel(testInfo.shortName(), visualizationConfig, data), BorderLayout.CENTER);
            getContentPane().add(createButtonPanel(clickAwaiter), BorderLayout.SOUTH);
            setVisible(true);
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            pack();
        }

        private JPanel createChartPanel(String title, TsdlTestVisualization visualizationInfo, XYDataset data) {
            var chart = ChartFactory.createTimeSeriesChart(title, "Date Time", "Value", data);
            var plot = (XYPlot) chart.getPlot();

            var renderPointShapes = visualizationInfo == null || visualizationInfo.renderPointShape();
            var renderer = (XYLineAndShapeRenderer) plot.getRenderer();
            for (int i = 0; i < plot.getSeriesCount(); i++) {
                renderer.setSeriesShapesVisible(i, renderPointShapes);
            }

            plot.setBackgroundPaint(new Color(255, 255, 255));
            var domain = (DateAxis) plot.getDomainAxis();

            if (visualizationInfo != null && !TsdlTestVisualization.DEFAULT_AXIS_FORMAT.equals(visualizationInfo.dateAxisFormat())) {
                domain.setDateFormatOverride(new SimpleDateFormat(visualizationInfo.dateAxisFormat()));
            }

            return new ChartPanel(chart);
        }

        private JPanel createButtonPanel(SingleActionAwait clickAwaiter) {
            var btnGet = new JButton("Run Test");
            var btnNo = new JButton("Skip Test");

            // both buttons should close the window
            ActionListener frameCloser = e -> TestVisualizationWindow.this.dispose();
            btnGet.addActionListener(frameCloser);
            btnNo.addActionListener(frameCloser);

            // execution is blocked until one of the buttons has been clicked
            btnGet.addActionListener(clickAwaiter);
            btnNo.addActionListener(clickAwaiter);

            // only one button denotes that the test should be executed
            btnGet.addActionListener(e1 -> shouldExecuteTest = true);
            btnNo.addActionListener(e -> shouldExecuteTest = false);

            var btnPanel = new JPanel(new GridLayout(1, 2));
            btnPanel.add(btnGet);
            btnPanel.add(btnNo);

            return btnPanel;
        }
    }

    private static class SingleActionAwait implements ActionListener {
        private CountDownLatch latch = createCountDown();

        public void actionPerformed() {
            actionPerformed(null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            latch.countDown();
        }

        public void waitFor() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private CountDownLatch createCountDown() {
            return new CountDownLatch(1);
        }

        public void reset() {
            latch = createCountDown();
        }
    }
}


