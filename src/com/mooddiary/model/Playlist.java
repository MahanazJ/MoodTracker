package com.mooddiary.model;

import java.util.List;
import java.util.Objects;

/** An immutable, named, ordered list of tracks. */
public final class Playlist {

    private final String name;
    private final String description;
    private final List<Track> tracks;

    public Playlist(String name, String description, List<Track> tracks) {
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        this.tracks = List.copyOf(Objects.requireNonNull(tracks, "tracks"));
        if (this.tracks.isEmpty()) {
            throw new IllegalArgumentException("Playlist '" + name + "' must contain at least one track");
        }
    }

    public String name()        { return name; }
    public String description() { return description; }
    public List<Track> tracks() { return tracks; }
    public int size()           { return tracks.size(); }

    @Override
    public String toString() {
        return name + " (" + tracks.size() + " tracks)";
    }
}
