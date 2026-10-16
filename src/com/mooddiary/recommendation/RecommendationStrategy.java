package com.mooddiary.recommendation;

import com.mooddiary.model.DiaryEntry;

/** Strategy pattern: different philosophies for turning a mood into music. */
public interface RecommendationStrategy {

    String name();

    String description();

    Recommendation recommend(DiaryEntry entry, PlaylistCatalog catalog);
}
