import java.awt.Color;
import java.awt.Graphics2D;

public class HighScoreScreen extends MenuScreen {

    private static final long serialVersionUID = 1L;

    private static final String FILENAME_HIGHSCORES = "resources/highscores.txt";

    static final int NUM_ENTRIES = 10;
    static final String[] DEFAULT_OPTIONS = { "Reset", "Back" };

    String[] names;
    int[] scores;

    public HighScoreScreen(int w, int h) {
        super(w, h, "High Scores", DEFAULT_OPTIONS);
        names = new String[NUM_ENTRIES];
        scores = new int[NUM_ENTRIES];
        loadFromFile();
    }

    public void loadFromFile() {
        try {
            In in = new In(FILENAME_HIGHSCORES);
            for (int i = 0; i < NUM_ENTRIES; i++) {
                names[i] = in.readLine();
                scores[i] = Integer.parseInt(in.readLine());
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            resetHighScores();
        }
    }

    public void saveToFile() {
        Out out = new Out(FILENAME_HIGHSCORES);
        for (int i = 0; i < NUM_ENTRIES; i++) {
            out.println(names[i]);
            out.println(scores[i]);
        }
        out.close();
    }

    public void resetHighScores() {
        for (int i = 0; i < NUM_ENTRIES; i++) {
            names[i] = "**********";
            scores[i] = 0;
        }
        saveToFile();
    }

    public void draw(Graphics2D g2) {
        double y = 0.1 * HEIGHT;

        // TITLE
        g2.setColor(Color.ORANGE);
        Utils.drawCenteredText(g2, XCENTER, y, title);

        // SUBTITLE
        y += (BUTTON_HEIGHT + BUTTON_SPACING);
        g2.setColor(Color.YELLOW);
        Utils.drawCenteredText(g2, XCENTER, y, subtitle);

        for (int i = 0; i < NUM_ENTRIES; i++) {
            y += g2.getFontMetrics().getHeight() * 3;

            g2.setColor(Color.WHITE);

            Utils.drawLeftAlignedText(g2, WIDTH * 0.2, y, "" + (i + 1));
            Utils.drawLeftAlignedText(g2, WIDTH * 0.3, y, names[i]);
            Utils.drawRightAlignedText(g2, WIDTH * 0.8, y, "" + scores[i]);
        }

        for (int i = 0; i < textOptions.length; i++) {
            y += (BUTTON_HEIGHT + BUTTON_SPACING);

            g2.setColor(Color.BLACK);
            fillCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            g2.setColor(i == highlightedOption ? Color.RED : Color.WHITE);
            drawCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            Utils.drawCenteredText(g2, XCENTER, y, textOptions[i]);
        }
    }

}