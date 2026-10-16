package com.mooddiary.recommendation;

import com.mooddiary.model.DiaryEntry;
import com.mooddiary.model.Mood;
import com.mooddiary.model.Playlist;
import com.mooddiary.model.Track;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The music-therapy "iso principle": start with songs that match how you feel,
 * then shift gradually toward a better place. The stronger the feeling, the
 * longer the playlist stays with you before it starts to lift.
 */
public final class MoodLiftStrategy implements RecommendationStrategy {

    private static final int LENGTH = 12;
    private final Map<Mood, Mood> targets = new EnumMap<>(Mood.class);

    public MoodLiftStrategy() {
        targets.put(Mood.SAD, Mood.HOPEFUL);
        targets.put(Mood.HEARTBROKEN, Mood.CONFIDENT);
        targets.put(Mood.LONELY, Mood.GRATEFUL);
        targets.put(Mood.ANXIOUS, Mood.CALM);
        targets.put(Mood.STRESSED, Mood.CALM);
        targets.put(Mood.ANGRY, Mood.CALM);
        targets.put(Mood.TIRED, Mood.HAPPY);
        targets.put(Mood.BORED, Mood.EXCITED);
        targets.put(Mood.NOSTALGIC, Mood.GRATEFUL);
    }

    @Override
    public String name() {
        return "Lift my mood";
    }

    @Override
    public String description() {
        return "Starts where you are, then gently moves somewhere better.";
    }

    @Override
    public Recommendation recommend(DiaryEntry entry, PlaylistCatalog catalog) {
        Mood current = entry.mood();
        Mood target = targets.get(current);
        if (target == null) {
            return new Recommendation(catalog.forMood(current),
                    "You're already in a good place, so here's more of that " + current.displayName().toLowerCase() + " feeling.",
                    name());
        }
        int matching = entry.intensity() <= 3 ? 3 : entry.intensity() <= 6 ? 4 : 6;
        Playlist from = catalog.forMood(current);
        Playlist to = catalog.forMood(target);
        List<Track> journey = new ArrayList<>(LENGTH);
        journey.addAll(from.tracks().subList(0, Math.min(matching, from.size())));
        for (Track t : to.tracks()) {
            if (journey.size() >= LENGTH) {
                break;
            }
            if (!journey.contains(t)) {
                journey.add(t);
            }
        }
        Playlist blended = new Playlist("From " + current.displayName() + " to " + target.displayName(),
                "Begins with " + from.name() + ", then eases into " + to.name() + ".", journey);
        return new Recommendation(blended,
                matching + " songs that sit with feeling " + current.displayName().toLowerCase()
                        + ", then a gradual shift toward " + target.displayName().toLowerCase() + ".",
                name());
    }
}
