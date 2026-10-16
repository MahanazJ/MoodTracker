package com.mooddiary;

import com.mooddiary.analysis.EmotionLexicon;
import com.mooddiary.analysis.MoodAnalyzer;
import com.mooddiary.analysis.PersonalLexicon;
import com.mooddiary.diary.DiaryService;
import com.mooddiary.recommendation.PlaylistCatalog;
import com.mooddiary.recommendation.PlaylistRecommender;
import com.mooddiary.storage.FileDiaryRepository;
import com.mooddiary.storage.FileLearningStore;
import com.mooddiary.storage.StorageException;
import com.mooddiary.ui.DiaryApp;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.time.Clock;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * Starts the diary. Entries are stored in ~/MoodDiary unless a folder is given:
 * {@code java -cp out com.mooddiary.Main [folder]}
 */
public final class Main {

    private Main() { }

    public static void main(String[] args) {
        Path home = args.length > 0 ? Path.of(args[0]) : Path.of(System.getProperty("user.home"), "MoodDiary");
        SwingUtilities.invokeLater(() -> start(home));
    }

    private static void start(Path home) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // the default look and feel is fine
        }
        try {
            FileLearningStore learning = new FileLearningStore(home.resolve("learned-words.txt"));
            MoodAnalyzer analyzer = new MoodAnalyzer(EmotionLexicon.standard(), new PersonalLexicon(learning.load()));
            DiaryService service = new DiaryService(
                    new FileDiaryRepository(home.resolve("entries"), msg -> System.err.println("[diary] " + msg)),
                    learning, analyzer, Clock.systemDefaultZone());
            PlaylistRecommender recommender = PlaylistRecommender.withDefaults(PlaylistCatalog.curated());

            DiaryApp app = new DiaryApp(service, recommender);
            JFrame frame = new JFrame("Mood Diary");
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (app.confirmClose()) {
                        frame.dispose();
                        System.exit(0);
                    }
                }
            });
            frame.setContentPane(app);
            frame.setMinimumSize(new Dimension(900, 620));
            frame.setSize(1240, 820);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (StorageException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Mood Diary couldn't start", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
