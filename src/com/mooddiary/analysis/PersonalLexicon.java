package com.mooddiary.analysis;

import com.mooddiary.analysis.TextNormalizer.Sentence;
import com.mooddiary.analysis.TextNormalizer.Token;
import com.mooddiary.model.Mood;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Word-to-mood associations learned from the user's own corrections.
 *
 * <p>When the user says "that entry was actually Stressed, not Sad", the
 * distinctive words in it are nudged toward Stressed and away from Sad. Over
 * time words like a coworker's name, "commute", or "rehearsal" pick up the
 * meaning they have in this particular person's life.
 */
public final class PersonalLexicon {

    static final double STEP_TOWARD = 0.3;
    static final double STEP_AWAY = 0.15;
    static final double MAX_WEIGHT = 1.0;
    static final double MIN_WEIGHT = -0.6;

    private static final Set<String> STOPWORDS = Set.of(
            "the", "and", "for", "that", "this", "with", "was", "were", "are", "have", "has", "had", "but", "not",
            "you", "your", "they", "them", "their", "she", "her", "him", "his", "its", "our", "out", "about",
            "just", "then", "than", "there", "here", "what", "when", "where", "which", "who", "why", "how",
            "all", "any", "some", "can", "could", "would", "should", "will", "did", "does", "doing", "been",
            "being", "into", "from", "over", "after", "before", "again", "also", "very", "really", "so", "too",
            "more", "most", "much", "many", "lot", "got", "get", "getting", "went", "go", "going", "come",
            "came", "make", "made", "today", "day", "yesterday", "tomorrow", "now", "thing", "things",
            "like", "feel", "felt", "feeling", "know", "think", "thought", "said", "say", "one", "two", "time",
            "even", "still", "way", "because", "while", "though", "back", "off", "only", "own", "same", "other",
            "myself", "me", "my", "i", "am", "is", "it", "to", "of", "in", "on", "at", "a", "an", "be", "we",
            "us", "do", "or", "if", "as", "by", "up", "no", "yes", "okay");

    private final Map<String, Map<Mood, Double>> weights;

    public PersonalLexicon() {
        this(new HashMap<>());
    }

    public PersonalLexicon(Map<String, Map<Mood, Double>> initial) {
        this.weights = new HashMap<>();
        initial.forEach((word, m) -> {
            if (!m.isEmpty()) {
                weights.put(word, new EnumMap<>(m));
            }
        });
    }

    public Map<Mood, Double> weightsFor(String word) {
        Map<Mood, Double> m = weights.get(word);
        return m == null ? Map.of() : m;
    }

    /** Snapshot for persistence. */
    public Map<String, Map<Mood, Double>> snapshot() {
        Map<String, Map<Mood, Double>> copy = new HashMap<>();
        weights.forEach((k, v) -> copy.put(k, Collections.unmodifiableMap(new EnumMap<>(v))));
        return Collections.unmodifiableMap(copy);
    }

    public int size() {
        return weights.size();
    }

    public void clear() {
        weights.clear();
    }

    /** Learns from a correction: words in {@code text} move from {@code detected} toward {@code actual}. */
    public void learn(String text, Mood detected, Mood actual) {
        if (detected == actual) {
            return;
        }
        for (String word : learnableWords(text)) {
            Map<Mood, Double> m = weights.computeIfAbsent(word, k -> new EnumMap<>(Mood.class));
            m.merge(actual, STEP_TOWARD, (a, b) -> Math.min(MAX_WEIGHT, a + b));
            m.merge(detected, -STEP_AWAY, (a, b) -> Math.max(MIN_WEIGHT, a + b));
            m.values().removeIf(v -> Math.abs(v) < 1e-9);
        }
    }

    static boolean isLearnable(String word) {
        return word.length() >= 3 && !word.startsWith("emo_") && !word.contains("_")
                && !STOPWORDS.contains(word) && !EmotionLexicon.NEGATORS.contains(word)
                && !EmotionLexicon.INTENSIFIERS.containsKey(word);
    }

    private static Set<String> learnableWords(String text) {
        Set<String> words = new HashSet<>();
        for (Sentence s : TextNormalizer.sentences(text)) {
            for (Token t : s.tokens()) {
                if (!t.isBoundary() && isLearnable(t.word())) {
                    words.add(t.word());
                }
            }
        }
        return words;
    }
}
