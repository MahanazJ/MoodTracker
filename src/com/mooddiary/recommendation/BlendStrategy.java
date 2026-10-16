package com.mooddiary.recommendation;

import com.mooddiary.model.DiaryEntry;
import com.mooddiary.model.Mood;
import com.mooddiary.model.Playlist;
import com.mooddiary.model.Track;

import java.util.ArrayList;
import java.util.List;

/** Mixed feelings get mixed music: two from your main mood for every one from your second. */
public final class BlendStrategy implements RecommendationStrategy {

    @Override
    public String name() {
        return "Blend my moods";
    }

    @Override
    public String description() {
        return "Mixes songs for your main and secondary feelings.";
    }

    @Override
    public Recommendation recommend(DiaryEntry entry, PlaylistCatalog catalog) {
        Mood main = entry.mood();
        Mood second = entry.secondaryMood();
        if (second == null || second == main) {
            return new Recommendation(catalog.forMood(main),
                    "Today read as mostly " + main.displayName().toLowerCase() + ", so this is a single-mood playlist.",
                    name());
        }
        List<Track> a = catalog.forMood(main).tracks();
        List<Track> b = catalog.forMood(second).tracks();
        List<Track> mix = new ArrayList<>(12);
        int ia = 0;
        int ib = 0;
        while (mix.size() < 12 && (ia < a.size() || ib < b.size())) {
            for (int k = 0; k < 2 && ia < a.size() && mix.size() < 12; k++) {
                Track t = a.get(ia++);
                if (!mix.contains(t)) {
                    mix.add(t);
                }
            }
            if (ib < b.size() && mix.size() < 12) {
                Track t = b.get(ib++);
                if (!mix.contains(t)) {
                    mix.add(t);
                }
            }
        }
        Playlist p = new Playlist(main.displayName() + " + " + second.displayName(),
                "Mostly " + catalog.forMood(main).name() + ", with some " + catalog.forMood(second).name() + ".", mix);
        return new Recommendation(p, "You wrote with mixed feelings: mostly " + main.displayName().toLowerCase()
                + ", with some " + second.displayName().toLowerCase() + ".", name());
    }
}
