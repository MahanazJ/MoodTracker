package com.mooddiary.ui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import javax.swing.JOptionPane;

/** Opens links in the browser, or copies them if the system can't open a browser. */
final class Links {

    private Links() { }

    static void open(URI uri, Component parent) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
                return;
            }
        } catch (Exception ignored) {
            // fall through to the clipboard
        }
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uri.toString()), null);
            JOptionPane.showMessageDialog(parent, "Your browser couldn't be opened, so the link was copied:\n" + uri,
                    "Link copied", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Open this link in your browser:\n" + uri, "Link",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
