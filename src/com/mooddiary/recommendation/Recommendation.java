package com.mooddiary.recommendation;

import com.mooddiary.model.Playlist;

import java.util.Objects;

/** A playlist picked for a diary entry, with a reason the user can read. */
public record Recommendation(Playlist playlist, String reason, String strategyName) {
    public Recommendation {
        Objects.requireNonNull(playlist);
        Objects.requireNonNull(reason);
        Objects.requireNonNull(strategyName);
    }
}
