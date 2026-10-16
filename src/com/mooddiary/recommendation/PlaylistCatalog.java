package com.mooddiary.recommendation;

import com.mooddiary.model.Mood;
import com.mooddiary.model.Playlist;
import com.mooddiary.model.Track;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Maps every mood to one curated playlist. Incomplete catalogs are rejected. */
public final class PlaylistCatalog {

    private final Map<Mood, Playlist> playlists;

    public PlaylistCatalog(Map<Mood, Playlist> playlists) {
        EnumMap<Mood, Playlist> copy = new EnumMap<>(Mood.class);
        copy.putAll(Objects.requireNonNull(playlists, "playlists"));
        for (Mood m : Mood.values()) {
            if (copy.get(m) == null) {
                throw new IllegalArgumentException("Catalog is missing a playlist for " + m);
            }
        }
        this.playlists = Collections.unmodifiableMap(copy);
    }

    public Playlist forMood(Mood mood) {
        return playlists.get(Objects.requireNonNull(mood, "mood"));
    }

    public Map<Mood, Playlist> all() {
        return playlists;
    }

    private static Track t(String title, String artist) {
        return new Track(title, artist);
    }

    /** Hand-picked playlists: twelve songs per mood across eras and genres. */
    public static PlaylistCatalog curated() {
        Map<Mood, Playlist> m = new EnumMap<>(Mood.class);

        m.put(Mood.HAPPY, new Playlist("Sunshine Mix", "Feel-good songs to amplify a great day.", List.of(
                t("Happy", "Pharrell Williams"),
                t("Walking on Sunshine", "Katrina and the Waves"),
                t("Good as Hell", "Lizzo"),
                t("Here Comes the Sun", "The Beatles"),
                t("Don't Stop Me Now", "Queen"),
                t("September", "Earth, Wind & Fire"),
                t("Shake It Off", "Taylor Swift"),
                t("Good Vibrations", "The Beach Boys"),
                t("Can't Stop the Feeling!", "Justin Timberlake"),
                t("Dancing Queen", "ABBA"),
                t("Levitating", "Dua Lipa"),
                t("Mr. Blue Sky", "Electric Light Orchestra"))));

        m.put(Mood.EXCITED, new Playlist("Can't Sit Still", "High-energy anthems for when you're buzzing.", List.of(
                t("Can't Hold Us", "Macklemore & Ryan Lewis"),
                t("Mr. Brightside", "The Killers"),
                t("Uptown Funk", "Mark Ronson ft. Bruno Mars"),
                t("Levels", "Avicii"),
                t("Titanium", "David Guetta ft. Sia"),
                t("Dog Days Are Over", "Florence + The Machine"),
                t("I Gotta Feeling", "The Black Eyed Peas"),
                t("Shut Up and Dance", "Walk the Moon"),
                t("Don't You Worry Child", "Swedish House Mafia ft. John Martin"),
                t("Party in the U.S.A.", "Miley Cyrus"),
                t("Higher Love", "Kygo & Whitney Houston"),
                t("On Top of the World", "Imagine Dragons"))));

        m.put(Mood.GRATEFUL, new Playlist("Thank You Notes", "Warm songs about the people and moments that matter.", List.of(
                t("What a Wonderful World", "Louis Armstrong"),
                t("Thank You", "Dido"),
                t("Thank U", "Alanis Morissette"),
                t("Count on Me", "Bruno Mars"),
                t("Lean on Me", "Bill Withers"),
                t("You've Got a Friend", "Carole King"),
                t("Beautiful Day", "U2"),
                t("Blessings", "Chance the Rapper"),
                t("Feeling Good", "Nina Simone"),
                t("Good Days", "SZA"),
                t("Better Days", "OneRepublic"),
                t("Thank You for Being a Friend", "Andrew Gold"))));

        m.put(Mood.LOVED, new Playlist("Heart Full", "Romantic, tender songs for feeling close to someone.", List.of(
                t("Can't Help Falling in Love", "Elvis Presley"),
                t("Lover", "Taylor Swift"),
                t("Adore You", "Harry Styles"),
                t("Thinking Out Loud", "Ed Sheeran"),
                t("At Last", "Etta James"),
                t("Best Part", "Daniel Caesar ft. H.E.R."),
                t("Make You Feel My Love", "Adele"),
                t("Yellow", "Coldplay"),
                t("Lucky", "Jason Mraz & Colbie Caillat"),
                t("Crazy in Love", "Beyoncé ft. Jay-Z"),
                t("Just the Way You Are", "Bruno Mars"),
                t("L-O-V-E", "Nat King Cole"))));

        m.put(Mood.CONFIDENT, new Playlist("Main Character Energy", "Songs that make you walk taller.", List.of(
                t("Stronger (What Doesn't Kill You)", "Kelly Clarkson"),
                t("Lose Yourself", "Eminem"),
                t("Eye of the Tiger", "Survivor"),
                t("Run the World (Girls)", "Beyoncé"),
                t("Hall of Fame", "The Script ft. will.i.am"),
                t("Roar", "Katy Perry"),
                t("Unstoppable", "Sia"),
                t("Confident", "Demi Lovato"),
                t("Juice", "Lizzo"),
                t("Remember the Name", "Fort Minor"),
                t("Started From the Bottom", "Drake"),
                t("Believer", "Imagine Dragons"))));

        m.put(Mood.HOPEFUL, new Playlist("Brighter Days", "Uplifting songs about getting through and moving forward.", List.of(
                t("Rise Up", "Andra Day"),
                t("Brave", "Sara Bareilles"),
                t("Keep Your Head Up", "Andy Grammer"),
                t("The Climb", "Miley Cyrus"),
                t("A Change Is Gonna Come", "Sam Cooke"),
                t("Somewhere Over the Rainbow", "Israel Kamakawiwo'ole"),
                t("Unwritten", "Natasha Bedingfield"),
                t("Shake It Out", "Florence + The Machine"),
                t("Hold On", "Wilson Phillips"),
                t("Firework", "Katy Perry"),
                t("Don't Stop Believin'", "Journey"),
                t("Fight Song", "Rachel Platten"))));

        m.put(Mood.CALM, new Playlist("Slow Tide", "Gentle, unhurried music for staying centered.", List.of(
                t("Weightless", "Marconi Union"),
                t("Clair de Lune", "Claude Debussy"),
                t("Holocene", "Bon Iver"),
                t("River Flows in You", "Yiruma"),
                t("Gymnopédie No. 1", "Erik Satie"),
                t("Bloom", "The Paper Kites"),
                t("Sunset Lover", "Petit Biscuit"),
                t("Banana Pancakes", "Jack Johnson"),
                t("Pink Moon", "Nick Drake"),
                t("Harvest Moon", "Neil Young"),
                t("Strawberry Swing", "Coldplay"),
                t("Nuvole Bianche", "Ludovico Einaudi"))));

        m.put(Mood.NOSTALGIC, new Playlist("Memory Lane", "Bittersweet songs for looking back.", List.of(
                t("In My Life", "The Beatles"),
                t("Summer of '69", "Bryan Adams"),
                t("Glory Days", "Bruce Springsteen"),
                t("Landslide", "Fleetwood Mac"),
                t("Photograph", "Ed Sheeran"),
                t("Good Riddance (Time of Your Life)", "Green Day"),
                t("Ribs", "Lorde"),
                t("Memories", "Maroon 5"),
                t("Night Changes", "One Direction"),
                t("Those Were the Days", "Mary Hopkin"),
                t("Seventeen Going Under", "Sam Fender"),
                t("Fast Car", "Tracy Chapman"))));

        m.put(Mood.BORED, new Playlist("Shake Things Up", "Eclectic, fun picks to break the monotony.", List.of(
                t("Bohemian Rhapsody", "Queen"),
                t("Take On Me", "a-ha"),
                t("Hey Ya!", "OutKast"),
                t("Africa", "Toto"),
                t("Crazy", "Gnarls Barkley"),
                t("Toxic", "Britney Spears"),
                t("Feel It Still", "Portugal. The Man"),
                t("bad guy", "Billie Eilish"),
                t("Tongue Tied", "Grouplove"),
                t("Sweet Child O' Mine", "Guns N' Roses"),
                t("Gangnam Style", "PSY"),
                t("Dance Monkey", "Tones and I"))));

        m.put(Mood.TIRED, new Playlist("Low Battery", "Easygoing grooves for a slow, foggy day.", List.of(
                t("The Lazy Song", "Bruno Mars"),
                t("Sunday Morning", "Maroon 5"),
                t("Coffee", "beabadoobee"),
                t("Put Your Records On", "Corinne Bailey Rae"),
                t("Island in the Sun", "Weezer"),
                t("Riptide", "Vance Joy"),
                t("Sunflower", "Post Malone & Swae Lee"),
                t("Sweet Disposition", "The Temper Trap"),
                t("Electric Feel", "MGMT"),
                t("Sleepyhead", "Passion Pit"),
                t("Wake Me Up", "Avicii"),
                t("Golden Hour", "JVKE"))));

        m.put(Mood.STRESSED, new Playlist("Pressure Release", "Songs that get it, then help you exhale.", List.of(
                t("Stressed Out", "twenty one pilots"),
                t("Under Pressure", "Queen & David Bowie"),
                t("Vienna", "Billy Joel"),
                t("Float On", "Modest Mouse"),
                t("Bad Day", "Daniel Powter"),
                t("Take It Easy", "Eagles"),
                t("Heavy", "Linkin Park ft. Kiiara"),
                t("Overwhelmed", "Royal & the Serpent"),
                t("Ain't Worried", "OneRepublic"),
                t("Breathe (In the Air)", "Pink Floyd"),
                t("Don't Panic", "Coldplay"),
                t("Vacation", "The Go-Go's"))));

        m.put(Mood.ANXIOUS, new Playlist("Steady Breath", "Grounding, reassuring music to slow things down.", List.of(
                t("breathin", "Ariana Grande"),
                t("Breathe (2 AM)", "Anna Nalick"),
                t("Three Little Birds", "Bob Marley & The Wailers"),
                t("Don't Worry, Be Happy", "Bobby McFerrin"),
                t("The Middle", "Jimmy Eat World"),
                t("Keep Breathing", "Ingrid Michaelson"),
                t("Experience", "Ludovico Einaudi"),
                t("Saturn", "Sleeping At Last"),
                t("To Build a Home", "The Cinematic Orchestra"),
                t("Breathe", "Télépopmusik"),
                t("Sunrise", "Norah Jones"),
                t("Unwell", "Matchbox Twenty"))));

        m.put(Mood.ANGRY, new Playlist("Release Valve", "Loud, cathartic tracks to let it all out.", List.of(
                t("Killing in the Name", "Rage Against the Machine"),
                t("Given Up", "Linkin Park"),
                t("Seven Nation Army", "The White Stripes"),
                t("Smells Like Teen Spirit", "Nirvana"),
                t("Chop Suey!", "System of a Down"),
                t("Duality", "Slipknot"),
                t("Break Stuff", "Limp Bizkit"),
                t("You Oughta Know", "Alanis Morissette"),
                t("Misery Business", "Paramore"),
                t("Before He Cheats", "Carrie Underwood"),
                t("Sabotage", "Beastie Boys"),
                t("good 4 u", "Olivia Rodrigo"))));

        m.put(Mood.LONELY, new Playlist("Party of One", "Songs that understand the quiet.", List.of(
                t("Eleanor Rigby", "The Beatles"),
                t("Boulevard of Broken Dreams", "Green Day"),
                t("Creep", "Radiohead"),
                t("All by Myself", "Eric Carmen"),
                t("Dancing On My Own", "Robyn"),
                t("Nobody", "Mitski"),
                t("Space Oddity", "David Bowie"),
                t("Lonely", "Justin Bieber & benny blanco"),
                t("Alone", "Heart"),
                t("Lonely Day", "System of a Down"),
                t("Mr. Lonely", "Bobby Vinton"),
                t("The Loneliest", "Måneskin"))));

        m.put(Mood.SAD, new Playlist("Gentle Rain", "Songs that sit with you when things feel heavy.", List.of(
                t("Fix You", "Coldplay"),
                t("Everybody Hurts", "R.E.M."),
                t("Mad World", "Gary Jules"),
                t("Skinny Love", "Bon Iver"),
                t("Hallelujah", "Jeff Buckley"),
                t("Tears in Heaven", "Eric Clapton"),
                t("Breathe Me", "Sia"),
                t("Fade Into You", "Mazzy Star"),
                t("The Sound of Silence", "Simon & Garfunkel"),
                t("Liability", "Lorde"),
                t("Gravity", "John Mayer"),
                t("Unsteady", "X Ambassadors"))));

        m.put(Mood.HEARTBROKEN, new Playlist("Mending", "For a broken heart, from the ache to letting go.", List.of(
                t("Someone Like You", "Adele"),
                t("drivers license", "Olivia Rodrigo"),
                t("Somebody That I Used to Know", "Gotye ft. Kimbra"),
                t("Say Something", "A Great Big World & Christina Aguilera"),
                t("The Night We Met", "Lord Huron"),
                t("Nothing Compares 2 U", "Sinéad O'Connor"),
                t("Back to Black", "Amy Winehouse"),
                t("All Too Well", "Taylor Swift"),
                t("Jar of Hearts", "Christina Perri"),
                t("Too Good at Goodbyes", "Sam Smith"),
                t("I Can't Make You Love Me", "Bonnie Raitt"),
                t("Un-break My Heart", "Toni Braxton"))));

        return new PlaylistCatalog(m);
    }
}
