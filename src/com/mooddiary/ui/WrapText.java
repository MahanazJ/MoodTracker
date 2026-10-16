package com.mooddiary.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/** Read-only wrapping text for use inside layouts. */
final class WrapText extends JTextArea {

    private static final long serialVersionUID = 1L;

    WrapText(String text, Font font, Color color) {
        super(text);
        setFont(font);
        setForeground(color);
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setFocusable(false);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        setAlignmentX(LEFT_ALIGNMENT);
    }

    /** Height for the width we'll actually get, so wrapped lines are never clipped. */
    @Override
    public java.awt.Dimension getPreferredSize() {
        int w = getWidth();
        if (w <= 0 && getParent() != null) {
            java.awt.Insets in = getParent().getInsets();
            w = getParent().getWidth() - in.left - in.right;
        }
        if (w <= 0) {
            return super.getPreferredSize();
        }
        javax.swing.text.View root = getUI().getRootView(this);
        root.setSize(w, Integer.MAX_VALUE);
        int h = (int) Math.ceil(root.getPreferredSpan(javax.swing.text.View.Y_AXIS));
        java.awt.Insets in = getInsets();
        return new java.awt.Dimension(w, h + in.top + in.bottom);
    }

    @Override
    public java.awt.Dimension getMaximumSize() {
        return new java.awt.Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}
