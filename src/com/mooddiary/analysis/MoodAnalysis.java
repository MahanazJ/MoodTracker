package com.mooddiary.analysis;

import com.mooddiary.model.Mood;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Result of reading a diary entry. */
public final class MoodAnalysis {

    private final Mood primary;
    private final Mood secondary;               // nullable
    private final double confidence;            // 0..1
    private final int intensity;                // 1..10
    private final double valence;               // -1..1
    private final Map<Mood, Double> shares;     // each mood's share of the total signal, sums to ~1
    private final List<Evidence> evidence;      // strongest first
    private final boolean lowSignal;
    private final boolean supportSuggested;
    private final int wordCount;

    MoodAnalysis(Mood primary, Mood secondary, double confidence, int intensity, double valence,
                 Map<Mood, Double> shares, List<Evidence> evidence, boolean lowSignal,
                 boolean supportSuggested, int wordCount) {
        this.primary = primary;
        this.secondary = secondary;
        this.confidence = confidence;
        this.intensity = intensity;
        this.valence = valence;
        this.shares = Collections.unmodifiableMap(new EnumMap<>(shares));
        this.evidence = List.copyOf(evidence);
        this.lowSignal = lowSignal;
        this.supportSuggested = supportSuggested;
        this.wordCount = wordCount;
    }

    public Mood primary()                 { return primary; }
    public Optional<Mood> secondary()     { return Optional.ofNullable(secondary); }
    public double confidence()            { return confidence; }
    public int intensity()                { return intensity; }
    public double valence()               { return valence; }
    public Map<Mood, Double> shares()     { return shares; }
    public List<Evidence> evidence()      { return evidence; }
    /** True when the text had too little emotional language for a confident read. */
    public boolean lowSignal()            { return lowSignal; }
    /** True when the text contains language suggesting the writer may be in crisis. */
    public boolean supportSuggested()     { return supportSuggested; }
    public int wordCount()                { return wordCount; }

    /** Moods sorted by share, strongest first, skipping those with no signal. */
    public List<Map.Entry<Mood, Double>> ranked() {
        return shares.entrySet().stream()
                .filter(e -> e.getValue() > 0.0001)
                .sorted(Map.Entry.<Mood, Double>comparingByValue().reversed())
                .toList();
    }
}
