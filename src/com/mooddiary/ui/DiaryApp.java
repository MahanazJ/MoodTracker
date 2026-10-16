package com.mooddiary.ui;

import com.mooddiary.diary.DiaryService;
import com.mooddiary.recommendation.PlaylistRecommender;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** The whole app: a header with three pages (Write, Journal, Mood chart). */
public final class DiaryApp extends JPanel {

    private static final long serialVersionUID = 1L;

    private final CardLayout cards = new CardLayout();
    private final JPanel pages = new JPanel(cards);
    private final WritePanel write;
    private final ChartPanel chart;
    private final FlatButton writeTab = new FlatButton("Write", FlatButton.Style.TAB);
    private final FlatButton journalTab = new FlatButton("Journal", FlatButton.Style.TAB);
    private final FlatButton chartTab = new FlatButton("Mood chart", FlatButton.Style.TAB);

    public DiaryApp(DiaryService service, PlaylistRecommender recommender) {
        super(new BorderLayout());
        setBackground(Theme.PAPER);

        write = new WritePanel(service, recommender);
        JournalPanel journal = new JournalPanel(service, this::openDay);
        chart = new ChartPanel(service, this::openDay);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PAPER);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE),
                BorderFactory.createEmptyBorder(10, 28, 0, 20)));
        JLabel brand = new JLabel("Mood Diary");
        brand.setFont(Theme.serif(Font.BOLD, 19));
        brand.setForeground(Theme.INK);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        header.add(brand, BorderLayout.WEST);
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        tabs.setOpaque(false);
        tabs.add(writeTab);
        tabs.add(journalTab);
        tabs.add(chartTab);
        header.add(tabs, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        pages.add(write, "write");
        pages.add(journal, "journal");
        pages.add(chart, "chart");
        add(pages, BorderLayout.CENTER);

        writeTab.addActionListener(e -> show("write"));
        journalTab.addActionListener(e -> show("journal"));
        chartTab.addActionListener(e -> show("chart"));
        show("write");
    }

    /** Called before the window closes. @return false to keep the window open */
    public boolean confirmClose() {
        return write.confirmClose();
    }

    void show(String page) {
        cards.show(pages, page);
        for (var pair : List.of(List.of(writeTab, "write"), List.of(journalTab, "journal"), List.of(chartTab, "chart"))) {
            ((FlatButton) pair.get(0)).setTabSelected(pair.get(1).equals(page));
        }
        if (page.equals("chart")) {
            chart.refresh();
        }
    }

    private void openDay(LocalDate date) {
        show("write");
        write.goTo(date);
    }
}
