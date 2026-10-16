package com.mooddiary.analysis;

import static com.mooddiary.model.Mood.ANGRY;
import static com.mooddiary.model.Mood.ANXIOUS;
import static com.mooddiary.model.Mood.BORED;
import static com.mooddiary.model.Mood.CALM;
import static com.mooddiary.model.Mood.CONFIDENT;
import static com.mooddiary.model.Mood.EXCITED;
import static com.mooddiary.model.Mood.GRATEFUL;
import static com.mooddiary.model.Mood.HAPPY;
import static com.mooddiary.model.Mood.HEARTBROKEN;
import static com.mooddiary.model.Mood.HOPEFUL;
import static com.mooddiary.model.Mood.LONELY;
import static com.mooddiary.model.Mood.LOVED;
import static com.mooddiary.model.Mood.NOSTALGIC;
import static com.mooddiary.model.Mood.SAD;
import static com.mooddiary.model.Mood.STRESSED;
import static com.mooddiary.model.Mood.TIRED;

/**
 * The built-in emotion vocabulary. Weights are roughly:
 * 1.2+ unmistakable, 1.0 explicit feeling word, 0.6-0.8 strong cue,
 * 0.3-0.5 situational hint. A term may point to several moods.
 *
 * <p>Phrases are written naturally ("can't wait"); the lexicon normalizes
 * them with the same pipeline used for diary text.
 */
final class LexiconData {

    private LexiconData() { }

    static void populate(EmotionLexicon.Builder b) {
        happy(b);
        excited(b);
        grateful(b);
        loved(b);
        confident(b);
        hopeful(b);
        calm(b);
        nostalgic(b);
        bored(b);
        tired(b);
        stressed(b);
        anxious(b);
        angry(b);
        lonely(b);
        sad(b);
        heartbroken(b);
        everyday(b);
        neutral(b);
    }

    private static void happy(EmotionLexicon.Builder b) {
        b.add(HAPPY, 1.1, "happy", "joyful", "joy", "overjoyed", "delighted", "cheerful", "elated", "jubilant", "blissful", "bliss");
        b.add(HAPPY, 0.9, "glad", "fantastic", "fabulous", "terrific", "wonderful", "awesome", "upbeat", "hilarious", "yay");
        b.add(HAPPY, 0.75, "amazing", "fun", "enjoyed", "enjoy", "pleased", "lovely", "excellent", "superb", "brilliant", "laughed", "laughing",
                "giggled", "giggling", "smiled", "smiling", "grinning", "cheered", "jolly", "merry", "sunny", "great");
        b.add(HAPPY, 0.45, "nice");
        b.add(HAPPY, 0.55, "good", "smile", "laugh", "perfect", "best", "beautiful", "funny", "cheer", "wholesome",
                "celebrate", "celebration", "celebrated", "freedom", "lmao", "satisfying");
        b.add(HAPPY, 0.45, "party");
        b.add(HAPPY, 0.25, "birthday");
        b.add(HAPPY, 0.35, "cool", "haha", "lol", "sunshine", "weekend", "beach", "treat", "dessert", "picnic", "festival",
                "better", "fine dining");
        b.mix("ecstatic", HAPPY, 1.1, EXCITED, 0.7);
        b.mix("thrilled", EXCITED, 1.0, HAPPY, 0.6);
        b.mix("content", HAPPY, 0.45, CALM, 0.7);
        b.mix("satisfied", HAPPY, 0.6, CALM, 0.4, CONFIDENT, 0.2);
        b.mix("heartwarming", HAPPY, 0.7, GRATEFUL, 0.4, LOVED, 0.3);
        b.mix("emo_happy", HAPPY, 0.9);
        b.mix("emo_laugh", HAPPY, 0.8);
        b.mix("emo_sparkle", HAPPY, 0.4, HOPEFUL, 0.3);

        b.add(HAPPY, 1.3, "good mood", "great mood", "in a good mood", "in high spirits", "made my day", "best day", "best day ever",
                "great day", "amazing day", "perfect day", "wonderful day", "fantastic day", "on cloud nine", "over the moon",
                "walking on air", "happy tears", "had a blast");
        b.add(HAPPY, 1.6, "could not be happier", "can not be happier", "never been happier", "happiest i have been");
        b.add(HAPPY, 1.0, "good day", "nice day", "lovely day", "fun day", "had fun", "so much fun", "a lot of fun", "too much fun",
                "having fun", "feel good", "feeling good", "felt good", "feel great", "feeling great", "felt great", "love it",
                "loved it", "love this", "cracked up", "burst out laughing", "laughed so hard", "died laughing",
                "game night", "good time", "great time", "slay", "slayed");
        b.add(HAPPY, 0.7, "pretty good", "really good", "hung out", "hang out", "hanging out", "with friends", "movie night",
                "good news", "great news", "good vibes", "to die for", "treated myself", "treat myself", "caught up with");
        b.mix("feeling better", HAPPY, 0.6, HOPEFUL, 0.6, CALM, 0.3);
        b.mix("feel better", HAPPY, 0.4, HOPEFUL, 0.5);
        b.mix("never felt better", HAPPY, 1.3, CONFIDENT, 0.4);
        b.mix("never been better", HAPPY, 1.3, CONFIDENT, 0.4);
        b.mix("could not be better", HAPPY, 1.3);
        b.mix("happy birthday", HAPPY, 0.4);
        b.mix("feel alive", EXCITED, 0.9, HAPPY, 0.6);
        b.mix("felt alive", EXCITED, 0.9, HAPPY, 0.6);
        b.mix("feel free", CALM, 0.7, HAPPY, 0.5);
    }

    private static void excited(EmotionLexicon.Builder b) {
        b.add(EXCITED, 1.1, "excited", "excitement", "exhilarated", "pumped", "hyped", "stoked", "psyched", "giddy", "woohoo", "emo_party");
        b.add(EXCITED, 0.85, "exciting", "exhilarating", "eager", "energized", "energetic", "buzzing", "electric", "adrenaline", "wow");
        b.add(EXCITED, 0.55, "hype", "omg", "adventure", "concert", "surprise", "anticipation");
        b.add(EXCITED, 0.3, "finally");
        b.add(EXCITED, 0.4, "trip", "vacation", "holiday", "travel", "travelling", "traveling", "flight", "festival", "rush");
        b.add(EXCITED, 1.3, "can not wait", "could not wait", "so ready", "dying to");
        b.add(EXCITED, 0.9, "looking forward", "fired up", "big news", "first date", "road trip", "on fire", "let us go",
                "gave me chills", "new adventure", "bucket list");
        b.mix("got the job", EXCITED, 1.1, CONFIDENT, 0.6, HAPPY, 0.6);
        b.mix("got accepted", EXCITED, 1.0, CONFIDENT, 0.6, HAPPY, 0.5);
        b.mix("got an offer", EXCITED, 1.0, CONFIDENT, 0.5);
        b.mix("new beginning", EXCITED, 0.5, HOPEFUL, 0.7);
        b.mix("big day", EXCITED, 0.6, ANXIOUS, 0.3);
        b.mix("emo_cool", CONFIDENT, 0.6, HAPPY, 0.3);
    }

    private static void grateful(EmotionLexicon.Builder b) {
        b.add(GRATEFUL, 1.2, "grateful", "gratitude", "thankful", "appreciative", "blessed", "fortunate");
        b.add(GRATEFUL, 0.85, "appreciate", "appreciated", "lucky", "blessing", "blessings", "kindness", "generous", "generosity");
        b.add(GRATEFUL, 0.5, "thanks", "thank", "kind", "grace", "heartfelt", "emo_pray");
        b.add(GRATEFUL, 1.4, "counting my blessings", "count my blessings", "so grateful", "means a lot", "meant a lot",
                "meant the world", "means the world", "could not have done it without");
        b.add(GRATEFUL, 0.8, "thank you", "thank god", "thankfully", "helped me", "was there for me", "showed up for me",
                "had my back", "looked out for me", "took care of me");
        b.mix("supportive", GRATEFUL, 0.6, LOVED, 0.5);
        b.mix("support", GRATEFUL, 0.35, LOVED, 0.3);
        b.mix("touched", GRATEFUL, 0.6, LOVED, 0.4);
        b.mix("so lucky", GRATEFUL, 1.0, HAPPY, 0.4);
    }

    private static void loved(EmotionLexicon.Builder b) {
        b.add(LOVED, 1.2, "loved", "cherished", "adored", "romantic", "romance", "soulmate", "emo_love");
        b.add(LOVED, 0.9, "adore", "cherish", "affection", "affectionate", "cuddle", "cuddled", "cuddling", "kissed", "kiss",
                "kissing", "intimate", "sweetheart", "anniversary", "smitten", "darling", "snuggled", "snuggling");
        b.add(LOVED, 0.6, "hug", "hugged", "hugs", "hugging", "boyfriend", "girlfriend", "fiance", "fiancee", "babe", "crush",
                "valued", "connection", "connected");
        b.add(LOVED, 0.4, "partner", "husband", "wife", "sweet", "family", "bestie");
        b.add(LOVED, 0.3, "date", "together");
        b.mix("love", LOVED, 0.7, HAPPY, 0.45);
        b.mix("loving", LOVED, 0.7, HAPPY, 0.4);
        b.add(LOVED, 1.4, "in love", "fell in love", "falling in love", "falling for", "love of my life", "love him", "love her",
                "love you", "feel loved", "felt loved", "feel so loved", "feel cared for", "felt cared for", "cares about me",
                "care about me", "held my hand", "holding hands", "date night", "went on a date", "proposed", "she said yes",
                "he said yes", "i said yes");
        b.mix("said yes", HAPPY, 0.4, CONFIDENT, 0.4);
        b.add(LOVED, 0.8, "cared for", "felt accepted", "feel accepted", "feel seen", "felt seen", "best friend", "quality time",
                "family dinner", "loves me", "love them", "my person", "butterflies");
        b.mix("butterflies", LOVED, 0.7, EXCITED, 0.4, ANXIOUS, 0.2);
        b.mix("wedding", HAPPY, 0.5, LOVED, 0.5, EXCITED, 0.3);
        b.mix("engaged", LOVED, 0.7, EXCITED, 0.8);
        b.mix("emo_plead", SAD, 0.3, LONELY, 0.3, LOVED, 0.2);
        b.mix("would love to", HOPEFUL, 0.4, EXCITED, 0.3);
    }

    private static void confident(EmotionLexicon.Builder b) {
        b.add(CONFIDENT, 1.2, "confident", "proud", "accomplished", "empowered", "unstoppable", "fearless", "triumphant");
        b.add(CONFIDENT, 0.9, "confidence", "pride", "accomplishment", "achievement", "achieved", "succeeded", "success",
                "successful", "nailed", "aced", "victory", "promoted", "promotion", "motivated", "determined", "capable",
                "breakthrough", "productive", "emo_strong");
        b.add(CONFIDENT, 0.6, "won", "win", "winning", "strong", "stronger", "powerful", "brave", "courage", "bold",
                "progress", "passed", "focused", "skilled", "competent");
        b.add(CONFIDENT, 0.3, "ready", "prepared");
        b.add(CONFIDENT, 1.3, "crushed it", "killed it", "killing it", "nailed it", "smashed it", "aced it", "i got this",
                "got this", "believe in myself", "proud of myself", "so proud", "personal best", "finally did it",
                "on top of the world", "on top of things", "in control", "leveled up", "did it", "crushing it");
        b.add(CONFIDENT, 0.7, "proud of me", "good job", "well done", "worked hard", "hard work", "good at", "got an a",
                "passed my", "passed the", "figured it out", "stood up for myself", "spoke up", "no doubt");
        b.mix("on top of the world", CONFIDENT, 1.0, HAPPY, 0.8);
    }

    private static void hopeful(EmotionLexicon.Builder b) {
        b.add(HOPEFUL, 1.2, "hopeful", "optimistic", "optimism", "encouraged", "uplifted", "reassured");
        b.add(HOPEFUL, 0.8, "hope", "hoping", "promising", "encouraging", "inspired", "inspiring", "healing", "improving",
                "faith", "brighter", "motivating", "recovering", "recovery");
        b.add(HOPEFUL, 0.45, "hopefully", "dream", "dreams", "goal", "goals", "future", "improve", "pray", "praying");
        b.add(HOPEFUL, 1.3, "things are looking up", "looking up", "better tomorrow", "tomorrow will be better",
                "tomorrow is a new day", "fresh start", "new start", "new chapter", "silver lining",
                "light at the end of the tunnel", "getting better", "it will be okay", "it is going to be okay",
                "it will be fine", "going to be fine", "going to be okay", "will be okay", "will get through this",
                "get through this", "turn things around", "second chance", "one day at a time", "baby steps",
                "small wins", "moving forward", "step in the right direction", "feel lighter", "felt lighter");
        b.mix("i hope", HOPEFUL, 0.8);
        b.mix("i wish", HOPEFUL, 0.3, SAD, 0.3, LONELY, 0.15);
        b.mix("wish", HOPEFUL, 0.3, SAD, 0.2);
    }

    private static void calm(EmotionLexicon.Builder b) {
        b.add(CALM, 1.2, "calm", "peaceful", "relaxed", "serene", "tranquil", "zen", "emo_relieved", "emo_calm");
        b.add(CALM, 0.95, "relaxing", "relax", "relaxation", "peace", "mellow", "soothing", "soothed", "cozy", "cosy", "unwind",
                "unwinding", "unwound", "meditated", "meditation", "meditate", "balanced", "centered", "grounded", "chilled", "comfy",
                "rested", "refreshed", "recharged");
        b.add(CALM, 0.6, "chill", "comfortable", "steady", "yoga", "easygoing", "recharge", "quiet", "stroll", "gentle", "slow morning",
                "breathe", "breathing", "fine", "alright", "all right", "okay", "nap", "napped");
        b.add(CALM, 0.2, "walk");
        b.add(CALM, 0.35, "tea", "bath", "reading", "nature", "garden", "gardening", "candle", "sunset", "ocean", "lake", "rain sounds");
        b.mix("relieved", CALM, 0.9, HAPPY, 0.4);
        b.mix("relief", CALM, 0.9, HAPPY, 0.3);
        b.add(CALM, 1.3, "at ease", "at peace", "peace of mind", "stress free", "weight off my shoulders", "breathe easy",
                "took it easy", "taking it easy", "lazy sunday", "chilled out", "chill day", "calm day", "quiet day", "no stress",
                "self care", "slept well", "good sleep", "slept great", "slept in", "feel rested", "felt rested",
                "nothing to worry about", "under control", "all good");
        b.add(CALM, 0.6, "not bad", "not too bad", "not that bad", "not so bad", "no worries", "no problem", "went for a walk",
                "take it easy", "alone time", "me time", "free time", "oh well", "it is what it is", "fine i guess");
        b.mix("deep breath", CALM, 0.3, ANXIOUS, 0.3);
        b.mix("deep breaths", CALM, 0.3, ANXIOUS, 0.4);
        b.mix("calm down", ANXIOUS, 0.3, ANGRY, 0.2);
    }

    private static void nostalgic(EmotionLexicon.Builder b) {
        b.add(NOSTALGIC, 1.4, "nostalgic", "nostalgia", "reminisce", "reminiscing", "reminisced", "throwback", "sentimental");
        b.add(NOSTALGIC, 0.9, "memories", "childhood", "yearbook", "reunion", "hometown", "longing", "remembering", "vintage");
        b.add(NOSTALGIC, 0.5, "memory", "remember", "remembered", "ago", "grandma", "grandpa");
        b.add(NOSTALGIC, 0.25, "past", "old");
        b.add(NOSTALGIC, 1.4, "good old days", "old times", "back in the day", "trip down memory lane", "memory lane",
                "takes me back", "took me back", "brought back memories", "brings back memories", "miss those days",
                "miss the old", "when i was a kid", "when i was little", "when i was young", "when we were kids",
                "old photos", "old pictures", "old videos", "photo album", "old letters", "old friend", "old friends",
                "years ago", "back then", "college days", "high school", "growing up", "used to");
        b.mix("bittersweet", NOSTALGIC, 1.1, SAD, 0.4, HAPPY, 0.2);
        b.mix("emo_tearsmile", NOSTALGIC, 0.4, SAD, 0.4, HAPPY, 0.3);
        b.mix("homesick", LONELY, 1.1, NOSTALGIC, 0.7, SAD, 0.4);
    }

    private static void bored(EmotionLexicon.Builder b) {
        b.add(BORED, 1.3, "bored", "boredom", "uneventful", "monotonous", "uninterested", "emo_meh");
        b.add(BORED, 1.0, "boring", "dull", "tedious", "meh", "blah", "repetitive", "mundane", "pointless", "unstimulated");
        b.add(BORED, 0.4, "whatever", "scrolling", "routine", "lazy", "yawn");
        b.add(BORED, 1.3, "nothing to do", "nothing happened", "nothing interesting", "nothing new", "same old", "same as always",
                "same thing every day", "killing time", "kill time", "dragged on", "so slow", "slow day", "time is dragging",
                "watching the clock", "nothing exciting", "stuck at home", "no plans", "doom scrolling");
        b.mix("restless", BORED, 0.6, ANXIOUS, 0.6);
        b.mix("unmotivated", TIRED, 0.6, BORED, 0.6, SAD, 0.3);
        b.mix("uninspired", BORED, 0.9, SAD, 0.2);
        b.mix("emo_eyeroll", ANGRY, 0.5, BORED, 0.5);
    }

    private static void tired(EmotionLexicon.Builder b) {
        b.add(TIRED, 1.3, "tired", "exhausted", "exhaustion", "drained", "sleepy", "fatigued", "fatigue", "weary", "knackered",
                "lethargic", "groggy", "drowsy", "emo_sleepy", "emo_yawn", "sleep deprived", "jetlagged");
        b.add(TIRED, 0.9, "exhausting", "sluggish", "wiped", "yawning", "sore", "insomnia", "overslept", "nodding off", "tiring");
        b.add(TIRED, 1.5, "worn out", "wiped out", "running on empty", "dead tired", "dead on my feet", "no energy", "zero energy",
                "low energy", "barely slept", "did not sleep", "no sleep", "not enough sleep", "little sleep", "slept badly",
                "slept terribly", "bad sleep", "need a nap", "needed a nap", "could barely keep my eyes open",
                "can barely keep my eyes open", "i am beat", "so sleepy", "jet lag", "no motivation", "crashed early",
                "heavy eyes", "long day", "long week", "long night", "all nighter", "pulled an all nighter");
        b.mix("burned out", TIRED, 1.3, STRESSED, 0.8);
        b.mix("burnt out", TIRED, 1.3, STRESSED, 0.8);
        b.mix("burnout", TIRED, 1.2, STRESSED, 0.8);
        b.mix("could not sleep", TIRED, 0.9, ANXIOUS, 0.6);
        b.mix("can not sleep", TIRED, 0.9, ANXIOUS, 0.6);
        b.mix("got sick", TIRED, 0.7, SAD, 0.3);
        b.mix("sick", TIRED, 0.5, SAD, 0.2);
        b.mix("ill", TIRED, 0.4, SAD, 0.2);
        b.mix("unwell", TIRED, 0.6, SAD, 0.4);
        b.mix("sigh", SAD, 0.35, TIRED, 0.35);
        b.mix("long day", TIRED, 1.0, STRESSED, 0.3);
        b.mix("lie down", TIRED, 0.3);
        b.mix("lay down", TIRED, 0.25);
    }

    private static void stressed(EmotionLexicon.Builder b) {
        b.add(STRESSED, 1.3, "stressed", "overwhelmed", "swamped", "overloaded", "frazzled", "emo_stressed", "overworked");
        b.add(STRESSED, 1.0, "stress", "stressful", "stressing", "overwhelming", "hectic", "pressured", "frantic", "chaotic",
                "deadlines", "workload", "juggling", "fml", "crunch");
        b.add(STRESSED, 0.4, "busy");
        b.add(STRESSED, 0.7, "pressure", "deadline", "chaos", "rushed", "rushing", "tense", "tension", "overtime",
                "ugh", "struggling", "headache", "migraine", "multitasking");
        b.add(STRESSED, 0.35, "behind");
        b.mix("crushing", STRESSED, 0.5, SAD, 0.3);
        b.add(STRESSED, 0.4, "exam", "exams", "finals", "bills", "rent", "taxes", "mess", "problem", "problems", "difficult", "errands", "traffic");
        b.add(STRESSED, 1.5, "so much to do", "too much to do", "a lot on my plate", "on my plate", "under pressure",
                "stretched thin", "at my limit", "breaking point", "falling behind", "can not keep up", "could not keep up",
                "barely keeping up", "crazy busy", "losing my mind", "pulling my hair out", "everything at once",
                "drowning in work", "drowning in", "too many things", "so many things", "no time", "not enough time",
                "running late", "due tomorrow", "due today", "to do list", "back to back");
        b.mix("too much", STRESSED, 0.7, ANXIOUS, 0.2);
        b.mix("nothing went right", STRESSED, 0.9, SAD, 0.6, ANGRY, 0.3);
        b.mix("everything went wrong", STRESSED, 1.0, SAD, 0.5, ANGRY, 0.4);
        b.mix("bad day", SAD, 0.7, STRESSED, 0.6);
        b.mix("rough day", SAD, 0.6, STRESSED, 0.7);
        b.mix("tough day", SAD, 0.5, STRESSED, 0.7, TIRED, 0.3);
        b.mix("hard day", SAD, 0.5, STRESSED, 0.7, TIRED, 0.3);
        b.mix("hard time", SAD, 0.6, STRESSED, 0.5);
        b.mix("terrible day", SAD, 0.9, STRESSED, 0.5);
        b.mix("awful day", SAD, 0.9, STRESSED, 0.5);
        b.mix("horrible day", SAD, 0.9, STRESSED, 0.5);
        b.mix("worst day", SAD, 1.1, STRESSED, 0.6, ANGRY, 0.3);
        b.mix("struggle", STRESSED, 0.5, SAD, 0.4);
        b.mix("messed up", STRESSED, 0.5, SAD, 0.3, ANGRY, 0.3);
        b.mix("screwed up", STRESSED, 0.6, SAD, 0.4);
        b.mix("mistake", STRESSED, 0.4, ANXIOUS, 0.3, SAD, 0.3);
        b.mix("killing me", STRESSED, 0.5, TIRED, 0.3);
        b.mix("stuck in traffic", STRESSED, 0.6, ANGRY, 0.6);
        b.mix("missed the", STRESSED, 0.4);
        b.mix("lost my keys", STRESSED, 0.6);
        b.mix("lost my phone", STRESSED, 0.7, ANXIOUS, 0.3);
        b.mix("just great", ANGRY, 0.5, STRESSED, 0.5);
        b.mix("oh great", ANGRY, 0.5, STRESSED, 0.5);
        b.mix("yeah right", ANGRY, 0.5);
    }

    private static void anxious(EmotionLexicon.Builder b) {
        b.add(ANXIOUS, 1.4, "anxious", "anxiety", "nervous", "worried", "terrified", "panic", "panicked", "panicking",
                "apprehensive", "emo_anxious", "overthinking", "catastrophizing", "dread", "dreading");
        b.add(ANXIOUS, 1.1, "worry", "worrying", "worries", "scared", "afraid", "frightened", "fearful", "fear", "uneasy",
                "jittery", "jitters", "paranoid", "nervousness", "overthink", "dreaded", "spooked", "unsettled", "insecure");
        b.add(ANXIOUS, 0.7, "nerves", "awkward", "uncertain", "uncertainty", "shaking", "trembling", "doubt", "edgy",
                "unsure", "emo_unsure", "interview", "presentation", "scary", "self conscious");
        b.add(ANXIOUS, 0.35, "test", "results", "doctor", "appointment", "sweating", "sweaty");
        b.add(ANXIOUS, 1.7, "panic attack", "anxiety attack", "on edge", "racing thoughts", "mind racing", "mind was racing",
                "mind is racing", "pit in my stomach", "knot in my stomach", "knots in my stomach", "on pins and needles",
                "nerve wracking", "freaking out", "freaked out", "can not stop thinking", "could not stop thinking",
                "can not breathe", "could not breathe", "hyperventilating", "tight chest", "chest felt tight", "chest is tight",
                "heart racing", "heart was racing", "heart pounding", "worst case", "doubting myself", "what if");
        b.add(ANXIOUS, 0.9, "sick to my stomach", "not sure what to do", "do not know what to do", "waiting to hear",
                "test results", "scared me", "keep thinking about", "kept thinking about", "can not relax",
                "could not relax", "can not focus", "could not focus");
        b.add(ANXIOUS, 0.4, "not sure", "on my mind");
        b.mix("embarrassed", ANXIOUS, 0.8, SAD, 0.4);
        b.mix("embarrassing", ANXIOUS, 0.7, SAD, 0.3);
        b.mix("ashamed", SAD, 0.8, ANXIOUS, 0.5);
        b.mix("guilty", SAD, 0.6, ANXIOUS, 0.6);
        b.mix("guilt", SAD, 0.6, ANXIOUS, 0.6);
        b.mix("humiliated", SAD, 0.7, ANXIOUS, 0.6, ANGRY, 0.4);
        b.mix("do not know", ANXIOUS, 0.1);
    }

    private static void angry(EmotionLexicon.Builder b) {
        b.add(ANGRY, 1.4, "angry", "furious", "livid", "enraged", "rage", "irate", "pissed", "outraged", "infuriated",
                "infuriating", "seething", "fuming", "emo_angry", "dammit", "wtf");
        b.add(ANGRY, 1.0, "anger", "mad", "annoyed", "irritated", "resentful", "resent", "hate", "hated", "hatred",
                "disrespected", "insulted", "offended", "hostile", "raging", "frustrated", "frustration", "fuck", "grr");
        b.add(ANGRY, 0.75, "annoying", "irritating", "irritable", "frustrating", "bitter", "disgusted", "disgusting", "rude",
                "unfair", "injustice", "yelled", "yelling", "argument", "argued", "arguing", "fought", "grumpy", "cranky",
                "outrageous", "ridiculous", "disrespectful", "hating", "idiot", "idiots", "jerk", "screamed", "screaming", "blamed");
        b.add(ANGRY, 0.45, "fight", "stupid", "shit", "smh", "slammed", "scream", "goddamn", "jealous", "jealousy", "envious",
                "envy", "fucking", "damn", "petty", "unbelievable", "nonsense");
        b.add(ANGRY, 1.6, "pissed off", "fed up", "sick and tired", "can not stand", "could not stand", "lost my temper",
                "blood boiling", "blood was boiling", "how dare", "wanted to scream", "want to scream", "drives me crazy",
                "drove me crazy", "drives me insane", "got on my nerves", "gets on my nerves", "had enough", "not fair",
                "pain in the neck", "so done with", "last straw", "snapped at");
        b.mix("sick of", ANGRY, 1.0, TIRED, 0.4);
        b.mix("frustrated", ANGRY, 1.0, STRESSED, 0.4);
        b.mix("betrayed", ANGRY, 1.0, HEARTBROKEN, 0.6, SAD, 0.4);
        b.mix("betrayal", ANGRY, 1.0, HEARTBROKEN, 0.6);
        b.mix("lied to me", ANGRY, 1.0, HEARTBROKEN, 0.5, SAD, 0.3);
        b.mix("ignored me", SAD, 0.5, LONELY, 0.5, ANGRY, 0.5);
        b.mix("unappreciated", SAD, 0.6, ANGRY, 0.6);
        b.mix("taken for granted", SAD, 0.6, ANGRY, 0.7);
        b.mix("upset", SAD, 0.7, ANGRY, 0.5);
        b.mix("lost the game", SAD, 0.4, ANGRY, 0.4);
    }

    private static void lonely(EmotionLexicon.Builder b) {
        b.add(LONELY, 1.5, "lonely", "loneliness", "lonesome", "friendless", "isolated", "isolation", "outsider");
        b.add(LONELY, 1.0, "alone", "excluded", "invisible", "unwanted", "disconnected", "abandoned", "forgotten");
        b.add(LONELY, 0.4, "miss", "missing", "quiet house");
        b.add(LONELY, 0.25, "by myself");
        b.add(LONELY, 1.6, "left out", "nobody cares", "no one cares", "nobody understands", "no one understands",
                "no one to talk to", "nobody to talk to", "no friends", "i have no one", "have nobody", "all by myself",
                "all alone", "on my own", "do not belong", "nobody called", "no one called", "nobody texted",
                "no one texted", "nobody showed up", "no one showed up", "ate alone", "eating alone", "sat alone",
                "wish i had someone", "need someone", "no one noticed", "nobody noticed", "feel invisible", "felt invisible",
                "feel alone", "felt alone", "feel so alone", "felt so alone", "empty house", "everyone else has",
                "everyone was busy", "plans got cancelled", "plans fell through", "cancelled on me", "canceled on me");
        b.mix("miss you", LONELY, 1.0, HEARTBROKEN, 0.3, NOSTALGIC, 0.2);
        b.mix("miss him", LONELY, 0.8, HEARTBROKEN, 0.6);
        b.mix("miss her", LONELY, 0.8, HEARTBROKEN, 0.6);
        b.mix("miss them", LONELY, 0.9, NOSTALGIC, 0.4);
        b.mix("miss my", LONELY, 0.9, NOSTALGIC, 0.4);
        b.mix("missed my", LONELY, 0.8, NOSTALGIC, 0.4);
        b.mix("moved away", LONELY, 0.7, SAD, 0.3, NOSTALGIC, 0.2);
        b.mix("long distance", LONELY, 0.6, LOVED, 0.3);
        b.mix("empty", SAD, 0.55, LONELY, 0.45);
        b.mix("emptiness", SAD, 0.8, LONELY, 0.7);
    }

    private static void sad(EmotionLexicon.Builder b) {
        b.add(SAD, 1.4, "sad", "sadness", "depressed", "miserable", "sorrow", "grief", "grieving", "mourning", "devastated",
                "hopeless", "hopelessness", "despair", "sobbing", "sobbed", "heartache", "emo_cry", "worthless", "despondent");
        b.add(SAD, 1.1, "unhappy", "depressing", "depression", "cried", "crying", "cry", "tears", "tearful", "weep", "wept",
                "weeping", "gloomy", "glum", "melancholy", "somber", "bummed", "funeral", "discouraged", "defeated", "numb",
                "emo_sad", "dejected", "downcast", "disheartened", "crushed", "heartbroken");
        b.add(SAD, 0.8, "disappointed", "disappointing", "disappointment", "hurting", "hurt", "painful", "useless", "failure",
                "regret", "regrets", "rejected", "rejection", "died", "death", "loss", "teared", "awful", "terrible",
                "horrible", "sucks", "sucked", "demotivated", "unbearable", "pathetic");
        b.add(SAD, 0.45, "sorry");
        b.add(SAD, 0.45, "bad", "worst", "pain", "down", "low", "heavy", "failed", "lost", "bummer", "lowest", "cancelled", "canceled", "rainy");
        b.add(SAD, 1.7, "passed away", "feeling down", "feel down", "felt down", "so down", "down in the dumps", "feeling blue",
                "feel blue", "felt blue", "feeling low", "feel low", "felt low", "in tears", "broke down", "broke down crying",
                "can not stop crying", "could not stop crying", "cried myself to sleep", "heavy heart", "rock bottom",
                "dark place", "falling apart", "fell apart", "feel empty", "felt empty", "feel nothing", "felt nothing",
                "what is the point", "no point", "not good enough", "let me down", "let down", "lost my job", "got fired",
                "laid off", "feel like crying", "felt like crying", "want to cry", "wanted to cry", "lump in my throat",
                "everything hurts", "feel lost", "felt lost", "i failed", "hate myself", "hate my life", "can not go on",
                "can not do this anymore", "give up", "giving up", "gave up", "feel hopeless", "feel worthless", "teared up");
        b.mix("lost my job", SAD, 1.1, ANXIOUS, 0.7, STRESSED, 0.4);
        b.mix("got fired", SAD, 1.0, ANXIOUS, 0.6, ANGRY, 0.5);
        b.mix("laid off", SAD, 1.0, ANXIOUS, 0.7, STRESSED, 0.4);
        b.mix("feel lost", SAD, 0.9, ANXIOUS, 0.6);
        b.mix("felt lost", SAD, 0.9, ANXIOUS, 0.6);
        b.mix("falling apart", SAD, 1.1, STRESSED, 0.6);
        b.mix("not good enough", SAD, 1.1, ANXIOUS, 0.5);
        b.mix("give up", SAD, 0.8, TIRED, 0.5);
        b.mix("giving up", SAD, 0.9, TIRED, 0.5);
        b.mix("hate myself", SAD, 1.5, ANGRY, 0.3);
        b.mix("numb", SAD, 1.0, TIRED, 0.4);
        b.mix("defeated", SAD, 1.0, TIRED, 0.4);
        b.mix("let me down", SAD, 0.9, ANGRY, 0.4);
        b.mix("failure", SAD, 0.8, ANXIOUS, 0.3);
        b.mix("regret", SAD, 0.8, ANXIOUS, 0.2, NOSTALGIC, 0.2);
        b.mix("hurt", SAD, 0.7, HEARTBROKEN, 0.4, ANGRY, 0.2);
        b.mix("crushed", SAD, 0.8, HEARTBROKEN, 0.7);
        b.mix("rejected", SAD, 0.8, HEARTBROKEN, 0.5, LONELY, 0.3);
        b.mix("melancholy", SAD, 1.0, NOSTALGIC, 0.3);
        b.mix("awful", SAD, 0.75, ANGRY, 0.25);
        b.mix("terrible", SAD, 0.75, ANGRY, 0.25);
        b.mix("horrible", SAD, 0.75, ANGRY, 0.25);
        b.mix("sucks", SAD, 0.6, ANGRY, 0.35);
        b.mix("sucked", SAD, 0.6, ANGRY, 0.35);
        b.mix("teared up", SAD, 0.6, GRATEFUL, 0.2);
        b.mix("hurt my", SAD, 0.2);
        b.mix("feel like a failure", SAD, 1.4, ANXIOUS, 0.4);
    }

    private static void heartbroken(EmotionLexicon.Builder b) {
        b.add(HEARTBROKEN, 1.6, "heartbroken", "heartbreak", "emo_heartbreak", "unrequited", "dumped", "breakup", "ghosted");
        b.add(HEARTBROKEN, 0.9, "ex", "divorce", "divorced", "shattered", "separated", "heartache");
        b.add(HEARTBROKEN, 1.7, "broken heart", "broke my heart", "heart is broken", "heart broke", "my heart hurts",
                "broke up", "break up", "broken up", "split up", "dumped me", "left me", "my ex", "cheated on me",
                "does not love me", "did not love me", "does not want me", "do not want me", "still love him",
                "still love her", "still in love with", "can not get over", "get over him", "get over her", "relationship ended",
                "ended things", "called it off", "single again", "lost the love", "torn apart", "moved on without me",
                "with someone else", "blocked me", "unfollowed me", "over between us", "never loved me");
        b.mix("cheated", HEARTBROKEN, 1.0, ANGRY, 0.9);
        b.mix("cheated on me", HEARTBROKEN, 1.6, ANGRY, 1.0);
        b.mix("ghosted", HEARTBROKEN, 1.0, LONELY, 0.5, ANGRY, 0.3);
        b.mix("shattered", HEARTBROKEN, 0.9, SAD, 0.7);
        b.mix("divorce", HEARTBROKEN, 1.0, SAD, 0.6);
        b.mix("left me", HEARTBROKEN, 1.2, LONELY, 0.5);
    }

    /** Everyday situations and idioms that imply a mood without naming it. */
    private static void everyday(EmotionLexicon.Builder b) {
        // stressed
        b.add(STRESSED, 1.3, "drowning", "disaster", "nightmare", "hold it together", "holding it together",
                "need a break", "running around", "do not even know where to start", "do not know where to start",
                "too much on my plate", "spread too thin", "never ending", "endless", "one thing after another",
                "can not catch a break", "no time to breathe", "car broke down", "laptop broke", "phone broke",
                "broke down on the", "still have to", "have to finish", "so much work", "piling up", "piled up");
        b.mix("disaster", STRESSED, 1.0, SAD, 0.3);
        b.mix("nightmare", STRESSED, 1.0, ANXIOUS, 0.4);
        b.mix("need a break", STRESSED, 0.9, TIRED, 0.6);
        b.add(STRESSED, 0.35, "inbox", "meeting", "meetings", "commute");
        b.mix("it will get done", HOPEFUL, 0.4, CALM, 0.2);
        // angry
        b.add(ANGRY, 1.2, "unacceptable", "done being nice", "so done", "done with this", "i am done with",
                "who does that", "keyed my car", "the audacity", "making excuses", "third time i have asked",
                "every single time", "drives me nuts", "makes my blood boil", "take credit", "took credit");
        b.add(ANGRY, 0.5, "excuses", "excuse", "rude to me", "mad at");
        b.mix("mad at me", ANXIOUS, 0.6, ANGRY, 0.3, SAD, 0.2);
        // lonely
        b.add(LONELY, 1.3, "talk to nobody", "talk to no one", "spoke to nobody", "spoke to no one", "coupled up",
                "everyone i know is", "nobody to call", "no one to call", "nobody around", "no one around",
                "eat dinner in silence", "in silence", "on my own again", "nobody checks on me", "no one checks on me",
                "third wheel", "no one invited me", "nobody invited me", "not invited", "everyone else is");
        b.mix("moved out", LONELY, 0.5, SAD, 0.3);
        b.mix("someone around", LONELY, 0.6);
        // tired
        b.add(TIRED, 1.3, "zombie", "nothing left in the tank", "left in the tank", "body aches", "whole body aches",
                "need sleep", "need some sleep", "need to sleep", "night of sleep", "hour shift", "double shift",
                "nodded off", "dozed off", "passed out", "could sleep for a week", "up all night", "woke up exhausted",
                "can barely function", "running on fumes", "on autopilot", "went to bed early", "go to bed",
                "do not have anything left", "have nothing left", "eyes are heavy", "my eyes hurt");
        b.add(TIRED, 0.5, "aches", "aching", "bed", "coffees", "caffeine");
        b.mix("up four times", TIRED, 1.0);
        // bored
        b.add(BORED, 1.2, "stare at the wall", "stared at the wall", "staring at the wall", "staring at my screen",
                "hours crawl", "time crawls", "crawling by", "dragging on", "nothing on tv", "doing absolutely nothing",
                "doing nothing", "lying on the couch", "binge watched", "watched every episode", "another day in the office",
                "so dull", "bored out of my mind", "nothing to look forward to");
        b.add(BORED, 0.6, "crawl", "another day");
        // calm
        b.add(CALM, 1.3, "nowhere to be", "nothing to do and nowhere to be", "phone off", "did not think about work",
                "slow down for a change", "to slow down", "slowed down", "quiet evening", "quiet night", "peaceful evening",
                "listened to the rain", "candles lit", "lit a candle", "finished my book", "curled up", "warm blanket",
                "no rush", "no pressure", "breathing easier", "came back negative", "came back clear", "all clear");
        // excited
        b.add(EXCITED, 1.3, "closed on the house", "keys in hand", "got the keys", "move in day", "moving day", "new house",
                "new home", "dream house", "dream home", "first house", "booked tickets", "booked a trip", "booked flights",
                "booked a flight", "counting down", "counting down the days", "count down", "so excited", "going to see them",
                "we are going", "front row", "sold out", "got tickets", "it is happening", "this is happening",
                "big announcement", "surprise party", "just found out", "planning everything", "can not even");
        b.add(EXCITED, 0.5, "tickets", "planning", "booked");
        // confident
        b.add(CONFIDENT, 1.4, "ten feet tall", "feel like a boss", "felt like a boss", "negotiated", "got a raise",
                "stronger than", "how far i have come", "how far i have come", "proved them wrong", "proved myself",
                "held my own", "owned it", "rocked it", "best work", "really proud");
        b.add(CONFIDENT, 0.4, "salary", "raise", "improved", "learned");
        // hopeful
        b.add(HOPEFUL, 1.3, "way forward", "a way forward", "could actually change", "change things for me",
                "change my life", "starting over", "start over", "in remission", "good feeling about", "a good feeling",
                "first time in months", "first time in years", "light at the end", "worth it in the end", "it will work out",
                "things will get better", "things are getting better", "on the mend", "turning point", "brighter days",
                "better days", "looking forward to", "excited for the future");
        b.add(HOPEFUL, 0.8, "shrunk", "shrinking", "signed up", "long road", "this could", "could change", "applied");
        b.mix("cancer free", HAPPY, 1.3, GRATEFUL, 0.7, HOPEFUL, 0.6);
        // nostalgic
        b.add(NOSTALGIC, 1.4, "wish i could go back", "could go back", "go back in time", "remember when", "i forgot how",
                "video of me", "picture of me", "pictures of us", "photos of us", "as a kid", "when i was", "old kitchen",
                "our old", "my old room", "the way things were", "simpler times", "everything felt simpler", "felt simpler",
                "so much has changed", "who i was back then", "miss who i was", "we used to", "i used to");
        b.mix("used to study", NOSTALGIC, 1.0);
        // heartbroken
        b.add(HEARTBROKEN, 1.6, "not in love with me", "not been in love with me", "no longer in love", "fell out of love",
                "falling out of love", "out of love with me", "packed his things", "packed her things", "packed his stuff",
                "packed her stuff", "moved his stuff out", "moved her stuff out", "asked for a divorce", "wants a divorce",
                "we had something real", "thought we had something", "switch it off", "led me on", "played me",
                "does not feel the same", "did not feel the same", "not the same way", "rejected me");
        b.mix("did not see it coming", SAD, 0.6, HEARTBROKEN, 0.3, ANXIOUS, 0.2);
        b.mix("could not move", SAD, 0.6);
        // sad
        b.add(SAD, 1.4, "put him down", "put her down", "had to put down", "miss him so much", "miss her so much",
                "physically hurts", "such a disappointment", "a disappointment", "like crying", "for no reason",
                "some days are just", "grey", "gray", "so quiet without", "without her", "without him", "the house felt",
                "keep expecting", "i keep expecting");
        b.mix("for no reason", SAD, 0.6);
        b.mix("grey", SAD, 0.5);
        b.mix("gray", SAD, 0.4);
        b.mix("without her", SAD, 0.6, LONELY, 0.4);
        b.mix("without him", SAD, 0.6, LONELY, 0.4);
        b.mix("the house felt", SAD, 0.3, LONELY, 0.3);
        // anxious
        b.add(ANXIOUS, 1.4, "replaying", "over and over", "can not turn my brain off", "turn my brain off",
                "brain will not shut off", "can not shut my brain off", "stomach drops", "stomach dropped",
                "every time my phone buzzes", "hands are shaking", "hands were shaking", "sweaty", "freeze up",
                "i will freeze", "biopsy", "did i sound", "did i say something", "said something wrong", "do something wrong",
                "lying awake", "lay awake", "awake at", "what if they", "what if i", "second guessing", "second guess",
                "starting to panic", "on high alert", "tossing and turning", "scary");
        b.mix("lying in bed", ANXIOUS, 0.3, TIRED, 0.3);
        b.mix("couldn't focus", ANXIOUS, 0.9);
        // happy
        b.add(HAPPY, 1.2, "cheeks hurt from smiling", "cheeks hurt", "kind_of perfect", "on sale", "favorite song",
                "simple happy", "everyone was laughing", "telling stories", "great night", "fun night", "good night out",
                "so cute", "the cutest", "cutest", "brunch", "graduated", "graduation");
        b.mix("graduated", HAPPY, 0.9, CONFIDENT, 0.6, EXCITED, 0.4);
        b.mix("on sale", HAPPY, 0.5);
        b.mix("brunch", HAPPY, 0.5);
        // grateful
        b.add(GRATEFUL, 1.3, "restored my faith", "faith in people", "faith in humanity", "do not say it enough",
                "drove four hours", "sat with me", "so thankful", "thankful for", "grateful for", "lucky to have",
                "thank you whoever you are", "stranger", "out of their way", "went out of their way");
        b.mix("stranger", GRATEFUL, 0.3);
        // loved
        // round 3: patterns found by the blind test
        b.mix("positive", HAPPY, 0.35, HOPEFUL, 0.4);
        b.mix("stay positive", HOPEFUL, 0.6, SAD, 0.2);
        b.mix("trying to stay positive", HOPEFUL, 0.5, SAD, 0.4);
        b.add(SAD, 1.3, "i just broke", "just broke down", "cancer came back", "bad news");
        b.mix("cancer", SAD, 0.6, ANXIOUS, 0.6);
        b.mix("cancer came back", SAD, 1.3, ANXIOUS, 0.7);
        b.mix("diagnosis", ANXIOUS, 0.7, SAD, 0.5);
        b.mix("diagnosed", ANXIOUS, 0.7, SAD, 0.6);
        b.add(ANXIOUS, 1.4, "spiraling", "spiralling", "spiral", "wants to talk", "need to talk", "needs to talk",
                "talk tomorrow", "might get fired", "going to get fired", "keep checking", "kept checking",
                "hate flying", "afraid of flying", "fear of flying", "turbulence", "can not settle", "imagining the worst",
                "worst case scenario", "on high alert", "can not sit still");
        b.mix("getting fired", ANXIOUS, 1.0, SAD, 0.5);
        b.mix("imagining", ANXIOUS, 0.4);
        b.mix("settle down", CALM, 0.5);
        b.add(HEARTBROKEN, 1.3, "needs space", "need space", "wanted space", "it is not working", "not working out",
                "over text", "broke up over text", "the guy she left me for", "the girl he left me for", "left me for",
                "thought i was over it", "thought i was over him", "thought i was over her");
        b.add(CONFIDENT, 1.5, "i can do hard things", "turns out i can", "did it by myself", "did it myself",
                "built it myself", "fixed it myself", "did it on my own", "all on my own", "without breaking down",
                "i did it", "finished building");
        b.add(CONFIDENT, 0.6, "no help", "how much it meant");
        b.add(CONFIDENT, 0.5, "built", "fixed");
        b.add(HOPEFUL, 1.3, "doing something about it", "taking steps", "first step", "first steps",
                "months sober", "days sober", "clean for");
        b.add(HOPEFUL, 0.7, "therapy session", "first session");
        b.mix("sober", HOPEFUL, 0.8, CONFIDENT, 0.6, GRATEFUL, 0.3);
        b.mix("therapy", HOPEFUL, 0.6);
        b.add(HOPEFUL, 0.8, "for once");
        b.add(LOVED, 1.2, "made my favorite", "made me dinner", "cooked for me", "surprised me", "came home to find",
                "left me a note", "brought me flowers", "planned a surprise", "spoiled me", "took care of me");
        b.add(HAPPY, 1.0, "lovely evening", "lovely night", "lovely time", "nice evening", "great evening");
        b.add(HAPPY, 1.3, "did a little dance", "happy dance", "came out right", "came out perfect", "turned out great",
                "turned out perfect", "best time", "had the best time", "so did i");
        b.add(LONELY, 0.6, "everyone else");
        b.add(LONELY, 1.4, "nobody to share", "no one to share", "nobody to", "no one to", "with their families",
                "frozen dinner", "only my mom remembered", "nobody remembered", "no one remembered",
                "forgot my birthday", "by myself on my birthday", "eating cake by myself", "everyone's holiday");
        b.add(BORED, 1.2, "out of desperation", "waited for hours", "nothing to read", "most pointless",
                "no one to see", "reorganized my");
        b.add(GRATEFUL, 1.2, "stopped to help", "people are good", "thank you both", "could not have gotten here");
        b.add(NOSTALGIC, 1.3, "being eleven again", "like a kid again", "like being a kid", "letters he wrote",
                "letters she wrote", "old watch", "old video game", "used to play");
        b.add(NOSTALGIC, 0.6, "the attic", "every summer", "during the war");
        b.add(TIRED, 1.3, "fall asleep standing up", "brain feels like mush", "mush", "nights of bad sleep",
                "every muscle", "can not keep my eyes open", "going to bed at", "eyes keep closing");
        b.add(CALM, 1.2, "mind finally went quiet", "went quiet", "everything was still", "took the long way",
                "nothing pressing", "slow evening", "gentle evening", "cup of tea", "rain on the window");
        b.add(ANGRY, 1.2, "slammed the door", "in my face", "shrugged at me", "two hours on hold");
        b.add(ANGRY, 0.5, "on hold");
        b.add(LOVED, 1.3, "wrote me a letter", "a letter about", "never felt more loved", "years together", "every year we",
                "our anniversary", "tenth anniversary", "lucky to have him", "lucky to have her", "slow danced");
    }

    /** Phrases that look emotional but usually aren't; matching them stops false hits. */
    private static void neutral(EmotionLexicon.Builder b) {
        b.neutral("no wonder", "not only", "nothing but", "never mind", "sat down", "sit down", "slow down", "shut down",
                "write down", "wrote down", "down the street", "down the road", "down the hall", "upside down",
                "down to earth", "empty rooms", "empty room", "good morning", "good night", "good luck", "for good", "pretty much", "as well",
                "can not believe", "could not believe", "lost track of time",
                "lost weight", "the past few", "the past week", "the past month", "in the past", "past few days",
                "kid you not", "just kidding", "dead serious", "happy hour", "fun fact", "bad at", "good grief",
                "free trial", "date of", "due date", "to date", "up to date", "old man", "old woman", "how old",
                "years old", "fine print", "too bad", "not sure if", "i do not mind", "do not worry", "no matter",
                "missed call", "free tonight", "business", "businesses", "badly", "so badly", "walked out of there", "the rest of");
        // A few of those deserve a light nudge instead of nothing.
        b.mix("good luck", HOPEFUL, 0.2);
        b.mix("do not worry", CALM, 0.3);
        b.mix("too bad", SAD, 0.3);
    }
}
