package com.mooddiary.analysis;

import com.mooddiary.analysis.TextNormalizer.Token;
import com.mooddiary.model.Mood;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Vocabulary of emotional words and phrases, plus the modifier tables
 * (intensifiers, softeners, negators) the analyzer uses around them.
 */
public final class EmotionLexicon {

    /** A lexicon hit at some position in a sentence. */
    public record Match(int length, String key, Map<Mood, Double> weights,
                        boolean prefixNegated, double formFactor) {
        public boolean isNeutral() {
            return weights.isEmpty();
        }
    }

    /** Words that strengthen the emotion that follows them. */
    public static final Map<String, Double> INTENSIFIERS = Map.ofEntries(
            Map.entry("very", 1.4), Map.entry("really", 1.4), Map.entry("so", 1.4), Map.entry("extremely", 1.8),
            Map.entry("incredibly", 1.7), Map.entry("insanely", 1.6), Map.entry("super", 1.5), Map.entry("totally", 1.4),
            Map.entry("completely", 1.5), Map.entry("utterly", 1.6), Map.entry("absolutely", 1.6), Map.entry("truly", 1.4),
            Map.entry("deeply", 1.5), Map.entry("seriously", 1.4), Map.entry("genuinely", 1.3), Map.entry("too", 1.3),
            Map.entry("such", 1.3), Map.entry("most", 1.3), Map.entry("more", 1.15), Map.entry("fucking", 1.6),
            Map.entry("freaking", 1.5), Map.entry("fricking", 1.5), Map.entry("damn", 1.4), Map.entry("bloody", 1.4),
            Map.entry("hella", 1.5), Map.entry("pretty", 1.15), Map.entry("quite", 1.2), Map.entry("especially", 1.2),
            Map.entry("overwhelmingly", 1.7), Map.entry("terribly", 1.5), Map.entry("awfully", 1.5),
            Map.entry("unbelievably", 1.7), Map.entry("ridiculously", 1.5), Map.entry("beyond", 1.5),
            Map.entry("exceptionally", 1.6), Map.entry("properly", 1.2), Map.entry("massively", 1.6),
            Map.entry("hugely", 1.5), Map.entry("immensely", 1.6), Map.entry("intensely", 1.6), Map.entry("crazy", 1.4),
            Map.entry("mega", 1.4), Map.entry("way", 1.3), Map.entry("all", 1.1), Map.entry("even", 1.1),
            Map.entry("still", 1.05), Map.entry("honestly", 1.1), Map.entry("literally", 1.2), Map.entry("just", 1.05),
            Map.entry("always", 1.15), Map.entry("constantly", 1.3));

    /** Words that soften the emotion that follows them. */
    public static final Map<String, Double> DIMINISHERS = Map.ofEntries(
            Map.entry("slightly", 0.5), Map.entry("a_bit", 0.6), Map.entry("a_little", 0.6), Map.entry("kind_of", 0.65),
            Map.entry("sort_of", 0.65), Map.entry("somewhat", 0.65), Map.entry("fairly", 0.85), Map.entry("a_tad", 0.55),
            Map.entry("mildly", 0.55), Map.entry("barely", 0.35), Map.entry("almost", 0.7), Map.entry("partly", 0.6),
            Map.entry("bit", 0.65), Map.entry("maybe", 0.75), Map.entry("probably", 0.85), Map.entry("occasionally", 0.6),
            Map.entry("sometimes", 0.8), Map.entry("rather", 0.85), Map.entry("moderately", 0.7), Map.entry("relatively", 0.8));

    /** Words that flip or cancel the emotion that follows them. */
    public static final Set<String> NEGATORS = Set.of(
            "not", "no", "never", "nothing", "nobody", "none", "neither", "nor", "without", "hardly", "scarcely",
            "no_longer", "nowhere", "lack", "lacking", "zero", "nope");

    private static final int MAX_PHRASE_WORDS = 9;

    private static final Map<String, String> IRREGULAR = Map.ofEntries(
            Map.entry("felt", "feel"), Map.entry("broke", "break"), Map.entry("broken", "break"), Map.entry("made", "make"),
            Map.entry("lost", "lose"), Map.entry("sat", "sit"), Map.entry("ate", "eat"), Map.entry("went", "go"),
            Map.entry("gone", "go"), Map.entry("got", "get"), Map.entry("gotten", "get"), Map.entry("thought", "think"),
            Map.entry("came", "come"), Map.entry("ran", "run"), Map.entry("kept", "keep"), Map.entry("slept", "sleep"),
            Map.entry("fell", "fall"), Map.entry("fallen", "fall"), Map.entry("took", "take"), Map.entry("taken", "take"),
            Map.entry("brought", "bring"), Map.entry("said", "say"), Map.entry("told", "tell"), Map.entry("saw", "see"),
            Map.entry("seen", "see"), Map.entry("gave", "give"), Map.entry("given", "give"), Map.entry("held", "hold"),
            Map.entry("spent", "spend"), Map.entry("stood", "stand"), Map.entry("had", "have"), Map.entry("has", "have"),
            Map.entry("did", "do"), Map.entry("does", "do"), Map.entry("was", "be"), Map.entry("were", "be"),
            Map.entry("is", "be"), Map.entry("am", "be"), Map.entry("are", "be"), Map.entry("been", "be"),
            Map.entry("left", "leave"), Map.entry("met", "meet"), Map.entry("woke", "wake"), Map.entry("woken", "wake"),
            Map.entry("wrote", "write"), Map.entry("written", "write"), Map.entry("shrank", "shrink"),
            Map.entry("shrunk", "shrink"), Map.entry("began", "begin"), Map.entry("begun", "begin"),
            Map.entry("drank", "drink"), Map.entry("drunk", "drink"), Map.entry("spoke", "speak"), Map.entry("heard", "hear"),
            Map.entry("cried", "cry"), Map.entry("lay", "lie"), Map.entry("lying", "lie"));

    private final Map<String, Map<Mood, Double>> terms;
    private final Map<String, Map<Mood, Double>> stemmedTerms;

    private EmotionLexicon(Map<String, Map<Mood, Double>> terms, Map<String, Map<Mood, Double>> stemmedTerms) {
        this.terms = terms;
        this.stemmedTerms = stemmedTerms;
    }

    /**
     * Crude, tense-agnostic stem used to match phrases in any verb form:
     * "nodded off" / "nodding off", "I was beat" / "I'm beat", "sitting down" / "sat down".
     * Lexicon keys and diary text are stemmed identically, so it only needs to be consistent.
     */
    static String stem(String w) {
        if (w.startsWith("emo_") || w.indexOf('_') >= 0) {
            return w;
        }
        String irregular = IRREGULAR.get(w);
        if (irregular != null) {
            w = irregular;
        }
        if (w.length() <= 3) {
            return w;
        }
        String s = w;
        boolean stripped = false;
        if (s.endsWith("ies") || s.endsWith("ied")) {
            s = s.substring(0, s.length() - 3) + "y";
        } else if (s.endsWith("ing") && s.length() > 5) {
            s = s.substring(0, s.length() - 3);
            stripped = true;
        } else if (s.endsWith("ed") && s.length() > 4) {
            s = s.substring(0, s.length() - 2);
            stripped = true;
        } else if (s.endsWith("sses") || s.endsWith("shes") || s.endsWith("ches") || s.endsWith("xes")) {
            s = s.substring(0, s.length() - 2);
        } else if (s.endsWith("s") && !s.endsWith("ss") && !s.endsWith("us") && !s.endsWith("is")) {
            s = s.substring(0, s.length() - 1);
        }
        int n = s.length();
        if (stripped && n >= 2 && s.charAt(n - 1) == s.charAt(n - 2) && "bdgmnprt".indexOf(s.charAt(n - 1)) >= 0) {
            s = s.substring(0, n - 1);
        }
        if (s.length() > 3 && s.endsWith("e")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /** The built-in lexicon. */
    public static EmotionLexicon standard() {
        Builder b = new Builder();
        LexiconData.populate(b);
        return b.build();
    }

    public int size() {
        return terms.size();
    }

    public boolean contains(String normalizedWord) {
        return terms.containsKey(normalizedWord);
    }

    /**
     * Longest lexicon match starting at {@code start}. Phrases never span a
     * clause boundary. Single words fall back to inflection variants
     * ("happier", "loneliness", "stressing") and an "un-" prefix check
     * ("unloved" means not loved).
     */
    public Optional<Match> matchAt(List<Token> tokens, int start) {
        if (start >= tokens.size() || tokens.get(start).isBoundary()) {
            return Optional.empty();
        }
        int maxLen = Math.min(MAX_PHRASE_WORDS, tokens.size() - start);
        for (int len = maxLen; len >= 2; len--) {
            if (spansBoundary(tokens, start, len)) {
                continue;
            }
            String key = join(tokens, start, len);
            Map<Mood, Double> w = terms.get(key);
            if (w != null) {
                return Optional.of(new Match(len, key, w, false, 1.0));
            }
            String stemKey = joinStemmed(tokens, start, len);
            w = stemmedTerms.get(stemKey);
            if (w != null) {
                return Optional.of(new Match(len, stemKey, w, false, 0.95));
            }
        }
        return matchWord(tokens.get(start));
    }

    private Optional<Match> matchWord(Token token) {
        String word = token.word();
        Map<Mood, Double> exact = terms.get(word);
        if (exact != null) {
            return Optional.of(new Match(1, word, exact, false, 1.0));
        }
        if (token.elongated()) {
            String collapsed = word.replaceAll("([a-z])\\1", "$1");
            Map<Mood, Double> w = terms.get(collapsed);
            if (w != null) {
                return Optional.of(new Match(1, collapsed, w, false, 1.0));
            }
        }
        if (word.startsWith("emo_") || word.length() < 4) {
            return Optional.empty();
        }
        for (Variant v : variants(word)) {
            Map<Mood, Double> w = terms.get(v.form);
            if (w != null && !w.isEmpty()) {
                return Optional.of(new Match(1, v.form, w, false, v.factor));
            }
        }
        String st = stem(word);
        boolean inflected = !st.equals(word) && !st.equals(word.substring(0, word.length() - 1));
        Map<Mood, Double> stemmed = inflected ? stemmedTerms.get(st) : null;
        if (stemmed != null && !stemmed.isEmpty()) {
            return Optional.of(new Match(1, stem(word), stemmed, false, 0.9));
        }
        if (word.startsWith("un") && word.length() > 5) {
            String base = word.substring(2);
            Map<Mood, Double> w = terms.get(base);
            if (w == null) {
                for (Variant v : variants(base)) {
                    w = terms.get(v.form);
                    if (w != null) {
                        base = v.form;
                        break;
                    }
                }
            }
            if (w != null && !w.isEmpty()) {
                return Optional.of(new Match(1, base, w, true, 0.9));
            }
        }
        return Optional.empty();
    }

    private record Variant(String form, double factor) { }

    /** Candidate dictionary forms for an inflected word, most likely first. */
    private static List<Variant> variants(String w) {
        java.util.ArrayList<Variant> out = new java.util.ArrayList<>(8);
        int n = w.length();
        if (w.endsWith("iness")) {
            out.add(new Variant(w.substring(0, n - 5) + "y", 1.0));          // happiness -> happy
        }
        if (w.endsWith("ness")) {
            out.add(new Variant(w.substring(0, n - 4), 1.0));                // sadness -> sad
        }
        if (w.endsWith("iest") || w.endsWith("ier")) {
            out.add(new Variant(w.substring(0, n - (w.endsWith("iest") ? 4 : 3)) + "y", 1.1)); // happier -> happy
        }
        if (w.endsWith("est") && n > 5) {
            String b = w.substring(0, n - 3);
            out.add(new Variant(b, 1.1));
            out.add(new Variant(undouble(b), 1.1));                          // saddest -> sad
            out.add(new Variant(b + "e", 1.1));
        }
        if (w.endsWith("er") && n > 4) {
            String b = w.substring(0, n - 2);
            out.add(new Variant(b, 1.05));                                   // calmer -> calm
            out.add(new Variant(undouble(b), 1.05));                         // sadder -> sad
        }
        if (w.endsWith("ily")) {
            out.add(new Variant(w.substring(0, n - 3) + "y", 0.6));          // happily -> happy
        }
        if (w.endsWith("ly") && n > 4) {
            out.add(new Variant(w.substring(0, n - 2), 0.6));                // sadly -> sad
        }
        if (w.endsWith("ied") || w.endsWith("ies")) {
            out.add(new Variant(w.substring(0, n - 3) + "y", 0.95));         // worries -> worry
        }
        if (w.endsWith("ing") && n > 5) {
            String b = w.substring(0, n - 3);
            out.add(new Variant(b, 0.95));                                   // stressing -> stress
            out.add(new Variant(b + "e", 0.95));                             // smiling -> smile
            out.add(new Variant(undouble(b), 0.95));                         // sobbing -> sob
        }
        if (w.endsWith("ed") && n > 4) {
            String b = w.substring(0, n - 2);
            out.add(new Variant(b, 0.95));
            out.add(new Variant(b + "e", 0.95));
            out.add(new Variant(undouble(b), 0.95));
            out.add(new Variant(w.substring(0, n - 1), 0.95));               // smiled -> smile
        }
        if (w.endsWith("es") && n > 4) {
            out.add(new Variant(w.substring(0, n - 2), 0.95));
        }
        if (w.endsWith("s") && !w.endsWith("ss") && n > 3) {
            out.add(new Variant(w.substring(0, n - 1), 0.95));               // tears -> tear
        }
        return out;
    }

    private static String undouble(String s) {
        int n = s.length();
        return (n >= 2 && s.charAt(n - 1) == s.charAt(n - 2)) ? s.substring(0, n - 1) : s;
    }

    private static boolean spansBoundary(List<Token> tokens, int start, int len) {
        for (int k = start; k < start + len; k++) {
            if (tokens.get(k).isBoundary()) {
                return true;
            }
        }
        return false;
    }

    private static String joinStemmed(List<Token> tokens, int start, int len) {
        StringBuilder sb = new StringBuilder();
        for (int k = start; k < start + len; k++) {
            if (k > start) {
                sb.append(' ');
            }
            sb.append(stem(tokens.get(k).word()));
        }
        return sb.toString();
    }

    private static String join(List<Token> tokens, int start, int len) {
        StringBuilder sb = new StringBuilder();
        for (int k = start; k < start + len; k++) {
            if (k > start) {
                sb.append(' ');
            }
            sb.append(tokens.get(k).word());
        }
        return sb.toString();
    }

    /** Collects lexicon terms; duplicates keep the strongest weight per mood. */
    public static final class Builder {

        private final Map<String, Map<Mood, Double>> terms = new HashMap<>();

        public Builder add(Mood mood, double weight, String... phrases) {
            for (String p : phrases) {
                merge(p, mood, weight);
            }
            return this;
        }

        /** One term pointing at several moods: mix("bittersweet", NOSTALGIC, 1.1, SAD, 0.4). */
        public Builder mix(String phrase, Object... moodWeightPairs) {
            if (moodWeightPairs.length % 2 != 0) {
                throw new IllegalArgumentException("mix() needs mood/weight pairs for '" + phrase + "'");
            }
            for (int i = 0; i < moodWeightPairs.length; i += 2) {
                merge(phrase, (Mood) moodWeightPairs[i], ((Number) moodWeightPairs[i + 1]).doubleValue());
            }
            return this;
        }

        /** Terms that should be recognized but carry no emotion. */
        public Builder neutral(String... phrases) {
            for (String p : phrases) {
                terms.computeIfAbsent(normalize(p), k -> new EnumMap<>(Mood.class));
            }
            return this;
        }

        public EmotionLexicon build() {
            Map<String, Map<Mood, Double>> stemmed = new HashMap<>();
            terms.forEach((k, v) -> {
                StringBuilder sk = new StringBuilder();
                for (String part : k.split(" ")) {
                    if (sk.length() > 0) {
                        sk.append(' ');
                    }
                    sk.append(stem(part));
                }
                Map<Mood, Double> target = stemmed.computeIfAbsent(sk.toString(), x -> new EnumMap<>(Mood.class));
                v.forEach((mood, w) -> target.merge(mood, w, Math::max));
            });
            return new EmotionLexicon(freeze(terms), freeze(stemmed));
        }

        private static Map<String, Map<Mood, Double>> freeze(Map<String, Map<Mood, Double>> in) {
            Map<String, Map<Mood, Double>> frozen = new HashMap<>();
            in.forEach((k, v) -> frozen.put(k, v.isEmpty()
                    ? Collections.<Mood, Double>emptyMap()
                    : Collections.unmodifiableMap(new EnumMap<>(v))));
            return Collections.unmodifiableMap(frozen);
        }

        private void merge(String phrase, Mood mood, double weight) {
            terms.computeIfAbsent(normalize(phrase), k -> new EnumMap<>(Mood.class))
                    .merge(mood, weight, Math::max);
        }

        private static String normalize(String phrase) {
            List<String> words = TextNormalizer.words(phrase);
            if (words.isEmpty()) {
                throw new IllegalArgumentException("Empty lexicon phrase: '" + phrase + "'");
            }
            if (words.size() > MAX_PHRASE_WORDS) {
                throw new IllegalArgumentException("Phrase too long: '" + phrase + "'");
            }
            return String.join(" ", words);
        }
    }
}
