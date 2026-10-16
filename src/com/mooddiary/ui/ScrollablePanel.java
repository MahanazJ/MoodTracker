package com.mooddiary.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/** A panel that tracks the viewport width, so wrapped text reflows when resized. */
final class ScrollablePanel extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;

    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return r.height - 40; }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }

    /** When the width changes, wrapped text needs a different height: lay out again. */
    @Override
    public void setBounds(int x, int y, int w, int h) {
        boolean widthChanged = w != getWidth();
        super.setBounds(x, y, w, h);
        if (widthChanged) {
            invalidateTree(this);
            javax.swing.SwingUtilities.invokeLater(this::revalidate);
        }
    }

    private static void invalidateTree(java.awt.Container c) {
        c.invalidate();
        for (java.awt.Component k : c.getComponents()) {
            if (k instanceof java.awt.Container cc) {
                invalidateTree(cc);
            } else {
                k.invalidate();
            }
        }
    }
}
