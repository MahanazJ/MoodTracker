package com.mooddiary.ui;

import com.mooddiary.diary.DiaryService;
import com.mooddiary.diary.MoodStatistics;
import com.mooddiary.model.DiaryEntry;
import com.mooddiary.model.Mood;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** The mood chart page: a timeline of daily moods plus a breakdown for the period. */
final class ChartPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private enum Range {
        WEEK("Week", 7), MONTH("Month", 30), QUARTER("3 months", 91), YEAR("Year", 365), ALL("All time", -1);
        final String label;
        final int days;
        Range(String label, int days) { this.label = label; this.days = days; }
    }

    private final DiaryService service;
    private final MoodChart chart;
    private final Breakdown breakdown = new Breakdown();
    private final JLabel summary = new JLabel(" ");
    private final List<FlatButton> rangeButtons = new ArrayList<>();
    private Range range = Range.MONTH;

    ChartPanel(DiaryService service, Consumer<LocalDate> onOpen) {
        super(new BorderLayout());
        this.service = service;
        this.chart = new MoodChart(onOpen);
        setBackground(Theme.PAPER);
        setBorder(BorderFactory.createEmptyBorder(14, 28, 22, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Your moods over time");
        title.setFont(Theme.serif(Font.BOLD, 26));
        title.setForeground(Theme.INK);
        top.add(title, BorderLayout.WEST);
        JPanel ranges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        ranges.setOpaque(false);
        for (Range r : Range.values()) {
            FlatButton b = new FlatButton(r.label, FlatButton.Style.TAB);
            b.setFont(Theme.sans(Font.PLAIN, 13.5f));
            b.addActionListener(e -> {
                range = r;
                refresh();
            });
            rangeButtons.add(b);
            ranges.add(b);
        }
        top.add(ranges, BorderLayout.EAST);
        summary.setFont(Theme.sans(Font.PLAIN, 14));
        summary.setForeground(Theme.MUTED);
        summary.setBorder(BorderFactory.createEmptyBorder(6, 0, 10, 0));
        top.add(summary, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        add(chart, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(16, 64, 0, 22));
        JLabel bh = new JLabel("How often each mood showed up");
        bh.setFont(Theme.sans(Font.BOLD, 13.5f));
        bh.setForeground(Theme.INK);
        bottom.add(bh, BorderLayout.NORTH);
        bottom.add(Box.createVerticalStrut(6), BorderLayout.CENTER);
        bottom.add(breakdown, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        service.addChangeListener(this::refresh);
        refresh();
    }

    void refresh() {
        LocalDate to = service.today();
        List<DiaryEntry> all = service.allEntries();
        LocalDate from = range.days > 0 ? to.minusDays(range.days - 1L)
                : (all.isEmpty() ? to.minusDays(6) : all.get(0).date());
        if (!from.isBefore(to)) {
            from = to.minusDays(6);
        }
        for (int i = 0; i < rangeButtons.size(); i++) {
            rangeButtons.get(i).setTabSelected(Range.values()[i] == range);
        }
        chart.setData(all, from, to);
        MoodStatistics stats = service.statistics(from, to);
        breakdown.setCounts(stats.counts());

        if (stats.entries() == 0) {
            summary.setText("Save a diary entry and it shows up here as a point on the chart.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(stats.entries()).append(stats.entries() == 1 ? " day written" : " days written");
            sb.append(", mostly ").append(stats.mostCommon().map(m -> m.displayName().toLowerCase()).orElse("mixed"));
            sb.append(". On average your days felt ").append(MoodStatistics.describeValence(stats.averageValence()).toLowerCase());
            sb.append(". Current streak ").append(stats.currentStreak()).append(stats.currentStreak() == 1 ? " day" : " days");
            sb.append(", best ").append(stats.longestStreak()).append('.');
            summary.setText(sb.toString());
        }
        revalidate();
        repaint();
    }

    /** Horizontal bar per mood, most frequent first. */
    private static final class Breakdown extends JComponent {
        private static final long serialVersionUID = 1L;
        private List<Map.Entry<Mood, Integer>> rows = List.of();

        void setCounts(Map<Mood, Integer> counts) {
            rows = counts.entrySet().stream()
                    .sorted(Map.Entry.<Mood, Integer>comparingByValue(Comparator.reverseOrder()))
                    .toList();
            int cols = 2;
            int lines = Math.max(1, (rows.size() + cols - 1) / cols);
            setPreferredSize(new Dimension(600, lines * 26));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(Theme.sans(Font.PLAIN, 13));
            FontMetrics fm = g.getFontMetrics();
            if (rows.isEmpty()) {
                g.setColor(Theme.MUTED);
                g.drawString("Nothing yet for this period.", 0, fm.getAscent());
                g.dispose();
                return;
            }
            int max = rows.stream().mapToInt(Map.Entry::getValue).max().orElse(1);
            int colW = getWidth() / 2;
            int labelW = 96;
            int countW = 30;
            for (int i = 0; i < rows.size(); i++) {
                int col = i % 2;
                int line = i / 2;
                int x = col * colW;
                int y = line * 26;
                Mood m = rows.get(i).getKey();
                int n = rows.get(i).getValue();
                int barMax = colW - labelW - countW - 24;
                g.setColor(Theme.mood(m));
                g.fillOval(x, y + 6, 10, 10);
                g.setColor(Theme.INK);
                g.drawString(m.displayName(), x + 16, y + fm.getAscent() + 3);
                g.setColor(Theme.tint(Theme.mood(m), 0.82));
                g.fillRoundRect(x + labelW, y + 7, barMax, 9, 9, 9);
                g.setColor(Theme.mood(m));
                g.fillRoundRect(x + labelW, y + 7, Math.max(9, barMax * n / max), 9, 9, 9);
                g.setColor(Theme.MUTED);
                g.drawString(Integer.toString(n), x + labelW + barMax + 8, y + fm.getAscent() + 3);
            }
            g.dispose();
        }
    }
}
