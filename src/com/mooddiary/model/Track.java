package com.mooddiary.model;

import java.util.Objects;

/** A single song. */
public record Track(String title, String artist) {

    public Track {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(artist, "artist");
        if (title.isBlank() || artist.isBlank()) {
            throw new IllegalArgumentException("Track title and artist must not be blank");
        }
    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
