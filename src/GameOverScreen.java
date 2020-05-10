import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameOverScreen extends HighScoreScreen {

    private static final long serialVersionUID = 1L;

    static final String[] DEFAULT_OPTIONS = { "Rename", "Back to Main Menu" };
    int highlightedScore;

    boolean isRenaming = false;
    StringBuilder typedName = new StringBuilder();

    public GameOverScreen(int w, int h) {
        super(w, h);
        textOptions = DEFAULT_OPTIONS;
        highlightedScore = -1;
        title = "GAME OVER!";
    }

    public void setLastGameScore(int score) {
        subtitle = "YOUR SCORE: " + score;
        if (isNewHighScore(score)) {
            addEntry("No Name", score);
        }
    }

    private boolean isNewHighScore(int score) {
        return score >= scores[NUM_ENTRIES - 1];
    }

    private void addEntry(String name, int score) {
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

    private class RenamingKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                    typedName.setLength(Math.max(typedName.length() - 1, 0));
                    break;
                case KeyEvent.VK_ENTER:
                    stopRenaming();
                    break;

                default:
                    break;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // add any alphanumeric or space characters to stringbuilder
            char c = e.getKeyChar();
            if ((Character.isLetterOrDigit(c) || c == ' ') && typedName.length() < 20) {
                typedName.append(c);
            }
        }
    }

    public void startRenaming() {
        isRenaming = true;
        typedName = new StringBuilder();

        textOptions[0] = "Done";

        // stop listening for menu input
        for (KeyListener l : getKeyListeners()) {
            removeKeyListener(l);
        }

        addKeyListener(new RenamingKeyListener());

    }

    public void stopRenaming() {
        isRenaming = false;

        textOptions[0] = "Rename";

        // stop listening for menu input
        for (KeyListener l : getKeyListeners()) {
            removeKeyListener(l);
        }

        addKeyListener(new MenuControlKeyListener());

        names[highlightedScore] = typedName.toString();

        saveToFile();
    }

    public void setHighlightedScore(int index) {
        highlightedScore = index;
    }

    public void resetHighlightedScore() {
        highlightedScore = -1;
    }

    @Override
    public void draw(Graphics2D g2) {
        double y = 0.1 * HEIGHT;

        // TITLE
        g2.setColor(Color.ORANGE);
        Utils.drawCenteredText(g2, XCENTER, y, title, 3);

        // SUBTITLE
        y += (BUTTON_HEIGHT + BUTTON_SPACING);
        g2.setColor(Color.YELLOW);
        Utils.drawCenteredText(g2, XCENTER, y, subtitle, 2);

        for (int i = 0; i < NUM_ENTRIES; i++) {
            y += g2.getFontMetrics().getHeight() * 3;

            g2.setColor(Color.WHITE);

            String name;

            if (i == highlightedScore) {
                g2.setColor(Color.GREEN);
            }
            if (i == highlightedScore && isRenaming) {
                name = typedName.toString();
            } else {
                name = names[i];
            }

            Utils.drawLeftAlignedText(g2, WIDTH * 0.2, y, "" + (i + 1));
            Utils.drawLeftAlignedText(g2, WIDTH * 0.3, y, name);
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

    @Override
    public void selectCurrentOption() {
        if (highlightedOption == 0) {
            startRenaming();
        } else {
            super.selectCurrentOption();
        }
    }
}