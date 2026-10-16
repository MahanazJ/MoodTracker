package com.mooddiary.diary;

import com.mooddiary.model.DiaryEntry;
import com.mooddiary.model.Mood;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Summary of a period of diary entries. */
public final class MoodStatistics {

    private final int entries;
    private final Map<Mood, Integer> counts;
    private final double averageValence;
    private final Mood mostCommon;
    private final int currentStreak;
    private final int longestStreak;

    MoodStatistics(List<DiaryEntry> list, int currentStreak, int longestStreak) {
        Map<Mood, Integer> c = new EnumMap<>(Mood.class);
        double sum = 0;
        for (DiaryEntry e : list) {
            c.merge(e.mood(), 1, Integer::sum);
            sum += e.valence();
        }
        this.entries = list.size();
        this.counts = Collections.unmodifiableMap(c);
        this.averageValence = list.isEmpty() ? 0 : sum / list.size();
        this.mostCommon = c.entrySet().stream()
                .max(Map.Entry.<Mood, Integer>comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
    }

    public int entries()                  { return entries; }
    public Map<Mood, Integer> counts()    { return counts; }
    public double averageValence()        { return averageValence; }
    public Optional<Mood> mostCommon()    { return Optional.ofNullable(mostCommon); }
    public int currentStreak()            { return currentStreak; }
    public int longestStreak()            { return longestStreak; }

    /** Words for a valence value, matching the chart's labels. */
    public static String describeValence(double v) {
        if (v >= 0.55) return "Great";
        if (v >= 0.2)  return "Good";
        if (v > -0.2)  return "Okay";
        if (v > -0.55) return "Low";
        return "Rough";
    }
}
