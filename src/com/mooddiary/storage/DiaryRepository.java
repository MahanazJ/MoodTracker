package com.mooddiary.storage;

import com.mooddiary.model.DiaryEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Persistence for diary pages: at most one entry per date. */
public interface DiaryRepository {

    void save(DiaryEntry entry);

    Optional<DiaryEntry> findByDate(LocalDate date);

    /** Entries with {@code from <= date <= to}, oldest first. */
    List<DiaryEntry> findBetween(LocalDate from, LocalDate to);

    /** All entries, oldest first. */
    List<DiaryEntry> findAll();

    boolean delete(LocalDate date);
}
