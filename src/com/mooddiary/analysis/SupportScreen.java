package com.mooddiary.analysis;

import com.mooddiary.analysis.TextNormalizer.Sentence;
import com.mooddiary.analysis.TextNormalizer.Token;

import java.util.List;

/**
 * Looks for language suggesting the writer might be thinking about
 * self-harm or suicide, so the app can gently surface support resources.
 * It never blocks saving and never changes the detected mood.
 */
final class SupportScreen {

    private static final List<List<String>> PHRASES = java.util.stream.Stream.of(
            "kill myself", "killing myself", "want to die", "wanted to die", "wish i was dead", "wish i were dead",
            "end my life", "end it all", "ending it all", "take my own life", "taking my own life", "suicide",
            "suicidal", "hurt myself", "hurting myself", "harm myself", "self harm", "self harming", "cut myself",
            "cutting myself", "no reason to live", "nothing to live for", "better off without me", "better off dead",
            "do not want to be here anymore", "do not want to live", "do not want to exist", "can not go on",
            "not worth living", "disappear forever", "want to disappear", "wish i could disappear",
            "wish i was never born", "wish i had never been born")
            .map(TextNormalizer::words)
            .toList();

    private SupportScreen() { }

    static boolean flags(List<Sentence> sentences) {
        for (Sentence s : sentences) {
            List<Token> t = s.tokens();
            for (int i = 0; i < t.size(); i++) {
                for (List<String> phrase : PHRASES) {
                    if (matches(t, i, phrase)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean matches(List<Token> t, int start, List<String> phrase) {
        if (start + phrase.size() > t.size()) {
            return false;
        }
        for (int k = 0; k < phrase.size(); k++) {
            if (!t.get(start + k).word().equals(phrase.get(k))) {
                return false;
            }
        }
        return true;
    }
}
