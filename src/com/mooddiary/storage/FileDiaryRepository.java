package com.mooddiary.storage;

import com.mooddiary.model.DiaryEntry;
import com.mooddiary.model.Mood;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * One human-readable file per day, e.g. {@code entries/2026-10-16.txt}:
 *
 * <pre>
 * mood: STRESSED
 * detected: STRESSED
 * secondary: TIRED
 * corrected: false
 * intensity: 7
 * confidence: 0.82
 * valence: -0.55
 * created: 2026-10-16T21:04:11
 * updated: 2026-10-16T21:10:42
 * ---
 * Dear diary, today was...
 * </pre>
 *
 * Everything after the first "---" line is the diary text, stored exactly as written.
 */
public final class FileDiaryRepository implements DiaryRepository {

    private static final String EXTENSION = ".txt";
    private static final String SEPARATOR = "---";

    private final Path directory;
    private final Consumer<String> warnings;

    public FileDiaryRepository(Path directory, Consumer<String> warnings) {
        this.directory = Objects.requireNonNull(directory, "directory");
        this.warnings = Objects.requireNonNull(warnings, "warnings");
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new StorageException("Cannot create diary folder " + directory, e);
        }
    }

    @Override
    public void save(DiaryEntry e) {
        StringBuilder sb = new StringBuilder(e.text().length() + 300);
        sb.append("mood: ").append(e.mood().name()).append('\n');
        sb.append("detected: ").append(e.detectedMood().name()).append('\n');
        sb.append("secondary: ").append(e.secondaryMood() == null ? "" : e.secondaryMood().name()).append('\n');
        sb.append("corrected: ").append(e.corrected()).append('\n');
        sb.append("intensity: ").append(e.intensity()).append('\n');
        sb.append("confidence: ").append(String.format(java.util.Locale.ROOT, "%.3f", e.confidence())).append('\n');
        sb.append("valence: ").append(String.format(java.util.Locale.ROOT, "%.3f", e.valence())).append('\n');
        sb.append("created: ").append(e.createdAt()).append('\n');
        sb.append("updated: ").append(e.updatedAt()).append('\n');
        sb.append(SEPARATOR).append('\n');
        sb.append(e.text());
        try {
            AtomicFiles.write(fileFor(e.date()), sb.toString());
        } catch (IOException ex) {
            throw new StorageException("Could not save the entry for " + e.date(), ex);
        }
    }

    @Override
    public Optional<DiaryEntry> findByDate(LocalDate date) {
        Path f = fileFor(date);
        return Files.exists(f) ? Optional.of(read(f, date)) : Optional.empty();
    }

    @Override
    public List<DiaryEntry> findBetween(LocalDate from, LocalDate to) {
        return load(d -> !d.isBefore(from) && !d.isAfter(to));
    }

    @Override
    public List<DiaryEntry> findAll() {
        return load(d -> true);
    }

    @Override
    public boolean delete(LocalDate date) {
        try {
            return Files.deleteIfExists(fileFor(date));
        } catch (IOException e) {
            throw new StorageException("Could not delete the entry for " + date, e);
        }
    }

    private Path fileFor(LocalDate date) {
        return directory.resolve(date + EXTENSION);
    }

    private List<DiaryEntry> load(Predicate<LocalDate> filter) {
        List<DiaryEntry> out = new ArrayList<>();
        try (Stream<Path> files = Files.list(directory)) {
            for (Path f : files.toList()) {
                String name = f.getFileName().toString();
                if (!name.endsWith(EXTENSION)) {
                    continue;
                }
                LocalDate date;
                try {
                    date = LocalDate.parse(name.substring(0, name.length() - EXTENSION.length()));
                } catch (DateTimeParseException notAnEntry) {
                    continue;
                }
                if (!filter.test(date)) {
                    continue;
                }
                try {
                    out.add(read(f, date));
                } catch (StorageException bad) {
                    warnings.accept(bad.getMessage() + " (skipped)");
                }
            }
        } catch (IOException e) {
            throw new StorageException("Could not read the diary folder " + directory, e);
        }
        out.sort(Comparator.comparing(DiaryEntry::date));
        return out;
    }

    private DiaryEntry read(Path file, LocalDate date) {
        String content;
        try {
            content = Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException("Could not read " + file.getFileName(), e);
        }
        content = content.replace("\r\n", "\n");
        int sep = content.startsWith(SEPARATOR + "\n") ? 0 : content.indexOf("\n" + SEPARATOR + "\n");
        if (sep < 0) {
            throw new StorageException("Diary file " + file.getFileName() + " has no '---' line");
        }
        String header = content.substring(0, sep);
        String text = content.substring(sep == 0 ? SEPARATOR.length() + 1 : sep + SEPARATOR.length() + 2);
        Map<String, String> h = new HashMap<>();
        for (String line : header.split("\n")) {
            int colon = line.indexOf(':');
            if (colon > 0) {
                h.put(line.substring(0, colon).strip(), line.substring(colon + 1).strip());
            }
        }
        try {
            Mood mood = Mood.valueOf(h.getOrDefault("mood", "CALM"));
            Mood detected = Mood.valueOf(h.getOrDefault("detected", mood.name()));
            String sec = h.getOrDefault("secondary", "");
            Mood secondary = sec.isEmpty() ? null : Mood.valueOf(sec);
            LocalDateTime created = parseTime(h.get("created"), date);
            LocalDateTime updated = parseTime(h.get("updated"), date);
            return new DiaryEntry(date, text, mood, detected, secondary,
                    Boolean.parseBoolean(h.getOrDefault("corrected", "false")),
                    Integer.parseInt(h.getOrDefault("intensity", "5")),
                    Double.parseDouble(h.getOrDefault("confidence", "0.5")),
                    Double.parseDouble(h.getOrDefault("valence", Double.toString(mood.valence()))),
                    created, updated);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new StorageException("Diary file " + file.getFileName() + " is damaged: " + e.getMessage(), e);
        }
    }

    private static LocalDateTime parseTime(String s, LocalDate fallback) {
        return (s == null || s.isEmpty()) ? fallback.atStartOfDay() : LocalDateTime.parse(s);
    }
}
