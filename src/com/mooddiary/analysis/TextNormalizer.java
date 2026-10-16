package com.mooddiary.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns free-form diary text into sentences of normalized tokens.
 *
 * <p>Handles: curly quotes, emoji and text emoticons (mapped to {@code emo_*}
 * tokens), contractions ("can't" becomes "can not"), apostrophe-less and
 * texting slang ("im", "idk", "kinda"), stretched words ("sooo"), ALL CAPS
 * emphasis, clause punctuation, and multi-word modifiers ("a bit").
 *
 * <p>The lexicon runs its own phrases through {@link #words(String)}, so
 * phrases and diary text are always normalized identically.
 */
public final class TextNormalizer {

    /** A normalized word, emoji token, or clause boundary (",", ";", ":"). */
    public record Token(String word, boolean caps, boolean elongated) {
        public boolean isBoundary() {
            return word.equals(",") || word.equals(";") || word.equals(":");
        }
    }

    /** A sentence's tokens plus punctuation cues. */
    public record Sentence(List<Token> tokens, int exclamations, boolean question) { }

    private static final Pattern SENTENCE_SPLIT = Pattern.compile("(?<=[.!?…])\\s+|\\n+");
    private static final Pattern RAW_TOKEN = Pattern.compile("emo_[a-z]+|[A-Za-z]+(?:'[A-Za-z]+)?|[,;:]");
    private static final Pattern STRETCHED = Pattern.compile("([a-z])\\1{2,}");

    private static final java.util.Set<String> IS_BASES = java.util.Set.of(
            "it", "that", "what", "he", "she", "there", "here", "who", "where", "everything",
            "nothing", "life", "this", "everyone", "nobody", "someone", "today", "work", "how");
    private static final Map<String, String> SLANG = new HashMap<>();
    private static final Map<String, String> JOINS = new HashMap<>();
    private static final Map<Integer, String> EMOJI = new HashMap<>();
    private static final List<Map.Entry<Pattern, String>> EMOTICONS = new ArrayList<>();

    static {
        String[][] slang = {
                {"im", "i am"}, {"ive", "i have"}, {"dont", "do not"}, {"didnt", "did not"},
                {"doesnt", "does not"}, {"cant", "can not"}, {"cannot", "can not"}, {"couldnt", "could not"},
                {"wouldnt", "would not"}, {"shouldnt", "should not"}, {"wasnt", "was not"},
                {"isnt", "is not"}, {"arent", "are not"}, {"werent", "were not"},
                {"havent", "have not"}, {"hasnt", "has not"}, {"hadnt", "had not"},
                {"wont", "will not"}, {"aint", "is not"}, {"idk", "i do not know"},
                {"ngl", "honestly"}, {"tbh", "honestly"}, {"imo", ""}, {"rn", "right now"},
                {"gonna", "going to"}, {"wanna", "want to"}, {"gotta", "got to"},
                {"kinda", "kind of"}, {"sorta", "sort of"}, {"u", "you"}, {"ur", "your"},
                {"bc", "because"}, {"cuz", "because"}, {"cos", "because"}, {"tho", "though"},
                {"thx", "thanks"}, {"ty", "thanks"}, {"bf", "boyfriend"}, {"gf", "girlfriend"},
                {"bff", "best friend"}, {"alot", "a lot"}, {"irl", "in real life"},
                {"tmrw", "tomorrow"}, {"tmr", "tomorrow"}, {"tonite", "tonight"},
                {"stressful", "stressful"}, {"ok", "okay"}, {"okey", "okay"}, {"k", "okay"},
                {"doomscrolling", "doom scrolling"}, {"burnout", "burnout"},
                {"hahaha", "haha"}, {"hahah", "haha"}, {"lmfao", "lmao"}, {"lolol", "lol"},
                {"yesss", "yes"}, {"noooo", "no"}, {"ughh", "ugh"}, {"ughhh", "ugh"},
        };
        for (String[] pair : slang) {
            SLANG.put(pair[0], pair[1]);
        }

        // Multi-word modifiers become single tokens so lookback windows treat them as one unit.
        String[][] joins = {
                {"a little bit", "a_little"}, {"a little", "a_little"}, {"a bit", "a_bit"},
                {"kind of", "kind_of"}, {"sort of", "sort_of"}, {"a tad", "a_tad"},
                {"no longer", "no_longer"}, {"not at all", "not"},
        };
        for (String[] pair : joins) {
            JOINS.put(pair[0], pair[1]);
        }

        mapEmoji("emo_happy", "😀😃😄😁😊🙂☺😆😸😺");
        mapEmoji("emo_laugh", "😂🤣");
        mapEmoji("emo_love", "😍🥰😘💕💖💗💓💞💘❤💜💙🧡💛💚🤍🖤");
        mapEmoji("emo_sad", "😢🙁☹😞😔😿");
        mapEmoji("emo_cry", "😭");
        mapEmoji("emo_heartbreak", "💔");
        mapEmoji("emo_angry", "😡😠🤬👿");
        mapEmoji("emo_anxious", "😰😨😱😬😟😥😖");
        mapEmoji("emo_stressed", "😫😩🤯");
        mapEmoji("emo_sleepy", "😴💤😪");
        mapEmoji("emo_yawn", "🥱");
        mapEmoji("emo_pray", "🙏");
        mapEmoji("emo_relieved", "😌");
        mapEmoji("emo_calm", "🧘🍃🌿");
        mapEmoji("emo_party", "🤩🥳🎉🎊");
        mapEmoji("emo_meh", "😐😑😶");
        mapEmoji("emo_eyeroll", "🙄😒");
        mapEmoji("emo_tearsmile", "🥲");
        mapEmoji("emo_strong", "💪🏆");
        mapEmoji("emo_sparkle", "✨🌈☀🌞");
        mapEmoji("emo_unsure", "😕🫤");
        mapEmoji("emo_plead", "🥺");
        mapEmoji("emo_cool", "😎");

        // Order matters: more specific patterns first.
        emoticon("</3", "emo_heartbreak");
        emoticon("<3+", "emo_love");
        emoticon(":'-?\\(", "emo_cry");
        emoticon(">:-?\\(", "emo_angry");
        emoticon(":-?\\(+", "emo_sad");
        emoticon("(?<![A-Za-z]):-?\\)+|(?<![A-Za-z]):-?D+\\b|\\(:|;-?\\)|\\bx-?D+\\b", "emo_happy");
        emoticon("(?<![A-Za-z/]):-?/(?!/)", "emo_unsure");
        emoticon(":-?\\|", "emo_meh");
    }

    private TextNormalizer() { }

    private static void mapEmoji(String token, String chars) {
        chars.codePoints().forEach(cp -> EMOJI.put(cp, token));
    }

    private static void emoticon(String regex, String token) {
        EMOTICONS.add(Map.entry(Pattern.compile(regex), token));
    }

    /** Splits a diary entry into analysed sentences. */
    public static List<Sentence> sentences(String text) {
        String prepared = replaceSymbols(text == null ? "" : text);
        List<Sentence> out = new ArrayList<>();
        for (String raw : SENTENCE_SPLIT.split(prepared)) {
            String s = raw.strip();
            if (s.isEmpty()) {
                continue;
            }
            List<Token> tokens = tokenize(s);
            if (tokens.stream().allMatch(Token::isBoundary)) {
                continue;
            }
            int excl = (int) s.chars().filter(c -> c == '!').count();
            out.add(new Sentence(tokens, excl, s.endsWith("?")));
        }
        return out;
    }

    /** Normalized words for a lexicon phrase, e.g. "can't wait" becomes [can, not, wait]. */
    public static List<String> words(String phrase) {
        return tokenize(replaceSymbols(phrase)).stream().map(Token::word).toList();
    }

    // ------------------------------------------------------------------ internals

    private static String replaceSymbols(String text) {
        String t = text.replace('’', '\'').replace('‘', '\'').replace('“', '"').replace('”', '"');
        for (Map.Entry<Pattern, String> e : EMOTICONS) {
            t = e.getKey().matcher(t).replaceAll(" " + e.getValue() + " ");
        }
        StringBuilder sb = new StringBuilder(t.length() + 16);
        t.codePoints().forEach(cp -> {
            String token = EMOJI.get(cp);
            if (token != null) {
                sb.append(' ').append(token).append(' ');
            } else if (cp != 0xFE0F && cp != 0x200D) {     // drop emoji variation selectors / joiners
                sb.appendCodePoint(cp);
            }
        });
        return sb.toString();
    }

    private static List<Token> tokenize(String sentence) {
        List<Token> tokens = new ArrayList<>();
        Matcher m = RAW_TOKEN.matcher(sentence);
        while (m.find()) {
            String raw = m.group();
            boolean caps = raw.length() >= 2 && raw.chars().anyMatch(Character::isLetter)
                    && raw.equals(raw.toUpperCase(Locale.ROOT)) && !raw.startsWith("emo_");
            String lower = raw.toLowerCase(Locale.ROOT);
            boolean elongated = false;
            if (!lower.startsWith("emo_")) {
                Matcher st = STRETCHED.matcher(lower);
                if (st.find()) {
                    elongated = true;
                    lower = st.replaceAll("$1$1");
                }
            }
            for (String w : expand(lower)) {
                tokens.add(new Token(w, caps, elongated));
            }
        }
        return joinModifiers(tokens);
    }

    /** Expands contractions and slang into normalized words. */
    private static List<String> expand(String w) {
        String slang = SLANG.get(w);
        if (slang != null) {
            return slang.isEmpty() ? List.of() : List.of(slang.split(" "));
        }
        if (w.equals("let's")) {
            return List.of("let", "us");
        }
        if (w.equals("can't")) {
            return List.of("can", "not");
        }
        if (w.equals("won't")) {
            return List.of("will", "not");
        }
        if (w.equals("ain't")) {
            return List.of("is", "not");
        }
        if (w.equals("shan't")) {
            return List.of("shall", "not");
        }
        if (w.endsWith("n't")) {
            return List.of(w.substring(0, w.length() - 3), "not");
        }
        int apos = w.indexOf('\'');
        if (apos > 0) {
            String base = w.substring(0, apos);
            String suffix = w.substring(apos + 1);
            return switch (suffix) {
                case "m" -> List.of(base, "am");
                case "re" -> List.of(base, "are");
                case "ve" -> List.of(base, "have");
                case "ll" -> List.of(base, "will");
                case "d" -> List.of(base, "would");
                case "s" -> IS_BASES.contains(base) ? List.of(base, "is") : List.of(base); // "it's" vs "mom's"
                default -> List.of(base + suffix);
            };
        }
        return List.of(w);
    }

    private static List<Token> joinModifiers(List<Token> in) {
        List<Token> out = new ArrayList<>(in.size());
        int i = 0;
        outer:
        while (i < in.size()) {
            for (int len = 3; len >= 2; len--) {
                if (i + len <= in.size()) {
                    StringBuilder key = new StringBuilder();
                    for (int k = 0; k < len; k++) {
                        if (k > 0) {
                            key.append(' ');
                        }
                        key.append(in.get(i + k).word());
                    }
                    String joined = JOINS.get(key.toString());
                    if (joined != null) {
                        boolean caps = false;
                        for (int k = 0; k < len; k++) {
                            caps |= in.get(i + k).caps();
                        }
                        out.add(new Token(joined, caps, false));
                        i += len;
                        continue outer;
                    }
                }
            }
            out.add(in.get(i));
            i++;
        }
        return out;
    }
}
