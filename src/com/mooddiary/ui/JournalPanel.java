package com.mooddiary.ui;

import com.mooddiary.diary.DiaryService;
import com.mooddiary.model.DiaryEntry;
import com.mooddiary.model.Mood;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

/** Every saved page, newest first, with a reader on the right. */
final class JournalPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter LIST_DATE = DateTimeFormatter.ofPattern("EEE, MMM d yyyy");
    private static final DateTimeFormatter READ_DATE = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static final String ALL = "All moods";

    private final DiaryService service;
    private final DefaultListModel<DiaryEntry> model = new DefaultListModel<>();
    private final JList<DiaryEntry> list = new JList<>(model);
    private final JComboBox<Object> filter;
    private final JLabel readDate = new JLabel(" ");
    private final JLabel readMood = new JLabel(" ");
    private final MoodDot readDot = new MoodDot(Theme.FAINT, 14);
    private final DiaryPage reader = new DiaryPage();
    private final FlatButton edit = new FlatButton("Edit this page", FlatButton.Style.SECONDARY);
    private final FlatButton delete = new FlatButton("Delete", FlatButton.Style.QUIET);
    private final JLabel empty = new JLabel("Your saved pages will be listed here.");

    JournalPanel(DiaryService service, Consumer<LocalDate> onEdit) {
        super(new BorderLayout());
        this.service = service;
        setBackground(Theme.PAPER);
        setBorder(BorderFactory.createEmptyBorder(14, 28, 22, 28));

        List<Object> options = new ArrayList<>();
        options.add(ALL);
        options.addAll(List.of(Mood.values()));
        filter = new JComboBox<>(options.toArray());
        filter.setFont(Theme.sans(Font.PLAIN, 13.5f));
        filter.addActionListener(e -> reload());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        JLabel title = new JLabel("Journal");
        title.setFont(Theme.serif(Font.BOLD, 26));
        title.setForeground(Theme.INK);
        top.add(title, BorderLayout.WEST);
        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        f.setOpaque(false);
        JLabel show = new JLabel("Show");
        show.setFont(Theme.sans(Font.PLAIN, 13.5f));
        show.setForeground(Theme.MUTED);
        f.add(show);
        f.add(filter);
        top.add(f, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new EntryRenderer());
        list.setBackground(Theme.PAGE);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelected();
            }
        });
        JScrollPane listScroll = new JScrollPane(list);
        listScroll.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        listScroll.getVerticalScrollBar().setUnitIncrement(18);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new javax.swing.BoxLayout(head, javax.swing.BoxLayout.Y_AXIS));
        readDate.setFont(Theme.serif(Font.BOLD, 22));
        readDate.setForeground(Theme.INK);
        readMood.setFont(Theme.sans(Font.PLAIN, 14));
        readMood.setForeground(Theme.MUTED);
        JPanel moodLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        moodLine.setOpaque(false);
        moodLine.add(readDot);
        moodLine.add(Box.createHorizontalStrut(8));
        moodLine.add(readMood);
        readDate.setAlignmentX(LEFT_ALIGNMENT);
        moodLine.setAlignmentX(LEFT_ALIGNMENT);
        head.add(readDate);
        head.add(Box.createVerticalStrut(4));
        head.add(moodLine);
        head.add(Box.createVerticalStrut(10));
        right.add(head, BorderLayout.NORTH);
        reader.setEditable(false);
        JScrollPane readScroll = new JScrollPane(reader);
        readScroll.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        right.add(readScroll, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        edit.addActionListener(e -> {
            DiaryEntry s = list.getSelectedValue();
            if (s != null) {
                onEdit.accept(s.date());
            }
        });
        delete.addActionListener(e -> deleteSelected());
        actions.add(edit);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(delete);
        right.add(actions, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, right);
        split.setResizeWeight(0.36);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(6);
        split.setOpaque(false);
        listScroll.setMinimumSize(new Dimension(260, 100));
        add(split, BorderLayout.CENTER);

        empty.setFont(Theme.serif(Font.ITALIC, 16));
        empty.setForeground(Theme.MUTED);

        service.addChangeListener(this::reload);
        reload();
    }

    void reload() {
        DiaryEntry selected = list.getSelectedValue();
        Object f = filter.getSelectedItem();
        model.clear();
        service.allEntries().stream()
                .filter(e -> !(f instanceof Mood m) || e.mood() == m)
                .sorted(Comparator.comparing(DiaryEntry::date).reversed())
                .forEach(model::addElement);
        if (selected != null) {
            for (int i = 0; i < model.size(); i++) {
                if (model.get(i).date().equals(selected.date())) {
                    list.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (list.getSelectedIndex() < 0 && !model.isEmpty()) {
            list.setSelectedIndex(0);
        }
        showSelected();
    }

    private void showSelected() {
        DiaryEntry e = list.getSelectedValue();
        edit.setEnabled(e != null);
        delete.setEnabled(e != null);
        if (e == null) {
            readDate.setText(model.isEmpty() ? "No pages yet" : " ");
            readMood.setText(model.isEmpty() ? "Write your first entry on the Write page." : " ");
            readDot.setColor(Theme.FAINT);
            reader.setText("");
            reader.setMarginColor(Theme.tint(Theme.FAINT, 0.35));
            return;
        }
        readDate.setText(e.date().format(READ_DATE));
        String sec = e.secondary().filter(s -> !e.corrected()).map(s -> ", with some " + s.displayName().toLowerCase()).orElse("");
        readMood.setText(e.mood().displayName() + sec + ". Intensity " + e.intensity() + " of 10.");
        readDot.setColor(Theme.mood(e.mood()));
        reader.setText(e.text());
        reader.setCaretPosition(0);
        reader.setMarginColor(Theme.mood(e.mood()));
    }

    private void deleteSelected() {
        DiaryEntry e = list.getSelectedValue();
        if (e == null) {
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Delete the page for " + e.date().format(READ_DATE)
                + "? This can't be undone.", "Delete page", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            service.delete(e.date());
        }
    }

    private static final class EntryRenderer extends JPanel implements ListCellRenderer<DiaryEntry> {
        private static final long serialVersionUID = 1L;
        private final MoodDot dot = new MoodDot(Theme.FAINT, 12);
        private final JLabel date = new JLabel();
        private final JLabel mood = new JLabel();
        private final JLabel snippet = new JLabel();

        EntryRenderer() {
            setLayout(new BorderLayout(10, 0));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.RULE),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            date.setFont(Theme.serif(Font.BOLD, 15));
            date.setForeground(Theme.INK);
            mood.setFont(Theme.sans(Font.PLAIN, 13));
            mood.setForeground(Theme.MUTED);
            snippet.setFont(Theme.sans(Font.PLAIN, 13));
            snippet.setForeground(Theme.MUTED);
            JPanel dotWrap = new JPanel(new BorderLayout());
            dotWrap.setOpaque(false);
            dotWrap.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
            dotWrap.add(dot, BorderLayout.NORTH);
            JPanel lines = new JPanel(new BorderLayout(0, 2));
            lines.setOpaque(false);
            JPanel first = new JPanel(new BorderLayout());
            first.setOpaque(false);
            first.add(date, BorderLayout.WEST);
            first.add(mood, BorderLayout.EAST);
            lines.add(first, BorderLayout.NORTH);
            lines.add(snippet, BorderLayout.SOUTH);
            add(dotWrap, BorderLayout.WEST);
            add(lines, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends DiaryEntry> l, DiaryEntry e, int i,
                                                      boolean selected, boolean focus) {
            dot.setColor(Theme.mood(e.mood()));
            date.setText(e.date().format(LIST_DATE));
            mood.setText(e.mood().displayName());
            snippet.setText(e.snippet(54));
            setBackground(selected ? Theme.ACCENT_SOFT : Theme.PAGE);
            setOpaque(true);
            return this;
        }
    }
}
