package com.mooddiary.diary;

import com.mooddiary.analysis.MoodAnalysis;
import com.mooddiary.analysis.MoodAnalyzer;
import com.mooddiary.analysis.PersonalLexicon;
import com.mooddiary.model.DiaryEntry;
import com.mooddiary.model.Mood;
import com.mooddiary.storage.DiaryRepository;
import com.mooddiary.storage.LearningStore;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Application logic for the diary: writing a page analyses it and saves it,
 * correcting a mood teaches the analyzer, and queries feed the journal and chart.
 * Listeners are notified whenever entries change so views can refresh.
 */
public final class DiaryService {

    private final DiaryRepository repository;
    private final LearningStore learningStore;
    private final MoodAnalyzer analyzer;
    private final Clock clock;
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public DiaryService(DiaryRepository repository, LearningStore learningStore, MoodAnalyzer analyzer, Clock clock) {
        this.repository = Objects.requireNonNull(repository);
        this.learningStore = Objects.requireNonNull(learningStore);
        this.analyzer = Objects.requireNonNull(analyzer);
        this.clock = Objects.requireNonNull(clock);
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    public void addChangeListener(Runnable r) {
        listeners.add(r);
    }

    /** Analyses text without saving (live mood meter, showing past entries). */
    public MoodAnalysis analyze(String text) {
        return analyzer.analyze(text);
    }

    /**
     * Saves a diary page. If the text is unchanged and the user had corrected
     * the mood, the correction is kept; otherwise the mood is re-detected.
     */
    public DiaryEntry write(LocalDate date, String text) {
        Objects.requireNonNull(date, "date");
        if (date.isAfter(today())) {
            throw new IllegalArgumentException("You can't write an entry for a future date.");
        }
        String body = text == null ? "" : text.strip();
        if (body.isEmpty()) {
            throw new IllegalArgumentException("Write something about your day first.");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        Optional<DiaryEntry> existing = repository.findByDate(date);
        if (existing.isPresent() && existing.get().text().equals(body) && existing.get().corrected()) {
            return existing.get();
        }
        MoodAnalysis a = analyzer.analyze(body);
        DiaryEntry entry = new DiaryEntry(date, body, a.primary(), a.primary(), a.secondary().orElse(null), false,
                a.intensity(), a.confidence(), a.valence(),
                existing.map(DiaryEntry::createdAt).orElse(now), now);
        repository.save(entry);
        fireChanged();
        return entry;
    }

    /** Records the user's own mood for a day and learns from the difference. */
    public DiaryEntry correctMood(LocalDate date, Mood actual) {
        DiaryEntry e = repository.findByDate(date)
                .orElseThrow(() -> new IllegalArgumentException("Save the entry before changing its mood."));
        if (actual != e.detectedMood()) {
            PersonalLexicon personal = analyzer.personalLexicon();
            personal.learn(e.text(), e.detectedMood(), actual);
            learningStore.save(personal.snapshot());
        }
        DiaryEntry updated = e.withCorrectedMood(actual, LocalDateTime.now(clock));
        repository.save(updated);
        fireChanged();
        return updated;
    }

    public Optional<DiaryEntry> entryFor(LocalDate date) {
        return repository.findByDate(date);
    }

    public List<DiaryEntry> allEntries() {
        return repository.findAll();
    }

    public List<DiaryEntry> entriesBetween(LocalDate from, LocalDate to) {
        return repository.findBetween(from, to);
    }

    public boolean delete(LocalDate date) {
        boolean removed = repository.delete(date);
        if (removed) {
            fireChanged();
        }
        return removed;
    }

    public void forgetLearnedWords() {
        analyzer.personalLexicon().clear();
        learningStore.save(analyzer.personalLexicon().snapshot());
    }

    public int learnedWordCount() {
        return analyzer.personalLexicon().size();
    }

    public MoodStatistics statistics(LocalDate from, LocalDate to) {
        List<DiaryEntry> all = repository.findAll();
        Set<LocalDate> days = all.stream().map(DiaryEntry::date).collect(Collectors.toSet());
        List<DiaryEntry> range = all.stream()
                .filter(e -> !e.date().isBefore(from) && !e.date().isAfter(to))
                .toList();
        return new MoodStatistics(range, currentStreak(days), longestStreak(all));
    }

    private int currentStreak(Set<LocalDate> days) {
        LocalDate d = today();
        if (!days.contains(d)) {
            d = d.minusDays(1);       // today not written yet doesn't break the streak
        }
        int n = 0;
        while (days.contains(d)) {
            n++;
            d = d.minusDays(1);
        }
        return n;
    }

    private static int longestStreak(List<DiaryEntry> sorted) {
        int best = 0;
        int run = 0;
        LocalDate prev = null;
        for (DiaryEntry e : sorted) {
            run = (prev != null && e.date().equals(prev.plusDays(1))) ? run + 1 : 1;
            best = Math.max(best, run);
            prev = e.date();
        }
        return best;
    }

    private void fireChanged() {
        for (Runnable r : listeners) {
            r.run();
        }
    }
}
