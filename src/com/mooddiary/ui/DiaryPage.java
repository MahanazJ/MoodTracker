package com.mooddiary.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * A ruled notebook page. The margin line takes on the color of the mood being
 * detected, so the page quietly reflects what you're writing.
 */
final class DiaryPage extends JTextArea {

    private static final long serialVersionUID = 1L;
    private static final int MARGIN_X = 44;

    private Color marginColor = Theme.tint(Theme.FAINT, 0.35);
    private String placeholder = "";

    DiaryPage() {
        setFont(Theme.serif(java.awt.Font.PLAIN, 17));
        setForeground(Theme.INK);
        setCaretColor(Theme.INK);
        setSelectionColor(Theme.tint(Theme.ACCENT, 0.75));
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(22, MARGIN_X + 18, 28, 28));
    }

    void setMarginColor(Color c) {
        marginColor = c;
        repaint();
    }

    void setPlaceholder(String text) {
        placeholder = text;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setColor(Theme.PAGE);
        g.fillRect(0, 0, getWidth(), getHeight());

        FontMetrics fm = getFontMetrics(getFont());
        Insets in = getInsets();
        int lineH = fm.getHeight();
        g.setColor(Theme.RULE);
        for (int y = in.top + fm.getAscent() + fm.getDescent() / 2 + 2; y < getHeight(); y += lineH) {
            g.drawLine(MARGIN_X + 6, y, getWidth() - 14, y);
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(marginColor);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(MARGIN_X, 0, MARGIN_X, getHeight());

        if (getDocument().getLength() == 0 && !placeholder.isEmpty()) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(getFont().deriveFont(java.awt.Font.ITALIC));
            g.setColor(Theme.FAINT);
            g.drawString(placeholder, in.left, in.top + fm.getAscent());
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
