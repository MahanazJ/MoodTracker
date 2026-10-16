package com.mooddiary.model;

/**
 * The moods the diary can detect. Each mood has a valence (-1 very
 * unpleasant .. +1 very pleasant), used to plot the daily mood chart, and an
 * energy level (0 low .. 1 high).
 */
public enum Mood {
    HAPPY("Happy", 0.80, 0.60, "cheerful, upbeat, having a good time"),
    EXCITED("Excited", 0.85, 0.95, "buzzing, thrilled, can't wait"),
    GRATEFUL("Grateful", 0.75, 0.40, "thankful, appreciative, blessed"),
    LOVED("Loved", 0.85, 0.50, "close to someone, cared for, romantic"),
    CONFIDENT("Confident", 0.70, 0.75, "proud, capable, accomplished"),
    HOPEFUL("Hopeful", 0.50, 0.50, "optimistic, looking forward, healing"),
    CALM("Calm", 0.45, 0.15, "peaceful, relaxed, at ease"),
    NOSTALGIC("Nostalgic", 0.05, 0.30, "reminiscing, bittersweet, missing the past"),
    BORED("Bored", -0.30, 0.10, "restless, unstimulated, nothing going on"),
    TIRED("Tired", -0.40, 0.05, "exhausted, drained, low energy"),
    STRESSED("Stressed", -0.60, 0.80, "overwhelmed, under pressure, stretched thin"),
    ANXIOUS("Anxious", -0.65, 0.85, "worried, nervous, on edge"),
    ANGRY("Angry", -0.75, 0.95, "frustrated, irritated, furious"),
    LONELY("Lonely", -0.70, 0.20, "isolated, left out, missing people"),
    SAD("Sad", -0.80, 0.20, "down, hurt, grieving"),
    HEARTBROKEN("Heartbroken", -0.90, 0.30, "a breakup, rejection, or loss of love");

    private final String displayName;
    private final double valence;
    private final double energy;
    private final String description;

    Mood(String displayName, double valence, double energy, String description) {
        this.displayName = displayName;
        this.valence = valence;
        this.energy = energy;
        this.description = description;
    }

    public String displayName() { return displayName; }
    public double valence()     { return valence; }
    public double energy()      { return energy; }
    public String description() { return description; }
    public boolean isPositive() { return valence > 0.2; }

    @Override
    public String toString() { return displayName; }
}
