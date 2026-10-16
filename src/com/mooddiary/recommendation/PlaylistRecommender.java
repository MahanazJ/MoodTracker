package com.mooddiary.recommendation;

import com.mooddiary.model.DiaryEntry;

import java.util.List;
import java.util.Objects;

/** Holds the catalog and the active strategy; the UI can switch strategies at runtime. */
public final class PlaylistRecommender {

    private final PlaylistCatalog catalog;
    private final List<RecommendationStrategy> strategies;
    private RecommendationStrategy active;

    public PlaylistRecommender(PlaylistCatalog catalog, List<RecommendationStrategy> strategies) {
        this.catalog = Objects.requireNonNull(catalog);
        this.strategies = List.copyOf(strategies);
        if (this.strategies.isEmpty()) {
            throw new IllegalArgumentException("At least one strategy is required");
        }
        this.active = this.strategies.get(0);
    }

    public static PlaylistRecommender withDefaults(PlaylistCatalog catalog) {
        return new PlaylistRecommender(catalog,
                List.of(new MatchMoodStrategy(), new MoodLiftStrategy(), new BlendStrategy()));
    }

    public Recommendation recommend(DiaryEntry entry) {
        return active.recommend(entry, catalog);
    }

    public PlaylistCatalog catalog()                    { return catalog; }
    public List<RecommendationStrategy> strategies()     { return strategies; }
    public RecommendationStrategy active()               { return active; }

    public void setActive(RecommendationStrategy s) {
        if (!strategies.contains(s)) {
            throw new IllegalArgumentException("Unknown strategy " + s.name());
        }
        active = s;
    }
}
