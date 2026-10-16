package com.mooddiary.analysis;

import com.mooddiary.model.Mood;

import java.util.List;

import static com.mooddiary.model.Mood.*;

/**
 * Labeled diary samples covering every mood plus tricky cases (negation,
 * contrast, sarcasm cues, slang, emoji, mixed feelings, other people's moods).
 * Run: java -cp out:out-test com.mooddiary.analysis.AnalyzerAccuracyTest
 */
public final class AnalyzerAccuracyTest {

    record Case(Mood expected, String text) { }

    static final List<Case> CASES = List.of(
        // HAPPY
        new Case(HAPPY, "Today was honestly such a good day. Got coffee with Maya, we laughed the whole time, and the sun was out. I'm in such a good mood."),
        new Case(HAPPY, "I don't even know why but I woke up feeling great and that feeling lasted all day :)"),
        new Case(HAPPY, "Had so much fun at the beach with my cousins!! Best day of the summer 😄"),
        new Case(HAPPY, "work was fine, nothing special, but then we went to karaoke and I could not stop laughing. so happy rn"),
        new Case(HAPPY, "Not gonna lie, today was pretty great. Good food, good people, no drama."),
        // EXCITED
        new Case(EXCITED, "I GOT THE JOB!!! I start in two weeks and I literally cannot wait. Still shaking from the call!!"),
        new Case(EXCITED, "Tomorrow we fly to Japan and I'm so pumped I can't sleep. Two weeks of adventure!"),
        new Case(EXCITED, "Concert tonight!!! Front row tickets. I've been looking forward to this for months 🎉"),
        new Case(EXCITED, "so hyped for the weekend, we're doing a road trip up the coast and I can't wait"),
        // GRATEFUL
        new Case(GRATEFUL, "My neighbor brought soup when she heard I was sick. I'm so grateful for people like her."),
        new Case(GRATEFUL, "Feeling really thankful tonight. My parents helped me move and never once complained. I'm lucky to have them."),
        new Case(GRATEFUL, "Counting my blessings today. Health, a roof over my head, friends who check in. It means a lot."),
        new Case(GRATEFUL, "My team covered for me while I was out and it meant the world. I appreciate them so much 🙏"),
        // LOVED
        new Case(LOVED, "Date night with Sam. We cooked together, slow danced in the kitchen, and he kept holding my hand. I feel so loved."),
        new Case(LOVED, "She told me she loves me for the first time today. I think I'm falling in love too 😍"),
        new Case(LOVED, "My best friend surprised me with a care package and a letter. I felt so seen and cared for."),
        new Case(LOVED, "Lazy morning cuddling with my husband. Nothing fancy, just us. I adore him."),
        // CONFIDENT
        new Case(CONFIDENT, "Nailed my presentation today. My manager said it was the best one this quarter. I'm proud of myself."),
        new Case(CONFIDENT, "Ran my first 10k without stopping! Personal best. I feel strong and capable."),
        new Case(CONFIDENT, "I finally stood up for myself in that meeting and it went well. I've got this."),
        new Case(CONFIDENT, "Passed my driving test on the first try. Feeling unstoppable 💪"),
        // HOPEFUL
        new Case(HOPEFUL, "Things have been hard but I think they're finally looking up. Therapy is helping and I'm hopeful about next month."),
        new Case(HOPEFUL, "Applied to three jobs today. Nothing back yet but I have a good feeling. Tomorrow is a new day."),
        new Case(HOPEFUL, "It's been a rough year, but I'm healing slowly. One day at a time. I really believe it'll be okay."),
        new Case(HOPEFUL, "Starting fresh this week. New apartment, new routine, a fresh start. I'm optimistic."),
        // CALM
        new Case(CALM, "Quiet Sunday. Read on the balcony, made tea, took a long bath. I feel peaceful and rested."),
        new Case(CALM, "Did yoga this morning and went for a walk by the lake. Really relaxed and at ease today."),
        new Case(CALM, "Nothing big happened, just a slow cozy day at home. It was nice to unwind."),
        new Case(CALM, "Finally turned in the report. What a relief, a weight off my shoulders. Now I can just breathe."),
        // NOSTALGIC
        new Case(NOSTALGIC, "Found a box of old photos from high school. So many memories. I miss those days."),
        new Case(NOSTALGIC, "Heard a song from when I was a kid and it took me back. Bittersweet but nice."),
        new Case(NOSTALGIC, "Drove past my childhood home today. I remember riding my bike down that street. Feeling nostalgic."),
        new Case(NOSTALGIC, "Reunion with my college friends. We spent the night reminiscing about the good old days."),
        // BORED
        new Case(BORED, "So bored. Nothing to do, nobody's around, just scrolling on my phone all afternoon."),
        new Case(BORED, "Same old routine. Work, eat, sleep. Nothing interesting happened. Meh."),
        new Case(BORED, "today was so boring. the meeting dragged on forever and I was just watching the clock"),
        new Case(BORED, "Rainy day stuck at home with no plans. I'm restless and bored out of my mind."),
        // TIRED
        new Case(TIRED, "I'm exhausted. Barely slept last night and had back to back classes. I have no energy left."),
        new Case(TIRED, "Long day. My eyes are heavy and I just want to crawl into bed. So sleepy 😴"),
        new Case(TIRED, "Pulled an all nighter for the project and now I'm running on empty. Completely drained."),
        new Case(TIRED, "tired af. worked a double shift, feet are sore, going to sleep the second I get home"),
        // STRESSED
        new Case(STRESSED, "I have three deadlines tomorrow and so much to do. I'm completely overwhelmed and falling behind."),
        new Case(STRESSED, "Work is chaos. My boss keeps adding things to my plate and I'm stretched so thin. Ugh."),
        new Case(STRESSED, "Finals week. Two exams, a paper due, and I'm working weekends. The pressure is insane."),
        new Case(STRESSED, "Everything went wrong today. Missed the bus, spilled coffee, forgot my laptop charger. So stressed."),
        // ANXIOUS
        new Case(ANXIOUS, "I can't stop worrying about the test results. What if it's bad? My heart's been racing all day."),
        new Case(ANXIOUS, "Job interview tomorrow and I'm so nervous I feel sick to my stomach. I keep overthinking every answer."),
        new Case(ANXIOUS, "Had a panic attack on the train. Couldn't breathe, chest was tight. Still on edge tonight."),
        new Case(ANXIOUS, "She hasn't texted back in hours and my mind is racing. I'm scared I said something wrong."),
        // ANGRY
        new Case(ANGRY, "I am SO angry. My roommate ate my food again and then lied about it. I'm sick of it."),
        new Case(ANGRY, "My coworker took credit for my work in front of everyone. I'm furious. How dare he."),
        new Case(ANGRY, "Customer service hung up on me twice. This is ridiculous and I'm absolutely fed up 😡"),
        new Case(ANGRY, "Got into a huge argument with my brother. He's so disrespectful and it drives me crazy."),
        // LONELY
        new Case(LONELY, "Everyone has plans this weekend except me. I feel so alone. Nobody even texted."),
        new Case(LONELY, "Moved to a new city a month ago and I still don't know anyone. Eating alone again tonight."),
        new Case(LONELY, "I miss my friends back home so much. It's quiet here and I have no one to talk to."),
        new Case(LONELY, "Sat alone at lunch again. Sometimes it feels like I'm invisible and nobody cares."),
        // SAD
        new Case(SAD, "My grandma passed away this morning. I can't stop crying. I don't know what to do without her."),
        new Case(SAD, "Feeling really down today. Everything feels heavy and I cried in the car on the way home."),
        new Case(SAD, "Didn't get into the program. I'm so disappointed. I really thought this was it."),
        new Case(SAD, "Just sad. No reason really. Feel empty and it's hard to get out of bed."),
        // HEARTBROKEN
        new Case(HEARTBROKEN, "He broke up with me tonight. Five years and it's just over. My heart is broken."),
        new Case(HEARTBROKEN, "Found out she's been seeing someone else. I still love her and I can't get over it 💔"),
        new Case(HEARTBROKEN, "Saw my ex at the party with his new girlfriend. It felt like being dumped all over again."),
        new Case(HEARTBROKEN, "She said she doesn't love me anymore. I keep rereading our messages. I'm heartbroken."),
        // TRICKY: negation, contrast, others' feelings, slang
        new Case(SAD, "I'm not happy. I haven't been happy in a long time."),
        new Case(CALM, "I'm not stressed anymore, everything is under control and I feel calm."),
        new Case(HAPPY, "The morning was awful and I was so annoyed at first, but the evening turned out amazing and I'm really happy now."),
        new Case(STRESSED, "Although the weekend was relaxing, today I am completely overwhelmed with work."),
        new Case(ANXIOUS, "My sister was really happy about her promotion. I'm happy for her, but honestly I'm anxious about my own job."),
        new Case(TIRED, "Not sad or anything, just super tired. Long week."),
        new Case(HAPPY, "Never been happier honestly!!"),
        new Case(SAD, "Nothing makes me happy anymore."),
        new Case(EXCITED, "omg omg omg we're going to see them LIVE next month I can't even 🤩🤩"),
        new Case(ANGRY, "oh great, another flat tire. just great. absolutely furious."),
        new Case(LONELY, "Everyone else has someone. I just wish I had someone to come home to."),
        new Case(CALM, "Today was okay. Not bad, not great. Pretty chill actually."),
        new Case(GRATEFUL, "Rough week but my friends really showed up for me. I'm so grateful, I don't know what I'd do without them."),
        new Case(HEARTBROKEN, "we split up today. idk how to feel. i keep crying and i miss him already"),
        new Case(CONFIDENT, "Everyone doubted me but I crushed it. Got an A on the hardest exam of the semester."),
        new Case(NOSTALGIC, "Looking through my old yearbook made me miss the old times. Back then everything felt simpler.")
    );

    public static void main(String[] args) {
        MoodAnalyzer analyzer = MoodAnalyzer.standard();
        int top1 = 0;
        int top2 = 0;
        boolean verbose = args.length > 0 && args[0].equals("-v");
        for (Case c : CASES) {
            MoodAnalysis a = analyzer.analyze(c.text());
            boolean ok = a.primary() == c.expected();
            boolean ok2 = ok || a.secondary().map(m -> m == c.expected()).orElse(false);
            if (ok) top1++;
            if (ok2) top2++;
            if (!ok || verbose) {
                System.out.printf("%s expected %-11s got %-11s (2nd %s, conf %.0f%%, int %d): %s%n",
                        ok ? "  " : "XX", c.expected(), a.primary(), a.secondary().map(Mood::toString).orElse("-"),
                        a.confidence() * 100, a.intensity(), c.text());
                if (!ok) {
                    a.ranked().stream().limit(4).forEach(e -> System.out.printf("        %-11s %.2f%n", e.getKey(), e.getValue()));
                    a.evidence().stream().limit(6).forEach(e -> System.out.printf("        %s %.2f%n", e.describe(), e.weight()));
                }
            }
        }
        System.out.printf("%nTop-1 accuracy: %d/%d (%.1f%%)   Top-2: %d/%d (%.1f%%)%n",
                top1, CASES.size(), 100.0 * top1 / CASES.size(), top2, CASES.size(), 100.0 * top2 / CASES.size());
    }
}
