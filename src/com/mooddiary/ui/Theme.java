package com.mooddiary.ui;

import com.mooddiary.model.Mood;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/** Colors and type for the diary. Mood colors are the only bright accents. */
final class Theme {

    static final Color PAPER = new Color(0xF2F3F7);
    static final Color PAGE = Color.WHITE;
    static final Color INK = new Color(0x1E2433);
    static final Color MUTED = new Color(0x6B7285);
    static final Color FAINT = new Color(0x9AA0B2);
    static final Color RULE = new Color(0xE4E7EF);
    static final Color LINE = new Color(0xD5D9E4);
    static final Color ACCENT = new Color(0x3D4B8F);
    static final Color ACCENT_HOVER = new Color(0x2F3B75);
    static final Color ACCENT_SOFT = new Color(0xE6E9F5);
    static final Color SUPPORT = new Color(0xFFF4E5);
    static final Color SUPPORT_EDGE = new Color(0xE8B86A);

    private static final Map<Mood, Color> MOOD_COLORS = new EnumMap<>(Mood.class);

    static {
        MOOD_COLORS.put(Mood.HAPPY, new Color(0xE9B400));
        MOOD_COLORS.put(Mood.EXCITED, new Color(0xF27A1A));
        MOOD_COLORS.put(Mood.GRATEFUL, new Color(0xC98B2E));
        MOOD_COLORS.put(Mood.LOVED, new Color(0xE0527E));
        MOOD_COLORS.put(Mood.CONFIDENT, new Color(0x8E5BD6));
        MOOD_COLORS.put(Mood.HOPEFUL, new Color(0x5DB06A));
        MOOD_COLORS.put(Mood.CALM, new Color(0x3AA6A0));
        MOOD_COLORS.put(Mood.NOSTALGIC, new Color(0xB38B6D));
        MOOD_COLORS.put(Mood.BORED, new Color(0x9A9CA6));
        MOOD_COLORS.put(Mood.TIRED, new Color(0x7D8BA6));
        MOOD_COLORS.put(Mood.STRESSED, new Color(0xE2584D));
        MOOD_COLORS.put(Mood.ANXIOUS, new Color(0xB0607A));
        MOOD_COLORS.put(Mood.ANGRY, new Color(0xB8202A));
        MOOD_COLORS.put(Mood.LONELY, new Color(0x5B76D8));
        MOOD_COLORS.put(Mood.SAD, new Color(0x2F69A3));
        MOOD_COLORS.put(Mood.HEARTBROKEN, new Color(0x6C2D74));
    }

    private static final String SERIF = pick("Georgia", "Charter", "Cambria", "Palatino Linotype", "DejaVu Serif", Font.SERIF);
    private static final String SANS = pick("Segoe UI", "SF Pro Text", "Helvetica Neue", "Inter", "DejaVu Sans", Font.SANS_SERIF);

    private Theme() { }

    static Color mood(Mood m) {
        return MOOD_COLORS.get(m);
    }

    static Color tint(Color c, double amount) {
        int r = (int) Math.round(c.getRed() + (255 - c.getRed()) * amount);
        int g = (int) Math.round(c.getGreen() + (255 - c.getGreen()) * amount);
        int b = (int) Math.round(c.getBlue() + (255 - c.getBlue()) * amount);
        return new Color(r, g, b);
    }

    static Font serif(int style, float size) {
        return new Font(SERIF, style, Math.round(size));
    }

    static Font sans(int style, float size) {
        return new Font(SANS, style, Math.round(size));
    }

    private static String pick(String... families) {
        Set<String> available = Set.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String f : families) {
            if (available.contains(f)) {
                return f;
            }
        }
        return families[families.length - 1];
    }
}
