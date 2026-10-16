package com.mooddiary.analysis;

import com.mooddiary.analysis.EmotionLexicon.Match;
import com.mooddiary.analysis.TextNormalizer.Sentence;
import com.mooddiary.analysis.TextNormalizer.Token;
import com.mooddiary.model.Mood;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Reads a diary entry and works out how the writer feels.
 *
 * <p>For every emotional word or phrase found by the {@link EmotionLexicon},
 * a contribution is computed and adjusted by its context:
 * <ul>
 *   <li><b>Negation</b> within four words ("not happy", "never felt calm") moves
 *       the weight to an opposite mood rather than just deleting it.
 *       "Never been so happy" is recognised as emphasis, not negation.</li>
 *   <li><b>Intensifiers / softeners</b> ("so", "extremely", "a bit", "kind of").</li>
 *   <li><b>Emphasis</b>: ALL CAPS, stretched words ("sooo"), exclamation marks, "af".</li>
 *   <li><b>Contrast</b>: what follows "but", "however", "although…," counts more.</li>
 *   <li><b>Position</b>: later sentences count more, since entries tend to end on
 *       how the writer feels now.</li>
 *   <li><b>Summary sentences</b> ("Overall…", "Today was…", "Right now I feel…").</li>
 *   <li><b>Time</b>: "this morning", "earlier", "at first" count less.</li>
 *   <li><b>Who</b>: feelings attributed only to other people count less;
 *       "I feel/I am …" counts more.</li>
 *   <li><b>Questions</b> count less; repeated words have diminishing returns.</li>
 *   <li><b>Personal learning</b> from the user's past corrections.</li>
 * </ul>
 */
public final class MoodAnalyzer {

    private static final Map<Mood, Mood> OPPOSITE = new EnumMap<>(Mood.class);

    static {
        OPPOSITE.put(Mood.HAPPY, Mood.SAD);
        OPPOSITE.put(Mood.EXCITED, Mood.BORED);
        OPPOSITE.put(Mood.GRATEFUL, Mood.SAD);
        OPPOSITE.put(Mood.LOVED, Mood.LONELY);
        OPPOSITE.put(Mood.CONFIDENT, Mood.ANXIOUS);
        OPPOSITE.put(Mood.HOPEFUL, Mood.SAD);
        OPPOSITE.put(Mood.CALM, Mood.ANXIOUS);
        OPPOSITE.put(Mood.BORED, Mood.HAPPY);
        OPPOSITE.put(Mood.TIRED, Mood.HAPPY);
        OPPOSITE.put(Mood.STRESSED, Mood.CALM);
        OPPOSITE.put(Mood.ANXIOUS, Mood.CALM);
        OPPOSITE.put(Mood.ANGRY, Mood.CALM);
        OPPOSITE.put(Mood.LONELY, Mood.LOVED);
        OPPOSITE.put(Mood.SAD, Mood.CALM);
        OPPOSITE.put(Mood.HEARTBROKEN, Mood.CALM);
        // NOSTALGIC has no meaningful opposite; a negated nostalgic cue is dropped.
    }

    private static final Set<String> SELF = Set.of("i", "me", "myself", "we", "us", "ourselves");
    /** People who might be the subject of a sentence instead of the writer. */
    private static final Set<String> OTHERS = Set.of("he", "she", "they", "his", "her", "their", "mom", "dad",
            "mother", "father", "brother", "sister", "friend", "friends", "boss", "coworker", "colleague", "teacher",
            "everyone", "people", "somebody", "someone", "husband", "wife", "boyfriend", "girlfriend", "partner",
            "son", "daughter", "roommate", "neighbor", "grandma", "grandpa", "aunt", "uncle", "cousin", "manager");
    /** Stories and media: "the movie was so sad" is not the writer's own mood. */
    private static final Set<String> MEDIA = Set.of("movie", "film", "show", "book", "episode", "character", "song",
            "story", "novel", "series", "documentary", "ending", "finale", "game", "news", "article", "video");
    private static final Set<String> FEEL_VERBS = Set.of("feel", "feeling", "felt", "feels", "am", "became", "become",
            "becoming", "getting", "got", "grew");
    private static final Set<String> CONTRAST = Set.of("but", "however", "yet", "though", "although", "anyway", "anyways",
            "nevertheless", "still");
    private static final Set<String> CONCESSIVE_OPENERS = Set.of("although", "though", "despite", "while", "even");

    private static final List<List<String>> SUMMARY_CUES = phrases("overall", "all in all", "in general", "today was",
            "today has been", "today is", "the day was", "this day was", "at the end of the day", "honestly", "i just feel",
            "right now i", "now i feel", "i feel so", "i am feeling", "i have been feeling", "lately i", "this week has been",
            "this week was", "the whole day", "all day", "in the end", "ultimately", "mostly i", "i guess i am",
            "i think i am", "my mood");
    private static final List<List<String>> NOW_CUES = phrases("now", "right now", "tonight", "currently", "at the moment",
            "finally", "these days", "lately");
    private static final List<List<String>> PAST_CUES = phrases("this morning", "earlier", "at first", "yesterday",
            "last week", "last month", "last year", "years ago", "back then", "used to", "before that", "initially");

    private final EmotionLexicon lexicon;
    private final PersonalLexicon personal;

    public MoodAnalyzer(EmotionLexicon lexicon, PersonalLexicon personal) {
        this.lexicon = Objects.requireNonNull(lexicon, "lexicon");
        this.personal = Objects.requireNonNull(personal, "personal");
    }

    public static MoodAnalyzer standard() {
        return new MoodAnalyzer(EmotionLexicon.standard(), new PersonalLexicon());
    }

    public PersonalLexicon personalLexicon() {
        return personal;
    }

    public MoodAnalysis analyze(String text) {
        List<Sentence> sentences = TextNormalizer.sentences(text == null ? "" : text);
        Scorer scorer = new Scorer();
        int n = sentences.size();
        int words = 0;
        int emotionalSentences = 0;

        for (int k = 0; k < n; k++) {
            Sentence s = sentences.get(k);
            words += (int) s.tokens().stream().filter(t -> !t.isBoundary()).count();
            SentenceContext ctx = sentenceContext(sentences, k);
            if (scoreSentence(s, ctx, scorer)) {
                emotionalSentences++;
            }
        }
        applyPersonal(sentences, scorer);
        return summarize(scorer, emotionalSentences, words, SupportScreen.flags(sentences));
    }

    // ------------------------------------------------------------------ sentence level

    private record SentenceContext(double weight, String note) { }

    private SentenceContext sentenceContext(List<Sentence> all, int k) {
        Sentence s = all.get(k);
        List<Token> t = s.tokens();
        int n = all.size();
        double w = (n == 1) ? 1.0 : 0.85 + 0.35 * k / (n - 1);
        List<String> notes = new ArrayList<>(2);

        if (containsAny(t, SUMMARY_CUES)) {
            w *= 1.5;
            notes.add("summary");
        }
        if (containsAny(t, NOW_CUES)) {
            w *= 1.2;
        }
        if (containsAny(t, PAST_CUES)) {
            w *= 0.75;
            notes.add("earlier in the day");
        }
        if (s.question()) {
            w *= 0.7;
        }
        w *= 1.0 + 0.12 * Math.min(3, s.exclamations());

        boolean self = t.stream().anyMatch(x -> SELF.contains(x.word()));
        boolean othersAsSubject = false;
        for (int i = 0; i < Math.min(3, t.size()); i++) {
            if (OTHERS.contains(t.get(i).word())) {
                othersAsSubject = true;
                break;
            }
        }
        boolean mediaAsSubject = false;
        for (int i = 0; i < Math.min(4, t.size()); i++) {
            if (MEDIA.contains(t.get(i).word())) {
                mediaAsSubject = true;
                break;
            }
        }
        if (!self && mediaAsSubject) {
            w *= 0.4;
            notes.add("about a story or show");
        } else if (!self && othersAsSubject) {
            w *= 0.6;
            notes.add("about someone else");
        }

        // A following sentence that opens with "But…" overrides this one somewhat.
        if (k + 1 < n && startsWithContrast(all.get(k + 1))) {
            w *= 0.7;
        }
        if (startsWithContrast(s)) {
            w *= 1.2;
        }
        return new SentenceContext(w, String.join(", ", notes));
    }

    private static boolean startsWithContrast(Sentence s) {
        List<Token> t = s.tokens();
        return !t.isEmpty() && (t.get(0).word().equals("but") || t.get(0).word().equals("however")
                || t.get(0).word().equals("anyway") || t.get(0).word().equals("anyways"));
    }

    /** Per-token weights for clause contrast inside one sentence. */
    private static double[] clauseWeights(List<Token> t) {
        double[] cw = new double[t.size()];
        java.util.Arrays.fill(cw, 1.0);
        if (t.isEmpty()) {
            return cw;
        }
        if (CONCESSIVE_OPENERS.contains(t.get(0).word())) {
            // "Although it rained, I had fun": clause up to the first comma counts less.
            int comma = -1;
            for (int i = 1; i < t.size(); i++) {
                if (t.get(i).isBoundary()) {
                    comma = i;
                    break;
                }
            }
            if (comma > 0) {
                for (int i = 0; i < t.size(); i++) {
                    cw[i] = i < comma ? 0.55 : 1.25;
                }
                return cw;
            }
        }
        for (int i = 1; i < t.size(); i++) {
            String w = t.get(i).word();
            if (w.equals("because") || w.equals("since") || w.equals("cause")) {
                // Reasons explain the feeling ("I'm so lucky because I'd had a long week"); they aren't the feeling.
                for (int j = i + 1; j < t.size() && !t.get(j).isBoundary(); j++) {
                    cw[j] = Math.min(cw[j], 0.6);
                }
            }
            if ((w.equals("though") || w.equals("although")) && i < t.size() - 1) {
                // "I laughed, even though it was a stressful week": the concession counts less.
                for (int j = 0; j < t.size(); j++) {
                    cw[j] = j > i ? Math.min(cw[j], 0.6) : Math.max(cw[j], 1.15);
                }
                continue;
            }
            if (w.equals("but") || w.equals("however") || w.equals("yet")) {
                for (int j = 0; j < t.size(); j++) {
                    cw[j] = j < i ? Math.min(cw[j], 0.6) : 1.25;
                }
            }
        }
        return cw;
    }

    // ------------------------------------------------------------------ token level

    /** @return true if the sentence contained any emotional language */
    private boolean scoreSentence(Sentence s, SentenceContext ctx, Scorer scorer) {
        List<Token> t = s.tokens();
        double[] cw = clauseWeights(t);
        boolean selfInSentence = t.stream().anyMatch(x -> SELF.contains(x.word()));
        int prevHitEnd = 0;
        boolean any = false;
        boolean negativeHit = false;

        int i = 0;
        while (i < t.size()) {
            Token tok = t.get(i);
            if (tok.isBoundary()) {
                i++;
                prevHitEnd = i;
                continue;
            }
            Optional<Match> found = lexicon.matchAt(t, i);
            // An intensifier like "fucking" or "damn" that has its own entry is treated as a
            // modifier when an emotional word follows it.
            if (found.isPresent() && found.get().length() == 1 && EmotionLexicon.INTENSIFIERS.containsKey(tok.word())
                    && emotionalWithin(t, i + 1, 2)) {
                found = Optional.empty();
            }
            if (found.isEmpty()) {
                i++;
                continue;
            }
            Match m = found.get();
            if (m.isNeutral()) {
                i += m.length();
                prevHitEnd = i;
                continue;
            }

            Modifiers mod = lookBack(t, i, prevHitEnd, m);
            double emphasis = 1.0;
            for (int k = i; k < i + m.length(); k++) {
                if (t.get(k).caps()) {
                    emphasis = Math.max(emphasis, 1.4);
                }
                if (t.get(k).elongated()) {
                    emphasis = Math.max(emphasis, 1.25);
                }
            }
            emphasis = Math.max(emphasis, mod.emphasis);
            int end = i + m.length();
            if (end < t.size() && t.get(end).word().equals("af")) {
                emphasis *= 1.4;
            }
            double feel = (selfInSentence && mod.feelVerb) ? 1.2 : 1.0;
            double repetition = scorer.repetitionFactor(m.key());
            double base = ctx.weight() * cw[i] * m.formFactor() * emphasis * feel * repetition;

            boolean negated = mod.negated ^ m.prefixNegated();
            String surface = surface(t, mod.start, end);
            String note = noteFor(mod, negated, cw[i], ctx.note());

            for (Map.Entry<Mood, Double> e : m.weights().entrySet()) {
                Mood mood = e.getKey();
                double w = e.getValue() * base;
                if (negated) {
                    Mood target = OPPOSITE.get(mood);
                    if (target == null) {
                        continue;
                    }
                    double factor = mood.isPositive() ? 0.6 : 0.35;
                    if (mod.multiplier > 1.0) {
                        factor *= 0.75;          // "not very happy" is milder than "not happy"
                    }
                    scorer.add(target, w * factor, surface, note);
                } else if (mod.hypothetical && !mood.isPositive() && mood != Mood.ANXIOUS) {
                    // "I might get fired": the bad event hasn't happened; the feeling is worry.
                    scorer.add(Mood.ANXIOUS, w * mod.multiplier * 0.6, surface, "something that might happen");
                    scorer.add(mood, w * mod.multiplier * 0.4, surface, note);
                } else {
                    scorer.add(mood, w * mod.multiplier, surface, note);
                }
            }
            any = true;
            prevHitEnd = end;
            i = end;
            for (Mood mood : m.weights().keySet()) {
                if (!mood.isPositive() && !negated && mood != Mood.NOSTALGIC) {
                    negativeHit = true;
                }
            }
        }

        // Excitement often shows only in punctuation: "WE CLOSED ON THE HOUSE!!!"
        long wordsInSentence = t.stream().filter(x -> !x.isBoundary()).count();
        long capsWords = t.stream().filter(Token::caps).count();
        boolean shouting = wordsInSentence >= 3 && capsWords >= 0.6 * wordsInSentence;
        if ((s.exclamations() >= 2 || shouting) && !negativeHit) {
            scorer.add(Mood.EXCITED, 0.3 * ctx.weight(), shouting ? "(all caps)" : "(exclamation marks)", "energy in your writing");
            scorer.add(Mood.HAPPY, 0.22 * ctx.weight(), shouting ? "(all caps)" : "(exclamation marks)", "energy in your writing");
            any = true;
        }
        // Questions you ask yourself ("Did I sound rude? Is she mad at me?") are a sign of worry.
        if (s.question() && selfInSentence) {
            scorer.add(Mood.ANXIOUS, 0.35 * ctx.weight(), "(questioning yourself)", "worried questions");
            any = true;
        }
        return any;
    }

    private boolean emotionalWithin(List<Token> t, int from, int span) {
        for (int k = from; k < Math.min(t.size(), from + span); k++) {
            if (t.get(k).isBoundary()) {
                return false;
            }
            Optional<Match> m = lexicon.matchAt(t, k);
            if (m.isPresent() && !m.get().isNeutral()) {
                return true;
            }
        }
        return false;
    }

    private static final Set<String> STOP_WORDS_CANCELLING_NEGATION = Set.of("stop", "stopping", "quit", "help");
    private static final Set<String> HYPOTHETICAL = Set.of("might", "may", "if", "possibly");

    private static final class Modifiers {
        boolean negated;
        boolean hypothetical;
        boolean feelVerb;
        double multiplier = 1.0;
        double emphasis = 1.0;
        int start;
        String kind = "";
    }

    /** Scans up to four words before a hit for negators, intensifiers and softeners. */
    private static Modifiers lookBack(List<Token> t, int hit, int floor, Match m) {
        Modifiers mod = new Modifiers();
        mod.start = hit;
        int lo = Math.max(floor, hit - 4);
        boolean neverIntensifies = false;
        for (int j = hit - 1; j >= lo; j--) {
            Token tok = t.get(j);
            String w = tok.word();
            if (tok.isBoundary() || CONTRAST.contains(w) || w.equals("and") || w.equals("because")) {
                break;
            }
            if (HYPOTHETICAL.contains(w)) {
                mod.hypothetical = true;
            }
            if (EmotionLexicon.NEGATORS.contains(w)) {
                // "never been so happy", "never felt better": emphasis, not negation
                if (w.equals("never") && (containsWord(t, j + 1, hit, "so", "this", "that", "more", "as")
                        || m.formFactor() > 1.0)) {
                    neverIntensifies = true;
                } else if (containsWord(t, j + 1, hit, STOP_WORDS_CANCELLING_NEGATION.toArray(new String[0]))) {
                    // "can't stop crying", "couldn't help smiling": a double negative that intensifies
                    mod.multiplier *= 1.3;
                } else {
                    mod.negated = !mod.negated;
                }
                mod.start = j;
            } else if (EmotionLexicon.INTENSIFIERS.containsKey(w)) {
                mod.multiplier *= EmotionLexicon.INTENSIFIERS.get(w);
                if (tok.caps() || tok.elongated()) {
                    mod.emphasis = 1.3;
                }
                mod.start = Math.min(mod.start, j);
            } else if (EmotionLexicon.DIMINISHERS.containsKey(w)) {
                mod.multiplier *= EmotionLexicon.DIMINISHERS.get(w);
                mod.start = Math.min(mod.start, j);
            } else if (FEEL_VERBS.contains(w)) {
                mod.feelVerb = true;
            }
        }
        if (neverIntensifies) {
            mod.multiplier *= 1.6;
        }
        mod.multiplier = Math.min(mod.multiplier, 2.5);
        if (mod.negated) {
            mod.kind = "negated";
        } else if (mod.multiplier > 1.05) {
            mod.kind = "intensified";
        } else if (mod.multiplier < 0.95) {
            mod.kind = "softened";
        }
        return mod;
    }

    private static boolean containsWord(List<Token> t, int from, int to, String... words) {
        for (int k = from; k < to; k++) {
            for (String w : words) {
                if (t.get(k).word().equals(w)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String noteFor(Modifiers mod, boolean negated, double clauseWeight, String sentenceNote) {
        List<String> parts = new ArrayList<>(3);
        if (negated) {
            parts.add("negated");
        } else if (!mod.kind.isEmpty()) {
            parts.add(mod.kind);
        }
        if (clauseWeight > 1.01) {
            parts.add("after a \"but\"");
        } else if (clauseWeight < 0.99) {
            parts.add("before a \"but\"");
        }
        if (!sentenceNote.isEmpty()) {
            parts.add(sentenceNote);
        }
        return String.join(", ", parts);
    }

    private static String surface(List<Token> t, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int k = from; k < to; k++) {
            String w = t.get(k).word();
            if (w.startsWith("emo_")) {
                w = "(" + w.substring(4) + " emoji)";
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(w.replace('_', ' '));
        }
        String out = (" " + sb + " ").replace(" i ", " I ").strip();
        return out.replace("can not", "can't").replace("do not", "don't").replace("did not", "didn't")
                .replace("could not", "couldn't").replace("does not", "doesn't").replace("i am", "I'm")
                .replace("is not", "isn't").replace("was not", "wasn't");
    }

    private void applyPersonal(List<Sentence> sentences, Scorer scorer) {
        if (personal.size() == 0) {
            return;
        }
        for (Sentence s : sentences) {
            for (Token tok : s.tokens()) {
                if (tok.isBoundary() || !PersonalLexicon.isLearnable(tok.word())) {
                    continue;
                }
                for (Map.Entry<Mood, Double> e : personal.weightsFor(tok.word()).entrySet()) {
                    scorer.add(e.getKey(), e.getValue() * 0.6, tok.word(), "learned from your corrections");
                }
            }
        }
    }

    // ------------------------------------------------------------------ summary

    private MoodAnalysis summarize(Scorer scorer, int emotionalSentences, int words, boolean support) {
        Map<Mood, Double> scores = new EnumMap<>(Mood.class);
        double total = 0;
        for (Mood m : Mood.values()) {
            double v = Math.max(0, scorer.scores.getOrDefault(m, 0.0));
            scores.put(m, v);
            total += v;
        }
        List<Map.Entry<Mood, Double>> ranked = scores.entrySet().stream()
                .sorted(Map.Entry.<Mood, Double>comparingByValue().reversed())
                .toList();

        boolean lowSignal = total < 0.6;
        Mood primary = total < 0.2 ? Mood.CALM : ranked.get(0).getKey();
        double top = scores.get(primary);
        double second = ranked.get(0).getKey() == primary ? ranked.get(1).getValue() : ranked.get(0).getValue();
        Mood secondary = null;
        Mood runnerUp = ranked.get(0).getKey() == primary ? ranked.get(1).getKey() : ranked.get(0).getKey();
        if (second >= 0.35 * top && second > 0.4) {
            secondary = runnerUp;
        }

        Map<Mood, Double> shares = new EnumMap<>(Mood.class);
        double valence = 0;
        for (Mood m : Mood.values()) {
            double share = total > 0 ? scores.get(m) / total : 0;
            shares.put(m, share);
            valence += share * m.valence();
        }
        if (total < 0.2) {
            valence = 0.1;     // nothing emotional said: plot as "okay"
        }

        double share = total > 0 ? top / total : 0;
        double margin = top > 0 ? (top - second) / top : 0;
        double evidenceFactor = 1 - Math.exp(-total / 1.2);
        double confidence = (0.55 * share + 0.45 * margin) * evidenceFactor;
        confidence = Math.max(0.05, Math.min(0.99, 0.15 + 0.9 * confidence));
        if (lowSignal) {
            confidence = Math.min(confidence, 0.35);
        }

        double density = top / Math.sqrt(Math.max(1, emotionalSentences));
        int intensity = (int) Math.round(1 + 9 * (1 - Math.exp(-density / 1.4)));
        intensity = Math.max(1, Math.min(10, lowSignal ? Math.min(intensity, 4) : intensity));

        List<Evidence> evidence = scorer.evidence(12);
        return new MoodAnalysis(primary, secondary, confidence, intensity, valence, shares, evidence,
                lowSignal, support, words);
    }

    /** Accumulates scores and evidence. */
    private static final class Scorer {
        final Map<Mood, Double> scores = new EnumMap<>(Mood.class);
        private final Map<String, Integer> seen = new HashMap<>();
        private final Map<String, Evidence> evidence = new LinkedHashMap<>();

        double repetitionFactor(String key) {
            int count = seen.merge(key, 1, Integer::sum);
            return 1.0 / (1.0 + 0.5 * (count - 1));
        }

        void add(Mood mood, double weight, String phrase, String note) {
            if (Math.abs(weight) < 1e-6) {
                return;
            }
            scores.merge(mood, weight, Double::sum);
            String key = phrase + "|" + mood;
            evidence.merge(key, new Evidence(phrase, mood, weight, note),
                    (a, b) -> new Evidence(a.phrase(), a.mood(), a.weight() + b.weight(), a.note()));
        }

        /** Strongest evidence first; each phrase appears once, under the mood it pointed to most. */
        List<Evidence> evidence(int limit) {
            Map<String, Evidence> byPhrase = new LinkedHashMap<>();
            evidence.values().stream()
                    .filter(e -> e.weight() > 0.05)
                    .sorted(Comparator.comparingDouble(Evidence::weight).reversed())
                    .forEach(e -> byPhrase.putIfAbsent(e.phrase(), e));
            return byPhrase.values().stream().limit(limit).toList();
        }
    }

    private static List<List<String>> phrases(String... ps) {
        List<List<String>> out = new ArrayList<>(ps.length);
        for (String p : ps) {
            out.add(TextNormalizer.words(p));
        }
        return out;
    }

    private static boolean containsAny(List<Token> t, List<List<String>> phrases) {
        for (List<String> p : phrases) {
            outer:
            for (int i = 0; i + p.size() <= t.size(); i++) {
                for (int k = 0; k < p.size(); k++) {
                    if (!t.get(i + k).word().equals(p.get(k))) {
                        continue outer;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
