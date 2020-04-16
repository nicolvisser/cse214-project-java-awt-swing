import java.awt.Color;
import java.awt.Graphics2D;

public class HighScoreScreen extends MenuScreen {

    private static final long serialVersionUID = 1L;

    private static final String FILENAME_HIGHSCORES = "resources/highscores.txt";

    static final int NUM_ENTRIES = 10;
    static final String[] DEFAULT_OPTIONS = { "Reset", "Back" };
    static final String[] GAME_OVER_OPTIONS = { "Rename", "Back" };

    String[] names;
    int[] scores;
    int highlightedScore;

    public HighScoreScreen(int w, int h) {
        super(w, h, "High Scores", DEFAULT_OPTIONS);
        names = new String[NUM_ENTRIES];
        scores = new int[NUM_ENTRIES];
        highlightedScore = -1;
        loadFromFile();
    }

    public void loadFromFile() {
        In in = new In(FILENAME_HIGHSCORES);
        for (int i = 0; i < NUM_ENTRIES; i++) {
            names[i] = in.readLine();
            scores[i] = Integer.parseInt(in.readLine());
        }
        in.close();
    }

    public void saveToFile() {
        Out out = new Out(FILENAME_HIGHSCORES);
        for (int i = 0; i < NUM_ENTRIES; i++) {
            out.println(names[i]);
            out.println(scores[i]);
        }
        out.close();
    }

    public void addEntry(String name, int score) {
        if (isNewHighScore(score)) {

            names[NUM_ENTRIES - 1] = name;
            scores[NUM_ENTRIES - 1] = score;

            for (int i = NUM_ENTRIES - 2; i >= 0; i--) {
                if (scores[i + 1] >= scores[i]) {
                    String tempName = names[i];
                    names[i] = names[i + 1];
                    names[i + 1] = tempName;

                    int tempScore = scores[i];
                    scores[i] = scores[i + 1];
                    scores[i + 1] = tempScore;

                    highlightedScore = i; // highlight last index to show this user's high score on next draw
                } else {
                    break;
                }
            }

            saveToFile();
        }
    }

    public void renameHighlightedScore(String name) {
        names[highlightedScore] = name;
        saveToFile();
    }

    public boolean isNewHighScore(int score) {
        return score > scores[NUM_ENTRIES - 1];
    }

    public void resetHighScores() {
        for (int i = 0; i < NUM_ENTRIES; i++) {
            names[i] = "**********";
            scores[i] = 0;
        }
        saveToFile();
    }

    public void setHighlightedScore(int index) {
        highlightedScore = index;
    }

    public void resetHighlightedScore() {
        highlightedScore = -1;
    }

    public void setOptionsToGameOverOptions() {
        textOptions = GAME_OVER_OPTIONS;
    }

    public void resetOptions() {
        textOptions = DEFAULT_OPTIONS;
    }

    public void draw(Graphics2D g2) {
        double y = 0.1 * HEIGHT;

        // TITLE
        g2.setColor(Color.ORANGE);
        drawCenteredText(g2, XCENTER, y, title);

        // SUBTITLE
        y += (BUTTON_HEIGHT + BUTTON_SPACING);
        g2.setColor(Color.YELLOW);
        drawCenteredText(g2, XCENTER, y, subtitle);

        for (int i = 0; i < NUM_ENTRIES; i++) {
            y += g2.getFontMetrics().getHeight() * 3;

            g2.setColor(i == highlightedScore ? Color.GREEN : Color.WHITE);

            drawLeftAlignedText(g2, WIDTH * 0.2, y, "" + (i + 1));
            drawLeftAlignedText(g2, WIDTH * 0.3, y, names[i]);
            drawRightAlignedText(g2, WIDTH * 0.8, y, "" + scores[i]);
        }

        for (int i = 0; i < textOptions.length; i++) {
            y += (BUTTON_HEIGHT + BUTTON_SPACING);

            g2.setColor(Color.BLACK);
            fillCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            g2.setColor(i == highlightedOption ? Color.RED : Color.WHITE);
            drawCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            drawCenteredText(g2, XCENTER, y, textOptions[i]);
        }
    }

}