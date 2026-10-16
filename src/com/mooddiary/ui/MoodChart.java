package com.mooddiary.ui;

import com.mooddiary.diary.MoodStatistics;
import com.mooddiary.model.DiaryEntry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;

/**
 * Daily mood over time. Height is how pleasant the day felt (great at the top,
 * rough at the bottom); each point is colored by that day's mood and sized by
 * intensity. Missing days break the line with a dashed bridge.
 */
final class MoodChart extends JComponent {

    private static final long serialVersionUID = 1L;
    private static final String[] BANDS = {"Great", "Good", "Okay", "Low", "Rough"};
    private static final double[] BAND_VALUES = {0.8, 0.4, 0.0, -0.4, -0.8};

    private List<DiaryEntry> entries = List.of();
    private LocalDate from = LocalDate.now();
    private LocalDate to = LocalDate.now();
    private final List<Point> points = new ArrayList<>();
    private Point hover;
    private final Consumer<LocalDate> onOpen;

    private record Point(double x, double y, double r, DiaryEntry entry) { }

    MoodChart(Consumer<LocalDate> onOpen) {
        this.onOpen = onOpen;
        setPreferredSize(new Dimension(800, 360));
        setOpaque(false);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setInitialDelay(80);
        MouseAdapter m = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = hit(e.getX(), e.getY());
                if (p != hover) {
                    hover = p;
                    setCursor(Cursor.getPredefinedCursor(p == null ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = null;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = hit(e.getX(), e.getY());
                if (p != null) {
                    onOpen.accept(p.entry().date());
                }
            }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
    }

    void setData(List<DiaryEntry> entries, LocalDate from, LocalDate to) {
        this.entries = List.copyOf(entries);
        this.from = from;
        this.to = to.isAfter(from) ? to : from.plusDays(1);
        hover = null;
        repaint();
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Point p = hit(e.getX(), e.getY());
        if (p == null) {
            return null;
        }
        DiaryEntry d = p.entry();
        String snippet = d.snippet(90).replace("&", "&amp;").replace("<", "&lt;");
        return "<html><b>" + d.date().format(DateTimeFormatter.ofPattern("EEEE, MMM d")) + "</b><br>"
                + d.mood().displayName() + ", intensity " + d.intensity() + " of 10<br>"
                + "<i>" + snippet + "</i><br><font color='#6B7285'>Click to read this day</font></html>";
    }

    private Point hit(int x, int y) {
        for (Point p : points) {
            double dx = p.x() - x;
            double dy = p.y() - y;
            if (dx * dx + dy * dy <= (p.r() + 5) * (p.r() + 5)) {
                return p;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int left = 64;
        int right = 22;
        int top = 18;
        int bottom = 40;
        int w = getWidth() - left - right;
        int h = getHeight() - top - bottom;
        if (w < 50 || h < 50) {
            g.dispose();
            return;
        }

        // background: a faint wash from warm (great) to cool (rough)
        g.setPaint(new GradientPaint(0, top, new Color(0xFBF7EA), 0, top + h, new Color(0xEEF1F8)));
        g.fillRoundRect(left, top, w, h, 12, 12);

        g.setFont(Theme.sans(Font.PLAIN, 12.5f));
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < BANDS.length; i++) {
            int y = yFor(BAND_VALUES[i], top, h);
            g.setColor(i == 2 ? Theme.LINE : Theme.RULE);
            g.setStroke(i == 2 ? new BasicStroke(1.2f) : new BasicStroke(1f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 1f, new float[]{3f, 4f}, 0f));
            g.drawLine(left + 6, y, left + w - 6, y);
            g.setColor(Theme.MUTED);
            g.drawString(BANDS[i], left - 12 - fm.stringWidth(BANDS[i]), y + fm.getAscent() / 2 - 1);
        }

        long span = Math.max(1, ChronoUnit.DAYS.between(from, to));
        // x-axis labels
        g.setStroke(new BasicStroke(1f));
        int ticks = Math.min(8, (int) span + 1);
        DateTimeFormatter fmt = span > 300 ? DateTimeFormatter.ofPattern("MMM yyyy") : DateTimeFormatter.ofPattern("MMM d");
        String lastLabel = "";
        for (int i = 0; i < ticks; i++) {
            LocalDate d = from.plusDays(Math.round((double) span * i / Math.max(1, ticks - 1)));
            String label = d.format(fmt);
            if (label.equals(lastLabel)) {
                continue;
            }
            lastLabel = label;
            int x = xFor(d, left, w, span);
            g.setColor(Theme.MUTED);
            g.drawString(label, Math.max(left, Math.min(left + w - fm.stringWidth(label), x - fm.stringWidth(label) / 2)),
                    top + h + 24);
        }

        points.clear();
        for (DiaryEntry e : entries) {
            if (e.date().isBefore(from) || e.date().isAfter(to)) {
                continue;
            }
            double r = 4.5 + e.intensity() * 0.55;
            points.add(new Point(xFor(e.date(), left, w, span), yFor(e.valence(), top, h), r, e));
        }

        if (points.isEmpty()) {
            g.setFont(Theme.serif(Font.ITALIC, 17));
            g.setColor(Theme.MUTED);
            String msg = "No diary entries in this period yet. Each saved day appears here.";
            fm = g.getFontMetrics();
            g.drawString(msg, left + (w - fm.stringWidth(msg)) / 2, top + h / 2);
            g.dispose();
            return;
        }

        // connecting line: solid between consecutive days, dashed across gaps
        for (int i = 1; i < points.size(); i++) {
            Point a = points.get(i - 1);
            Point b = points.get(i);
            boolean consecutive = a.entry().date().plusDays(1).equals(b.entry().date());
            g.setColor(Theme.tint(Theme.ACCENT, consecutive ? 0.45 : 0.7));
            g.setStroke(consecutive ? new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                    : new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{4f, 5f}, 0f));
            Path2D seg = new Path2D.Double();
            seg.moveTo(a.x(), a.y());
            double mx = (a.x() + b.x()) / 2;
            seg.curveTo(mx, a.y(), mx, b.y(), b.x(), b.y());
            g.draw(seg);
        }

        for (Point p : points) {
            Color c = Theme.mood(p.entry().mood());
            boolean hot = p == hover;
            double r = hot ? p.r() + 2.5 : p.r();
            g.setColor(Theme.tint(c, 0.7));
            g.fill(new Ellipse2D.Double(p.x() - r - 3, p.y() - r - 3, 2 * r + 6, 2 * r + 6));
            g.setColor(c);
            g.fill(new Ellipse2D.Double(p.x() - r, p.y() - r, 2 * r, 2 * r));
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.5f));
            g.draw(new Ellipse2D.Double(p.x() - r, p.y() - r, 2 * r, 2 * r));
        }

        // label for the hovered point, drawn last so it sits on top
        if (hover != null) {
            String label = hover.entry().mood().displayName();
            g.setFont(Theme.sans(Font.BOLD, 12.5f));
            fm = g.getFontMetrics();
            int lw = fm.stringWidth(label) + 14;
            int lx = (int) Math.max(left, Math.min(left + w - lw, hover.x() - lw / 2.0));
            int ly = (int) (hover.y() - hover.r() - 30);
            if (ly < top) {
                ly = (int) (hover.y() + hover.r() + 8);
            }
            g.setColor(Theme.INK);
            g.fillRoundRect(lx, ly, lw, 22, 8, 8);
            g.setColor(Color.WHITE);
            g.drawString(label, lx + 7, ly + 15);
        }
        g.dispose();
    }

    private int xFor(LocalDate d, int left, int w, long span) {
        double t = (double) ChronoUnit.DAYS.between(from, d) / span;
        return (int) Math.round(left + 18 + t * (w - 36));
    }

    private static int yFor(double valence, int top, int h) {
        double t = (1 - Math.max(-1, Math.min(1, valence))) / 2;   // +1 at top, -1 at bottom
        return (int) Math.round(top + 14 + t * (h - 28));
    }

    static String bandFor(double v) {
        return MoodStatistics.describeValence(v);
    }
}
