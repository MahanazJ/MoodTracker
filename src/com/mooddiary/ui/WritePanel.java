package com.mooddiary.ui;

import com.mooddiary.analysis.MoodAnalysis;
import com.mooddiary.diary.DiaryService;
import com.mooddiary.model.DiaryEntry;
import com.mooddiary.model.Mood;
import com.mooddiary.recommendation.PlaylistRecommender;
import com.mooddiary.storage.StorageException;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** The diary page for one day, with the reading and the playlist beside it. */
final class WritePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter HEADING = DateTimeFormatter.ofPattern("EEEE, MMMM d");
    private static final List<String> PROMPTS = List.of(
            "How was today? Start anywhere.",
            "What's been on your mind today?",
            "What happened today, and how did it feel?",
            "Dear diary…");

    private final DiaryService service;
    private final ResultPanel results;
    private final DiaryPage page = new DiaryPage();
    private final JLabel dateLabel = new JLabel();
    private final JLabel yearLabel = new JLabel();
    private final JLabel live = new JLabel(" ");
    private final JLabel status = new JLabel(" ");
    private final FlatButton next = new FlatButton("Next day", FlatButton.Style.QUIET);
    private final FlatButton todayButton = new FlatButton("Today", FlatButton.Style.QUIET);
    private final FlatButton save = new FlatButton("Save and find my songs", FlatButton.Style.PRIMARY);
    private final Timer liveTimer;
    private LocalDate date;
    private String savedText = "";

    WritePanel(DiaryService service, PlaylistRecommender recommender) {
        super(new BorderLayout());
        this.service = service;
        this.results = new ResultPanel(recommender, this::correct);
        setBackground(Theme.PAPER);

        // date bar
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 28, 10, 28));
        JPanel dates = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dates.setOpaque(false);
        dateLabel.setFont(Theme.serif(Font.BOLD, 26));
        dateLabel.setForeground(Theme.INK);
        yearLabel.setFont(Theme.serif(Font.PLAIN, 26));
        yearLabel.setForeground(Theme.FAINT);
        dates.add(dateLabel);
        dates.add(Box.createHorizontalStrut(10));
        dates.add(yearLabel);
        bar.add(dates, BorderLayout.WEST);
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 4));
        nav.setOpaque(false);
        FlatButton prev = new FlatButton("Previous day", FlatButton.Style.QUIET);
        prev.addActionListener(e -> goTo(date.minusDays(1)));
        next.addActionListener(e -> goTo(date.plusDays(1)));
        todayButton.addActionListener(e -> goTo(service.today()));
        nav.add(prev);
        nav.add(next);
        nav.add(todayButton);
        bar.add(nav, BorderLayout.EAST);
        add(bar, BorderLayout.NORTH);

        // the page
        JScrollPane pageScroll = new JScrollPane(page);
        pageScroll.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        pageScroll.getViewport().setBackground(Theme.PAGE);
        pageScroll.getVerticalScrollBar().setUnitIncrement(18);
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setBorder(BorderFactory.createEmptyBorder(0, 28, 20, 10));
        left.add(pageScroll, BorderLayout.CENTER);

        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
        foot.setBorder(BorderFactory.createEmptyBorder(12, 2, 0, 0));
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new javax.swing.BoxLayout(info, javax.swing.BoxLayout.Y_AXIS));
        live.setFont(Theme.sans(Font.PLAIN, 13.5f));
        live.setForeground(Theme.MUTED);
        status.setFont(Theme.sans(Font.PLAIN, 12.5f));
        status.setForeground(Theme.FAINT);
        info.add(live);
        info.add(Box.createVerticalStrut(2));
        info.add(status);
        foot.add(info, BorderLayout.CENTER);
        save.addActionListener(e -> saveEntry());
        foot.add(save, BorderLayout.EAST);
        left.add(foot, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, results);
        split.setResizeWeight(0.56);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(6);
        split.setContinuousLayout(true);
        split.setOpaque(false);
        results.setMinimumSize(new Dimension(330, 200));
        left.setMinimumSize(new Dimension(360, 200));
        add(split, BorderLayout.CENTER);

        liveTimer = new Timer(450, e -> updateLive());
        liveTimer.setRepeats(false);
        page.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { changed(); }
            @Override public void removeUpdate(DocumentEvent e) { changed(); }
            @Override public void changedUpdate(DocumentEvent e) { changed(); }
        });

        load(service.today());
    }

    /** Opens a specific day, asking first if there are unsaved changes. */
    void goTo(LocalDate d) {
        if (d.isAfter(service.today()) || d.equals(date)) {
            return;
        }
        if (hasUnsavedChanges()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Save your changes to " + date.format(HEADING) + " first?", "Unsaved changes",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (choice == JOptionPane.YES_OPTION && !saveEntry()) {
                return;
            }
        }
        load(d);
    }

    boolean hasUnsavedChanges() {
        return !page.getText().strip().equals(savedText.strip());
    }

    /** Saves before the app closes. @return false if the user cancelled */
    boolean confirmClose() {
        if (!hasUnsavedChanges()) {
            return true;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Save today's writing before closing?", "Unsaved changes",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            return saveEntry();
        }
        return choice == JOptionPane.NO_OPTION;
    }

    private void load(LocalDate d) {
        date = d;
        dateLabel.setText(d.format(HEADING));
        yearLabel.setText(Integer.toString(d.getYear()));
        next.setEnabled(d.isBefore(service.today()));
        todayButton.setEnabled(!d.equals(service.today()));
        page.setPlaceholder(PROMPTS.get(Math.floorMod(d.hashCode(), PROMPTS.size())));
        Optional<DiaryEntry> e = service.entryFor(d);
        savedText = e.map(DiaryEntry::text).orElse("");
        page.setText(savedText);
        page.setCaretPosition(0);
        if (e.isPresent()) {
            results.show(e.get(), service.analyze(e.get().text()));
            status.setText("Saved");
        } else {
            results.showEmpty();
            status.setText(" ");
        }
        updateLive();
        page.requestFocusInWindow();
    }

    private void changed() {
        liveTimer.restart();
        status.setText(hasUnsavedChanges() ? "Not saved yet" : "Saved");
    }

    private void updateLive() {
        String text = page.getText();
        int words = text.isBlank() ? 0 : text.strip().split("\\s+").length;
        if (words == 0) {
            live.setText("The page will pick up your mood as you write.");
            page.setMarginColor(Theme.tint(Theme.FAINT, 0.35));
            return;
        }
        MoodAnalysis a = service.analyze(text);
        String count = words + (words == 1 ? " word" : " words");
        if (a.lowSignal()) {
            live.setText("Keep going, nothing stands out yet. " + count);
            page.setMarginColor(Theme.tint(Theme.FAINT, 0.35));
        } else {
            Mood m = a.primary();
            live.setText("Sounds like " + m.displayName().toLowerCase() + " so far. " + count);
            page.setMarginColor(Theme.mood(m));
        }
    }

    private boolean saveEntry() {
        try {
            DiaryEntry e = service.write(date, page.getText());
            savedText = e.text();
            results.show(e, service.analyze(e.text()));
            status.setText("Saved");
            liveTimer.stop();
            updateLive();
            page.setMarginColor(Theme.mood(e.mood()));
            return true;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Nothing to save", JOptionPane.INFORMATION_MESSAGE);
        } catch (StorageException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Couldn't save", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void correct(Mood m) {
        try {
            if (hasUnsavedChanges() && !saveEntry()) {
                return;
            }
            DiaryEntry e = service.correctMood(date, m);
            results.show(e, service.analyze(e.text()));
            page.setMarginColor(Theme.mood(m));
            status.setText("Saved. The diary will remember this.");
        } catch (IllegalArgumentException | StorageException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Couldn't change the mood", JOptionPane.ERROR_MESSAGE);
        }
    }
}
