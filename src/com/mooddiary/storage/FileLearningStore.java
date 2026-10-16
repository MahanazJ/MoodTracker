package com.mooddiary.storage;

import com.mooddiary.model.Mood;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/** Stores learned weights as lines like {@code commute=STRESSED:0.30,CALM:-0.10}. */
public final class FileLearningStore implements LearningStore {

    private final Path file;

    public FileLearningStore(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    @Override
    public Map<String, Map<Mood, Double>> load() {
        Map<String, Map<Mood, Double>> out = new HashMap<>();
        if (!Files.exists(file)) {
            return out;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                int eq = line.indexOf('=');
                if (line.startsWith("#") || eq <= 0) {
                    continue;
                }
                Map<Mood, Double> m = new EnumMap<>(Mood.class);
                for (String part : line.substring(eq + 1).split(",")) {
                    String[] kv = part.split(":");
                    if (kv.length == 2) {
                        try {
                            m.put(Mood.valueOf(kv[0].strip()), Double.parseDouble(kv[1].strip()));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown moods or bad numbers; the rest of the file is still useful
                        }
                    }
                }
                if (!m.isEmpty()) {
                    out.put(line.substring(0, eq).strip(), m);
                }
            }
        } catch (IOException e) {
            throw new StorageException("Could not read " + file.getFileName(), e);
        }
        return out;
    }

    @Override
    public void save(Map<String, Map<Mood, Double>> weights) {
        StringBuilder sb = new StringBuilder("# Words the diary has learned from your mood corrections\n");
        new TreeMap<>(weights).forEach((word, m) -> {
            StringBuilder line = new StringBuilder(word).append('=');
            boolean first = true;
            for (Map.Entry<Mood, Double> e : m.entrySet()) {
                if (!first) {
                    line.append(',');
                }
                line.append(e.getKey().name()).append(':').append(String.format(Locale.ROOT, "%.2f", e.getValue()));
                first = false;
            }
            sb.append(line).append('\n');
        });
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            AtomicFiles.write(file, sb.toString());
        } catch (IOException e) {
            throw new StorageException("Could not save learned words", e);
        }
    }
}
