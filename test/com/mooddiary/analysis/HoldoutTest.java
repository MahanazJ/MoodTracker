package com.mooddiary.analysis;

import com.mooddiary.model.Mood;

import java.util.List;

import static com.mooddiary.model.Mood.*;

/** Harder, longer, more naturally phrased entries written separately from the lexicon. */
public final class HoldoutTest {

    static final List<AnalyzerAccuracyTest.Case> CASES = List.of(
        new AnalyzerAccuracyTest.Case(STRESSED, "Dear diary, I don't even know where to start. The client moved the launch up a week, my inbox is a disaster, and I still have to finish the slides tonight. I keep telling myself it'll get done but honestly I feel like I'm drowning."),
        new AnalyzerAccuracyTest.Case(SAD, "Woke up and the house felt so quiet without Biscuit. It's been three days since we had to put him down. I keep expecting to hear his paws on the floor. I miss him so much it physically hurts."),
        new AnalyzerAccuracyTest.Case(HAPPY, "Okay today was kind of perfect?? Brunch with the girls, found the cutest dress on sale, and then sunset at the pier. My cheeks hurt from smiling."),
        new AnalyzerAccuracyTest.Case(ANXIOUS, "I have the biopsy appointment on Thursday and I can't think about anything else. Every time my phone buzzes my stomach drops. I tried to watch a movie but I couldn't focus at all."),
        new AnalyzerAccuracyTest.Case(ANGRY, "Landlord STILL hasn't fixed the heat. Third time I've asked. He just keeps making excuses and I'm done being nice about it. Absolutely unacceptable."),
        new AnalyzerAccuracyTest.Case(LONELY, "Weekends are the worst now. Everyone I know is coupled up or busy with their kids. I spent Saturday talking to nobody except the cashier at the grocery store."),
        new AnalyzerAccuracyTest.Case(TIRED, "Baby was up four times last night. I'm a zombie. Drank three coffees and still nodded off during the afternoon call. I just need one full night of sleep."),
        new AnalyzerAccuracyTest.Case(CONFIDENT, "Six months ago I couldn't do a single pull up. Today I did five. Small thing but I'm really proud of how far I've come. I'm stronger than I give myself credit for."),
        new AnalyzerAccuracyTest.Case(GRATEFUL, "Mom drove four hours just to bring me groceries and sit with me while I was recovering. I don't say it enough but I am so thankful for her."),
        new AnalyzerAccuracyTest.Case(HEARTBROKEN, "Six years together and he packed his things today. He said he hasn't been in love with me for a long time. I didn't see it coming. I sat on the kitchen floor and couldn't move."),
        new AnalyzerAccuracyTest.Case(CALM, "Spent the afternoon in the garden repotting plants. Phone off, radio on. Didn't think about work once. It felt really good to slow down for a change."),
        new AnalyzerAccuracyTest.Case(EXCITED, "WE CLOSED ON THE HOUSE. Keys in hand!!! I've been dreaming about this for years and I keep walking around the empty rooms planning everything. Move-in day is Saturday!!"),
        new AnalyzerAccuracyTest.Case(BORED, "Another day in the office where literally nothing happens. Answered two emails, reorganized my desk, stared at the wall. The hours just crawl."),
        new AnalyzerAccuracyTest.Case(NOSTALGIC, "Mom sent me a video of me at my fifth birthday party. I watched it like ten times. I forgot how small our old kitchen was. Wish I could go back for just one day."),
        new AnalyzerAccuracyTest.Case(HOPEFUL, "Got the scan results and the tumor has shrunk. Still a long road but for the first time in months I feel like there's a way forward."),
        new AnalyzerAccuracyTest.Case(LOVED, "Our tenth anniversary. He wrote me a letter about every year we've been together and I cried reading it. I'm so lucky to have him. I've never felt more loved."),
        new AnalyzerAccuracyTest.Case(ANXIOUS, "Presentation in front of the whole department tomorrow. I've rehearsed it a hundred times but I'm terrified I'll freeze. Hands are already sweaty thinking about it."),
        new AnalyzerAccuracyTest.Case(SAD, "Failed the bar exam. Again. I studied for months. I don't know how to tell my parents. I just feel like such a disappointment."),
        new AnalyzerAccuracyTest.Case(STRESSED, "Kids were sick, the car broke down, and I had a deadline at five. I was running around all day trying to hold it together. I need a break so badly."),
        new AnalyzerAccuracyTest.Case(ANGRY, "Someone keyed my car in the parking lot and drove off. Who does that?! I wanted to scream. I'm still shaking with rage."),
        new AnalyzerAccuracyTest.Case(HAPPY, "My little brother graduated today! The whole family came out, we had a huge dinner and everyone was laughing and telling stories. Such a great night."),
        new AnalyzerAccuracyTest.Case(TIRED, "I don't have anything left in the tank. Twelve hour shift, came home, ate cereal standing up. My whole body aches. Bed."),
        new AnalyzerAccuracyTest.Case(LONELY, "My roommate moved out and now the apartment is so empty. I eat dinner in silence. I didn't realize how much I'd miss just having someone around."),
        new AnalyzerAccuracyTest.Case(CALM, "Rainy evening, candles lit, finished my book. Nothing to do and nowhere to be. Content."),
        new AnalyzerAccuracyTest.Case(HEARTBROKEN, "She blocked me everywhere. I guess that's my answer. I really thought we had something real. I don't understand how someone can just switch it off."),
        new AnalyzerAccuracyTest.Case(CONFIDENT, "Negotiated my salary for the first time and they said yes to everything. I walked out of there feeling ten feet tall."),
        new AnalyzerAccuracyTest.Case(SAD, "Some days are just grey. Didn't do anything wrong, nothing bad happened, I just feel heavy and like crying for no reason."),
        new AnalyzerAccuracyTest.Case(EXCITED, "Just booked tickets to Iceland for my birthday!! Northern lights, hot springs, the whole thing. I'm counting down the days."),
        new AnalyzerAccuracyTest.Case(GRATEFUL, "Lost my wallet on the train and some stranger tracked me down to return it with everything inside. Restored my faith in people. Thank you, whoever you are."),
        new AnalyzerAccuracyTest.Case(ANXIOUS, "Lying in bed at 2am replaying that conversation over and over. Did I sound rude? Is she mad at me? I can't turn my brain off."),
        new AnalyzerAccuracyTest.Case(BORED, "Snow day. Watched every episode of the show already. Nothing on TV, nobody to call, just lying on the couch doing absolutely nothing. So dull."),
        new AnalyzerAccuracyTest.Case(HOPEFUL, "Signed up for the community college program today. It's scary starting over at 34 but I feel like this could actually change things for me."),
        new AnalyzerAccuracyTest.Case(STRESSED, "Wedding is in two weeks and the caterer cancelled, the seating chart is a nightmare and my mom keeps calling with new demands. I can't keep up."),
        new AnalyzerAccuracyTest.Case(NOSTALGIC, "Walked through my old campus today. The coffee shop where we used to study is a bank now. So much has changed. I miss who I was back then."),
        new AnalyzerAccuracyTest.Case(ANGRY, "My manager made me redo the entire report because of one typo and then blamed me in front of the team. I'm so done with this place."),
        new AnalyzerAccuracyTest.Case(HAPPY, "Nothing special happened today and it was lovely. Good coffee, nice walk, my favorite song came on the radio. Simple happy day.")
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
                a.evidence().stream().limit(7).forEach(e -> System.out.printf("        %s %.2f%n", e.describe(), e.weight()));
            }
        }
        System.out.printf("%nHoldout top-1: %d/%d (%.1f%%)   top-2: %d/%d (%.1f%%)%n",
                top1, CASES.size(), 100.0 * top1 / CASES.size(), top2, CASES.size(), 100.0 * top2 / CASES.size());
    }
}
