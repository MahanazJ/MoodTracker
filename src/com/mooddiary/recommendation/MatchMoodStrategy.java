package com.mooddiary.recommendation;

import com.mooddiary.model.DiaryEntry;

/** The playlist curated for exactly the mood you're in. */
public final class MatchMoodStrategy implements RecommendationStrategy {

    @Override
    public String name() {
        return "Match my mood";
    }

    @Override
    public String description() {
        return "Music that meets you where you are.";
    }

    @Override
    public Recommendation recommend(DiaryEntry entry, PlaylistCatalog catalog) {
        return new Recommendation(catalog.forMood(entry.mood()),
                "Songs for feeling " + entry.mood().displayName().toLowerCase() + ".", name());
    }
}
