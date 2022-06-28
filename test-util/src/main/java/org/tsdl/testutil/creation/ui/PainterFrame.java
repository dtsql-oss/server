package org.tsdl.testutil.creation.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Represents a panel which allows users to paint time series to be used as test input data with their mouse.
 */
public class PainterFrame extends JPanel implements MouseMotionListener {
  public static final String INSTANT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
  public static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter
      .ofPattern(INSTANT_PATTERN)
      .withZone(ZoneOffset.UTC);
  private final List<Point> points = new ArrayList<>();

  private final JLabel mid = new JLabel("0");
  private final JLabel top = new JLabel("0");
  private final JLabel bottom = new JLabel("0");
  private int lineThickness;
  private int sampleRate;

  PainterFrame(int lineThickness, int sampleRate) {
    this.lineThickness = lineThickness;
    this.sampleRate = sampleRate;

    setLayout(null);

    addComponentListener(new ComponentListener() {
      @Override
      public void componentResized(ComponentEvent e) {
        remove(mid);
        remove(top);
        remove(bottom);

        mid.setText("0");
        mid.setBounds(10, getHeight() / 2 - 10, 100, 20);
        top.setText(getHeight() / 2 + "");
        top.setBounds(10, 0, 100, 20);
        bottom.setText(-getHeight() / 2 + "");
        bottom.setBounds(10, getHeight() - 20, 100, 20);

        add(mid);
        add(top);
        add(bottom);
      }

      @Override
      public void componentMoved(ComponentEvent e) {
        // nothing to do
      }

      @Override
      public void componentShown(ComponentEvent e) {
        // nothing to do
      }

      @Override
      public void componentHidden(ComponentEvent e) {
        // nothing to do
      }
    });

    setBackground(Color.white);

    setVisible(true);
    addMouseMotionListener(this);
  }

  private static Instant randomInstantBetween(Instant startInclusive, Instant endExclusive) {
    var startSeconds = startInclusive.getEpochSecond();
    var endSeconds = endExclusive.getEpochSecond();
    var random = ThreadLocalRandom
        .current()
        .nextLong(startSeconds, endSeconds);

    return Instant.ofEpochSecond(random);
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    var seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  public void setLineThickness(int lineThickness) {
    this.lineThickness = lineThickness;
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
  }

  public void reset() {
    points.clear();
    this.repaint();
  }

  String getOutput(String type, Instant referenceDate) {
    var lst = IntStream.range(0, points.size())
        .filter(n -> n % sampleRate == 0)
        .mapToObj(points::get)
        .sorted(Comparator.comparingDouble(Point::getX))
        .filter(distinctByKey(Point::getX))
        .collect(Collectors.toList());

    var lowestDate = referenceDate != null ? referenceDate : randomInstantBetween(
        Instant.now().minus(5L * 12 * 30, ChronoUnit.DAYS), // 5 years
        Instant.now().plus(5L * 12 * 30, ChronoUnit.DAYS) // 5 years
    );

    Conditions.checkContains(Condition.ARGUMENT, List.of("Java", "CSV"), type, "Unknown output type '%s'", type);

    var summaryStatistics = lst.stream().mapToDouble(Point::getY).summaryStatistics();
    switch (type) {
      case "CSV":
        return serializeCsv(lst, lowestDate, summaryStatistics);
      case "Java":
        return serializeJava(lst, lowestDate, summaryStatistics);
      default:
        throw Conditions.exception(Condition.ARGUMENT, "Unknown output type '%s'", type);
    }
  }

  //CHECKSTYLE.OFF: MissingJavadocMethod - No documentation for external interface method needed.
  public void mouseDragged(MouseEvent e) {
    var g = getGraphics();
    g.setColor(Color.blue);
    g.fillOval(e.getX(), e.getY(), lineThickness, lineThickness);

    var dpDate = e.getX();
    var dpValue = -e.getY() + this.getHeight() / 2; // e.g. for height = 200: (0 -> 200), (200 -> 0), (400 -> -200)....y' = -y + 200
    points.add(new Point(dpDate, dpValue));
  }
  //CHECKSTYLE.ON: MissingJavadocMethod

  public void mouseMoved(MouseEvent e) {
    // do nothing
  }

  private String serializeCsv(List<Point> lst, Instant lowestDate, DoubleSummaryStatistics summaryStatistics) {
    var output = new StringBuilder();

    output.append("# avg: ").append(summaryStatistics.getAverage()).append("\n");
    output.append("# sum: ").append(summaryStatistics.getSum()).append("\n");
    output.append("# min: ").append(summaryStatistics.getMin()).append("\n");
    output.append("# max: ").append(summaryStatistics.getMax()).append("\n");
    output.append("# count: ").append(summaryStatistics.getCount()).append("\n");

    for (var i = 0; i < lst.size(); i++) {
      var point = lst.get(i);
      output
          .append(INSTANT_FORMATTER.format(lowestDate.plus(i * 15L, ChronoUnit.MINUTES)))
          .append(";")
          .append(point.getY())
          .append("\n");
    }
    return output.toString();
  }

  private String serializeJava(List<Point> lst, Instant lowestDate, DoubleSummaryStatistics summaryStatistics) {
    var output = new StringBuilder();

    output.append("/* statistics\n");
    output.append(" avg: ").append(summaryStatistics.getAverage()).append("\n");
    output.append(" sum: ").append(summaryStatistics.getSum()).append("\n");
    output.append(" min: ").append(summaryStatistics.getMin()).append("\n");
    output.append(" max: ").append(summaryStatistics.getMax()).append("\n");
    output.append(" count: ").append(summaryStatistics.getCount()).append("\n");
    output.append("*/\n");

    output.append("List.of(\n");
    for (var i = 0; i < lst.size(); i++) {
      var point = lst.get(i);
      var trailingComma = i == lst.size() - 1 ? "" : ",";
      output
          .append("  DataPoint.of(")
          .append(INSTANT_FORMATTER.format(lowestDate.plus(i * 15L, ChronoUnit.MINUTES)))
          .append(", ")
          .append(point.getY())
          .append(String.format(")%s%n", trailingComma));
    }
    output.append(")");
    return output.toString();
  }
}
