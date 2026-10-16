# Mood Diary

A desktop diary in Java. Write about your day; the diary reads what you wrote, works out how you're feeling,
suggests twelve songs for that mood, and plots each day on a mood chart.

Requires Java 17 or newer (a JDK, so `javac` is available). No other dependencies.

## Run it

macOS / Linux:

    ./run.sh

Windows: double-click `run.bat` (or run `build.bat` first from a terminal to see any errors).

Your diary is saved in a `MoodDiary` folder in your home directory. To use a different folder:
`./run.sh /path/to/folder` (or `run.bat D:\MyDiary`).

## Using it

- **Write**: one ruled page per day. The margin line changes color to match the mood it's picking up as you type.
  Press *Save and find my songs* to see the mood, how sure it is, the phrases it based that on, and a playlist.
  Use *Previous day* / *Next day* to write about earlier days.
- **Not quite right?** Pick the mood you actually felt and press *Use this mood*. The diary learns what your
  words mean to you (saved in `learned-words.txt`), so similar entries are read better next time.
- **Playlist styles**: *Match my mood*, *Lift my mood* (starts with songs that match how you feel, then shifts
  toward a better place), *Blend my moods* (mixes your two strongest feelings). Double-click a song or use the
  buttons to play it on Spotify or YouTube.
- **Journal**: all your pages, newest first, filterable by mood.
- **Mood chart**: one point per day, from Rough to Great, colored by mood and sized by intensity.
  Hover for details, click to open that day.

## How mood detection works

`analysis/` contains a rule-based reader with a vocabulary of about 1,100 words and phrases across 16 moods
(happy, excited, grateful, loved, confident, hopeful, calm, nostalgic, bored, tired, stressed, anxious, angry,
lonely, sad, heartbroken). Around each match it considers negation ("not happy", "can't stop smiling"),
intensifiers and softeners, contrast ("but", "even though"), summary sentences ("overall…"), where in the entry
it appears, earlier-vs-now, whether it's about you or someone else (or a movie), CAPS and exclamation marks,
anxious self-questions, hypotheticals ("I might…"), slang, and emoji. Phrases match in any tense.

Accuracy on 30 entries it had never seen before tuning: 70% exactly right, 77% in the top two (random guessing
among 16 moods is about 6%). Corrections personalize it further. Run the test suites with:

    java -cp out:out-test com.mooddiary.analysis.BlindTest      (use ; instead of : on Windows)

If an entry contains language suggesting thoughts of self-harm, the diary shows crisis-line information.

## Code layout

    model/           Mood, DiaryEntry, Track, Playlist
    analysis/        TextNormalizer, EmotionLexicon + LexiconData, MoodAnalyzer, PersonalLexicon, SupportScreen
    storage/         DiaryRepository / FileDiaryRepository (one text file per day), LearningStore
    diary/           DiaryService (write, correct, query, stats), MoodStatistics
    recommendation/  PlaylistCatalog (16 moods x 12 songs), Match / Lift / Blend strategies
    ui/              Swing: DiaryApp, WritePanel, ResultPanel, JournalPanel, ChartPanel, MoodChart
    Main.java        wires everything together

To add words, edit `LexiconData.java`. To change songs, edit `PlaylistCatalog.curated()`.
