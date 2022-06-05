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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javax.swing.JLabel;
import javax.swing.JPanel;

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

  public static Instant randomInstantBetween(Instant startInclusive, Instant endExclusive) {
    long startSeconds = startInclusive.getEpochSecond();
    long endSeconds = endExclusive.getEpochSecond();
    long random = ThreadLocalRandom
        .current()
        .nextLong(startSeconds, endSeconds);

    return Instant.ofEpochSecond(random);
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
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

  public String getOutput(String type, Instant referenceDate) {
    var lst = IntStream.range(0, points.size())
        .filter(n -> n % sampleRate == 0)
        .mapToObj(points::get)
        .sorted(Comparator.comparingDouble(Point::getX))
        .filter(distinctByKey(Point::getX))
        .toList();

    var lowestDate = referenceDate != null ? referenceDate : randomInstantBetween(
        Instant.now().minus(5L * 12 * 30, ChronoUnit.DAYS), // 5 years
        Instant.now().plus(5L * 12 * 30, ChronoUnit.DAYS) // 5 years
    );
    // TODO also serialize avg, sum etc. as header comments
    var output = new StringBuilder();
    if ("CSV".equals(type)) {
      for (int i = 0; i < lst.size(); i++) {
        Point point = lst.get(i);
        output
            .append(INSTANT_FORMATTER.format(lowestDate.plus(i * 15L, ChronoUnit.MINUTES)))
            .append(";")
            .append(point.getY())
            .append("\n");
      }
    } else if ("Java".equals(type)) {
      output.append("List.of(\n");
      for (int i = 0; i < lst.size(); i++) {
        Point point = lst.get(i);
        var trailingComma = i == lst.size() - 1 ? "" : ",";
        output
            .append("  DataPoint.of(")
            .append(INSTANT_FORMATTER.format(lowestDate.plus(i * 15L, ChronoUnit.MINUTES)))
            .append(", ")
            .append(point.getY())
            .append(")%s%n".formatted(trailingComma));
      }
      output.append(")");
    } else {
      throw new IllegalArgumentException("Unknown output type '%s'".formatted(type));
    }

    return output.toString();
  }

  public void mouseDragged(MouseEvent e) {
    var g = getGraphics();
    g.setColor(Color.blue);
    g.fillOval(e.getX(), e.getY(), lineThickness, lineThickness);

    var dpDate = e.getX();
    var dpValue = -e.getY() + this.getHeight() / 2; // e.g. for height = 200: (0 -> 200), (200 -> 0), (400 -> -200)....y' = -y + 200
    points.add(new Point(dpDate, dpValue));
  }

  public void mouseMoved(MouseEvent e) {
    // do nothing
  }
}
