package com.mooddiary.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * One day's diary page: what the user wrote plus the mood it was read as.
 *
 * <p>{@code detectedMood} is what the analyzer found; {@code mood} is the mood
 * in effect, which differs only when the user corrected it.
 */
public record DiaryEntry(LocalDate date,
                         String text,
                         Mood mood,
                         Mood detectedMood,
                         Mood secondaryMood,      // nullable
                         boolean corrected,
                         int intensity,
                         double confidence,
                         double valence,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt) {

    public static final int MAX_TEXT_LENGTH = 100_000;

    public DiaryEntry {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(mood, "mood");
        Objects.requireNonNull(detectedMood, "detectedMood");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(updatedAt, "updatedAt");
        if (text.length() > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException("Entry is too long (max " + MAX_TEXT_LENGTH + " characters)");
        }
        if (intensity < 1 || intensity > 10) {
            throw new IllegalArgumentException("Intensity must be 1-10, got " + intensity);
        }
        confidence = clamp(confidence, 0, 1);
        valence = clamp(valence, -1, 1);
    }

    public Optional<Mood> secondary() {
        return Optional.ofNullable(secondaryMood);
    }

    /** A copy with the mood set by the user. */
    public DiaryEntry withCorrectedMood(Mood actual, LocalDateTime when) {
        double v = actual.valence() * (0.55 + 0.45 * intensity / 10.0);
        return new DiaryEntry(date, text, actual, detectedMood, secondaryMood,
                actual != detectedMood, intensity, confidence, v, createdAt, when);
    }

    /** First line or so of the entry, for list views. */
    public String snippet(int maxChars) {
        String flat = text.strip().replaceAll("\\s+", " ");
        return flat.length() <= maxChars ? flat : flat.substring(0, Math.max(0, maxChars - 1)).strip() + "…";
    }

    private static double clamp(double v, double lo, double hi) {
        if (Double.isNaN(v)) {
            return lo;
        }
        return Math.max(lo, Math.min(hi, v));
    }
}
