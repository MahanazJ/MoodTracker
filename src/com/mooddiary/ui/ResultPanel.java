package com.mooddiary.ui;

import com.mooddiary.analysis.Evidence;
import com.mooddiary.analysis.MoodAnalysis;
import com.mooddiary.model.DiaryEntry;
import com.mooddiary.model.Mood;
import com.mooddiary.model.Track;
import com.mooddiary.recommendation.PlaylistRecommender;
import com.mooddiary.recommendation.Recommendation;
import com.mooddiary.recommendation.RecommendationStrategy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/** Right-hand side of the Write page: what the diary read in your entry, and the songs for it. */
final class ResultPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final PlaylistRecommender recommender;
    private final Consumer<Mood> onCorrect;
    private final ScrollablePanel content = new ScrollablePanel();
    private DiaryEntry entry;
    private MoodAnalysis analysis;

    ResultPanel(PlaylistRecommender recommender, Consumer<Mood> onCorrect) {
        super(new BorderLayout());
        this.recommender = recommender;
        this.onCorrect = onCorrect;
        setBackground(Theme.PAPER);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.PAPER);
        content.setBorder(BorderFactory.createEmptyBorder(20, 26, 26, 26));
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Theme.PAPER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);
        showEmpty();
    }

    void showEmpty() {
        entry = null;
        analysis = null;
        content.removeAll();
        content.add(Box.createVerticalStrut(40));
        content.add(new WrapText("Your songs will appear here", Theme.serif(Font.PLAIN, 24), Theme.INK));
        content.add(Box.createVerticalStrut(10));
        content.add(new WrapText("Write about your day on the left, as much or as little as you like. When you save, "
                + "the diary reads what you wrote, works out how you're feeling, and picks twelve songs to match.",
                Theme.sans(Font.PLAIN, 14), Theme.MUTED));
        refresh();
    }

    void show(DiaryEntry e, MoodAnalysis a) {
        entry = e;
        analysis = a;
        rebuild();
    }

    private void rebuild() {
        content.removeAll();
        Mood mood = entry.mood();

        // Header: mood and how sure the diary is
        JPanel header = row();
        header.setLayout(new BorderLayout(14, 0));
        MoodDot dot = new MoodDot(Theme.mood(mood), 46);
        JPanel dotWrap = row();
        dotWrap.setLayout(new BorderLayout());
        dotWrap.add(dot, BorderLayout.NORTH);
        header.add(dotWrap, BorderLayout.WEST);
        JPanel titles = row();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(mood.displayName());
        name.setFont(Theme.serif(Font.BOLD, 30));
        name.setForeground(Theme.INK);
        titles.add(name);
        String meta = entry.corrected()
                ? "You chose this mood. I'd read it as " + entry.detectedMood().displayName().toLowerCase() + "."
                : Math.round(entry.confidence() * 100) + "% sure, intensity " + entry.intensity() + " of 10";
        titles.add(label(meta, Theme.sans(Font.PLAIN, 13.5f), Theme.MUTED));
        header.add(titles, BorderLayout.CENTER);
        add(header);
        gap(8);

        String sub = mood.description().substring(0, 1).toUpperCase() + mood.description().substring(1) + ".";
        if (!entry.corrected() && entry.secondary().isPresent()) {
            sub += " With some " + entry.secondary().get().displayName().toLowerCase() + " in there too.";
        }
        add(new WrapText(sub, Theme.serif(Font.ITALIC, 15), Theme.MUTED));
        gap(14);

        if (analysis.supportSuggested()) {
            add(supportCard());
            gap(16);
        }
        if (analysis.lowSignal() && !entry.corrected()) {
            add(new WrapText("I didn't find many feelings in this entry, so this is a loose guess. A sentence or two about "
                    + "how the day felt helps me read it better.", Theme.sans(Font.PLAIN, 13), Theme.MUTED));
            gap(14);
        }

        // Mood mix
        add(heading("Mood mix"));
        gap(6);
        add(new MixBars(analysis.ranked()));
        gap(18);

        // Why
        if (!analysis.evidence().isEmpty()) {
            add(heading("Why I think so"));
            gap(6);
            for (Evidence ev : analysis.evidence().subList(0, Math.min(7, analysis.evidence().size()))) {
                add(evidenceRow(ev));
                gap(4);
            }
            gap(14);
        }

        add(correctionRow());
        gap(24);
        add(divider());
        gap(18);
        addPlaylist();
        refresh();
    }

    private void addPlaylist() {
        Recommendation rec = recommender.recommend(entry);
        JPanel styles = row();
        styles.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        List<FlatButton> tabs = new ArrayList<>();
        for (RecommendationStrategy s : recommender.strategies()) {
            FlatButton b = new FlatButton(s.name(), FlatButton.Style.TAB);
            b.setFont(Theme.sans(Font.PLAIN, 13.5f));
            b.setTabSelected(s == recommender.active());
            b.setToolTipText(s.description());
            b.addActionListener(ev -> {
                recommender.setActive(s);
                rebuild();
            });
            tabs.add(b);
            styles.add(b);
        }
        add(styles);
        gap(12);

        JLabel title = new JLabel(rec.playlist().name());
        title.setFont(Theme.serif(Font.BOLD, 21));
        title.setForeground(Theme.INK);
        add(title);
        gap(3);
        add(new WrapText(rec.reason(), Theme.sans(Font.PLAIN, 13.5f), Theme.MUTED));
        gap(10);

        JList<Track> list = new JList<>(rec.playlist().tracks().toArray(new Track[0]));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new TrackRenderer());
        list.setBackground(Theme.PAGE);
        list.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        list.setSelectedIndex(0);
        list.setToolTipText("Double-click a song to play it on Spotify");
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && list.getSelectedValue() != null) {
                    open(spotify(list.getSelectedValue()));
                }
            }
        });
        JPanel listWrap = row();
        listWrap.setLayout(new BorderLayout());
        listWrap.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        listWrap.add(list, BorderLayout.CENTER);
        add(listWrap);
        gap(12);

        JPanel buttons = row();
        buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        FlatButton spotify = new FlatButton("Play on Spotify", FlatButton.Style.PRIMARY);
        spotify.addActionListener(ev -> open(spotify(list.getSelectedValue())));
        FlatButton youtube = new FlatButton("Play on YouTube", FlatButton.Style.SECONDARY);
        youtube.addActionListener(ev -> open(youtube(list.getSelectedValue())));
        buttons.add(spotify);
        buttons.add(Box.createHorizontalStrut(8));
        buttons.add(youtube);
        add(buttons);
    }

    private JComponent correctionRow() {
        JPanel p = row();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.add(label("Not quite right? I'm feeling ", Theme.sans(Font.PLAIN, 13.5f), Theme.INK));
        JComboBox<Mood> choice = new JComboBox<>(Mood.values());
        choice.setSelectedItem(entry.mood());
        choice.setFont(Theme.sans(Font.PLAIN, 13.5f));
        choice.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean f) {
                JLabel c = (JLabel) super.getListCellRendererComponent(l, v, i, sel, f);
                if (v instanceof Mood m) {
                    c.setIcon(new DotIcon(Theme.mood(m), 10));
                    c.setIconTextGap(8);
                }
                return c;
            }
        });
        p.add(choice);
        p.add(Box.createHorizontalStrut(8));
        FlatButton use = new FlatButton("Use this mood", FlatButton.Style.SECONDARY);
        use.addActionListener(ev -> {
            Mood m = (Mood) choice.getSelectedItem();
            if (m != null && m != entry.mood()) {
                onCorrect.accept(m);
            }
        });
        p.add(use);
        p.setToolTipText("Corrections teach the diary what your words mean to you");
        return p;
    }

    private JComponent supportCard() {
        JPanel card = row();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(Theme.SUPPORT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.SUPPORT_EDGE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        WrapText t1 = new WrapText("It sounds like you're carrying something really heavy right now. You don't have to "
                + "hold it alone, and talking to someone can help.", Theme.sans(Font.BOLD, 13.5f), Theme.INK);
        WrapText t2 = new WrapText("In the US you can call or text 988 to reach the Suicide & Crisis Lifeline, any time. "
                + "Outside the US, findahelpline.com lists free support lines in your country. If you're in immediate "
                + "danger, call your local emergency number.", Theme.sans(Font.PLAIN, 13.5f), Theme.INK);
        card.add(t1);
        card.add(Box.createVerticalStrut(6));
        card.add(t2);
        card.add(Box.createVerticalStrut(8));
        FlatButton link = new FlatButton("Find a helpline", FlatButton.Style.SECONDARY);
        link.setAlignmentX(LEFT_ALIGNMENT);
        link.addActionListener(e -> open(URI.create("https://findahelpline.com")));
        card.add(link);
        return card;
    }

    private JComponent evidenceRow(Evidence ev) {
        JPanel r = row();
        r.setLayout(new BorderLayout(8, 0));
        JPanel dotWrap = row();
        dotWrap.setLayout(new BorderLayout());
        dotWrap.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        dotWrap.add(new MoodDot(Theme.mood(ev.mood()), 10), BorderLayout.NORTH);
        r.add(dotWrap, BorderLayout.WEST);
        String text = "\u201C" + ev.phrase() + "\u201D  " + ev.mood().displayName().toLowerCase()
                + (ev.note().isEmpty() ? "" : ", " + ev.note());
        r.add(new WrapText(text, Theme.sans(Font.PLAIN, 13.5f), Theme.INK), BorderLayout.CENTER);
        return r;
    }

    // ------------------------------------------------------------ small helpers

    private void add(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        content.add(c);
    }

    private void gap(int h) {
        content.add(Box.createVerticalStrut(h));
    }

    private void refresh() {
        content.revalidate();
        content.repaint();
    }

    private static JPanel row() {
        JPanel p = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private static JLabel label(String text, Font f, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    private static JLabel heading(String text) {
        return label(text, Theme.sans(Font.BOLD, 13.5f), Theme.INK);
    }

    private static JComponent divider() {
        JComponent d = new JComponent() {
            private static final long serialVersionUID = 1L;
            @Override protected void paintComponent(Graphics g) {
                g.setColor(Theme.LINE);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        d.setPreferredSize(new Dimension(10, 1));
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return d;
    }

    private static URI spotify(Track t) {
        return URI.create("https://open.spotify.com/search/" + encode(t.title() + " " + t.artist()));
    }

    private static URI youtube(Track t) {
        return URI.create("https://www.youtube.com/results?search_query=" + encode(t.title() + " " + t.artist()));
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private void open(URI uri) {
        Links.open(uri, this);
    }

    /** One song in the playlist. */
    private static final class TrackRenderer extends JPanel implements javax.swing.ListCellRenderer<Track> {
        private static final long serialVersionUID = 1L;
        private final JLabel num = new JLabel();
        private final JLabel title = new JLabel();
        private final JLabel artist = new JLabel();

        TrackRenderer() {
            setLayout(new BorderLayout(10, 0));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            num.setFont(Theme.sans(Font.PLAIN, 12.5f));
            num.setForeground(Theme.FAINT);
            num.setPreferredSize(new Dimension(22, 10));
            title.setFont(Theme.sans(Font.PLAIN, 14));
            title.setForeground(Theme.INK);
            artist.setFont(Theme.sans(Font.PLAIN, 13));
            artist.setForeground(Theme.MUTED);
            JPanel text = new JPanel(new BorderLayout());
            text.setOpaque(false);
            text.add(title, BorderLayout.NORTH);
            text.add(artist, BorderLayout.SOUTH);
            add(num, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Track> list, Track t, int index,
                                                      boolean selected, boolean focus) {
            num.setText(Integer.toString(index + 1));
            title.setText(t.title());
            artist.setText(t.artist());
            setBackground(selected ? Theme.ACCENT_SOFT : Theme.PAGE);
            setOpaque(true);
            return this;
        }
    }

    /** Horizontal bars showing how much of the entry pointed to each mood. */
    private static final class MixBars extends JComponent {
        private static final long serialVersionUID = 1L;
        private final List<Map.Entry<Mood, Double>> rows;

        MixBars(List<Map.Entry<Mood, Double>> ranked) {
            this.rows = ranked.stream().filter(e -> e.getValue() >= 0.04).limit(5).toList();
            setPreferredSize(new Dimension(200, Math.max(1, rows.size()) * 24));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(1, rows.size()) * 24));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(Theme.sans(Font.PLAIN, 13));
            FontMetrics fm = g.getFontMetrics();
            int labelW = 96;
            int pctW = 42;
            int barMax = Math.max(40, getWidth() - labelW - pctW);
            if (rows.isEmpty()) {
                g.setColor(Theme.MUTED);
                g.drawString("No strong feelings found", 0, fm.getAscent() + 2);
            }
            for (int i = 0; i < rows.size(); i++) {
                Mood m = rows.get(i).getKey();
                double share = rows.get(i).getValue();
                int y = i * 24;
                g.setColor(Theme.INK);
                g.drawString(m.displayName(), 0, y + fm.getAscent() + 2);
                g.setColor(Theme.tint(Theme.mood(m), 0.82));
                g.fillRoundRect(labelW, y + 5, barMax, 10, 10, 10);
                g.setColor(Theme.mood(m));
                g.fillRoundRect(labelW, y + 5, Math.max(10, (int) (barMax * share)), 10, 10, 10);
                g.setColor(Theme.MUTED);
                String pct = Math.round(share * 100) + "%";
                g.drawString(pct, getWidth() - fm.stringWidth(pct), y + fm.getAscent() + 2);
            }
            g.dispose();
        }
    }
}
