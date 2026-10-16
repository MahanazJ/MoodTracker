package com.mooddiary.storage;

import com.mooddiary.model.Mood;

import java.util.Map;

/** Persistence for word-to-mood associations learned from the user's corrections. */
public interface LearningStore {

    Map<String, Map<Mood, Double>> load();

    void save(Map<String, Map<Mood, Double>> weights);
}
