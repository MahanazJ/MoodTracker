package com.mooddiary.analysis;

import com.mooddiary.model.Mood;

/** One piece of text that pushed the analysis toward a mood, for "why" explanations. */
public record Evidence(String phrase, Mood mood, double weight, String note) {

    public String describe() {
        String base = "\"" + phrase + "\" suggests " + mood.displayName();
        return note.isEmpty() ? base : base + "  (" + note + ")";
    }
}
