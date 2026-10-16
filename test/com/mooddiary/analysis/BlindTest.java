package com.mooddiary.analysis;

import com.mooddiary.model.Mood;

import java.util.List;

import static com.mooddiary.model.Mood.*;

/** Fresh entries never used for tuning. Measures realistic accuracy. */
public final class BlindTest {

    static final List<AnalyzerAccuracyTest.Case> CASES = List.of(
        new AnalyzerAccuracyTest.Case(HAPPY, "Spent the day at the fair with my nieces. Cotton candy, the ferris wheel, all of it. They had the best time and so did I."),
        new AnalyzerAccuracyTest.Case(SAD, "Got the call that Uncle Ray's cancer came back. Everyone's trying to stay positive but I went to my room and just broke."),
        new AnalyzerAccuracyTest.Case(ANXIOUS, "My boss wants to 'talk tomorrow morning' and didn't say about what. I've been spiraling all evening thinking I'm getting fired."),
        new AnalyzerAccuracyTest.Case(STRESSED, "Moving apartments this weekend and nothing is packed. Work won't let me take Friday off. My to-do list keeps getting longer."),
        new AnalyzerAccuracyTest.Case(ANGRY, "The airline lost my bag and the agent basically shrugged at me. Spent two hours on hold. Unbelievable. I'm livid."),
        new AnalyzerAccuracyTest.Case(TIRED, "I could fall asleep standing up. Three nights of bad sleep in a row and my brain feels like mush."),
        new AnalyzerAccuracyTest.Case(LONELY, "It's my birthday and only my mom remembered. I sat in my apartment eating cake by myself."),
        new AnalyzerAccuracyTest.Case(CALM, "Took the long way home along the river. Sun was setting, everything was still. My mind finally went quiet."),
        new AnalyzerAccuracyTest.Case(EXCITED, "Our band got booked for a real venue!! First proper gig next Friday. I've been practicing nonstop, I'm buzzing."),
        new AnalyzerAccuracyTest.Case(GRATEFUL, "Six months sober today. I couldn't have gotten here without my sponsor and my sister. Thank you both."),
        new AnalyzerAccuracyTest.Case(HEARTBROKEN, "He texted me that he 'needs space' and 'it's not working'. After two years. Over text. I can't stop shaking."),
        new AnalyzerAccuracyTest.Case(CONFIDENT, "Finished building the bookshelf by myself, no help, no YouTube. Looks amazing. Turns out I can do hard things."),
        new AnalyzerAccuracyTest.Case(NOSTALGIC, "Cleaning out the attic I found my grandpa's old watch and the letters he wrote to grandma during the war. Spent hours reading them."),
        new AnalyzerAccuracyTest.Case(BORED, "Waited at the DMV for four hours. Nothing to read, phone died. Longest, most pointless day."),
        new AnalyzerAccuracyTest.Case(HOPEFUL, "First therapy session today. Didn't fix anything, but for once I feel like I'm doing something about it."),
        new AnalyzerAccuracyTest.Case(LOVED, "Came home to find my partner had cleaned the whole house and made my favorite dinner because I'd had a long week. I'm so lucky."),
        new AnalyzerAccuracyTest.Case(ANXIOUS, "Flight's tomorrow and I hate flying. Keep checking the weather and imagining turbulence. Can't settle down."),
        new AnalyzerAccuracyTest.Case(SAD, "Everyone at work got invited to the wedding except me. I laughed it off but I cried in the bathroom."),
        new AnalyzerAccuracyTest.Case(STRESSED, "Tax deadline Monday, my receipts are everywhere and I can't find half of them. My head is pounding."),
        new AnalyzerAccuracyTest.Case(HAPPY, "My sourdough finally came out right!! Crispy crust, perfect crumb. Did a little dance in the kitchen."),
        new AnalyzerAccuracyTest.Case(ANGRY, "My neighbor's dog barked from 5am until noon. I knocked and he slammed the door in my face. I'm seething."),
        new AnalyzerAccuracyTest.Case(TIRED, "Moved furniture all day. Every muscle is sore, I can't keep my eyes open. Going to bed at 8."),
        new AnalyzerAccuracyTest.Case(CALM, "Rain on the window, a cup of tea, and an old movie. Nothing pressing. Just a gentle, slow evening."),
        new AnalyzerAccuracyTest.Case(LONELY, "Scrolled through everyone's holiday photos with their families. Mine is a frozen dinner and the TV. Nobody to share it with."),
        new AnalyzerAccuracyTest.Case(EXCITED, "She said YES!!! We're engaged!!! I can't believe it, I've been planning this for months!!"),
        new AnalyzerAccuracyTest.Case(HEARTBROKEN, "Saw on Instagram that she's engaged to the guy she left me for. I thought I was over it. I'm not."),
        new AnalyzerAccuracyTest.Case(CONFIDENT, "Gave the eulogy without breaking down. I was scared, but I did it and people came up to tell me how much it meant."),
        new AnalyzerAccuracyTest.Case(BORED, "Rainy Tuesday. Nothing to do, no one to see. Reorganized my sock drawer out of desperation."),
        new AnalyzerAccuracyTest.Case(GRATEFUL, "Car broke down on the highway and three strangers stopped to help push it. People are good. I'm thankful."),
        new AnalyzerAccuracyTest.Case(NOSTALGIC, "Played the old video game I used to play with my brother every summer. Felt like being eleven again.")
    );

    public static void main(String[] args) {
        MoodAnalyzer analyzer = MoodAnalyzer.standard();
        int top1 = 0, top2 = 0;
        for (AnalyzerAccuracyTest.Case c : CASES) {
            MoodAnalysis a = analyzer.analyze(c.text());
            boolean ok = a.primary() == c.expected();
            boolean ok2 = ok || a.secondary().map(m -> m == c.expected()).orElse(false);
            if (ok) top1++;
            if (ok2) top2++;
            if (!ok) {
                System.out.printf("XX expected %-11s got %-11s (2nd %s): %s%n", c.expected(), a.primary(),
                        a.secondary().map(Mood::toString).orElse("-"), c.text());
                a.ranked().stream().limit(4).forEach(e -> System.out.printf("        %-11s %.2f%n", e.getKey(), e.getValue()));
                a.evidence().stream().limit(6).forEach(e -> System.out.printf("        %s %.2f%n", e.describe(), e.weight()));
            }
        }
        System.out.printf("%nBlind top-1: %d/%d (%.1f%%)   top-2: %d/%d (%.1f%%)%n",
                top1, CASES.size(), 100.0 * top1 / CASES.size(), top2, CASES.size(), 100.0 * top2 / CASES.size());
    }
}
