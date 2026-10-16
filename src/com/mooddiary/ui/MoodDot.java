package com.mooddiary.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/** A filled circle in a mood's color. */
final class MoodDot extends JComponent {

    private static final long serialVersionUID = 1L;
    private Color color;
    private final int size;

    MoodDot(Color color, int size) {
        this.color = color;
        this.size = size;
        Dimension d = new Dimension(size, size);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    void setColor(Color c) {
        color = c;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Theme.tint(color, 0.78));
        g.fillOval(0, 0, size - 1, size - 1);
        int inner = (int) (size * 0.62);
        int off = (size - inner) / 2;
        g.setColor(color);
        g.fillOval(off, off, inner, inner);
        g.dispose();
    }
}
