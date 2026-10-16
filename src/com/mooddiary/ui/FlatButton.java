package com.mooddiary.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/** A button that looks the same on every platform. */
final class FlatButton extends JButton {

    private static final long serialVersionUID = 1L;

    enum Style { PRIMARY, SECONDARY, QUIET, TAB }

    private final Style style;
    private boolean hover;
    private boolean selected;

    FlatButton(String text, Style style) {
        super(text);
        this.style = style;
        setFont(Theme.sans(style == Style.PRIMARY ? java.awt.Font.BOLD : java.awt.Font.PLAIN, style == Style.TAB ? 15 : 13.5f));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    void setTabSelected(boolean s) {
        selected = s;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int w = fm.stringWidth(getText()) + (style == Style.TAB ? 24 : style == Style.QUIET ? 12 : 32);
        int h = fm.getHeight() + (style == Style.TAB ? 16 : 14);
        return new Dimension(w, h);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        boolean enabled = isEnabled();
        Color fg;
        switch (style) {
            case PRIMARY -> {
                g.setColor(!enabled ? Theme.LINE : hover ? Theme.ACCENT_HOVER : Theme.ACCENT);
                g.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
                fg = enabled ? Color.WHITE : Theme.MUTED;
            }
            case SECONDARY -> {
                g.setColor(hover && enabled ? Theme.ACCENT_SOFT : Theme.PAGE);
                g.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
                g.setColor(Theme.LINE);
                g.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
                fg = enabled ? Theme.INK : Theme.FAINT;
            }
            case TAB -> {
                fg = selected ? Theme.INK : hover ? Theme.INK : Theme.MUTED;
                if (selected) {
                    g.setColor(Theme.ACCENT);
                    g.fillRoundRect(10, h - 4, w - 20, 3, 3, 3);
                }
            }
            default -> fg = !enabled ? Theme.FAINT : hover ? Theme.ACCENT_HOVER : Theme.ACCENT;
        }
        g.setFont(getFont());
        g.setColor(fg);
        FontMetrics fm = g.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent() - (style == Style.TAB ? 2 : 0);
        g.drawString(getText(), tx, ty);
        if (isFocusOwner() && style != Style.TAB) {
            g.setColor(Theme.tint(Theme.ACCENT, 0.4));
            g.drawRoundRect(1, 1, w - 3, h - 3, 9, 9);
        }
        g.dispose();
    }
}
