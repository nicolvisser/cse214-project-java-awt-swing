import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import geom.Rectangle;

public class MenuScreen extends DisplayComponent {

    private static final long serialVersionUID = 1L;

    protected final int WIDTH;
    protected final int HEIGHT;

    protected final int XCENTER;

    protected final int BUTTON_WIDTH;
    protected final int BUTTON_HEIGHT;
    protected final int BUTTON_SPACING;

    public String title;
    public String subtitle;

    public String[] textOptions;
    public int highlightedOption = 0;

    public int selectedOption = -1;

    public MenuScreen(int w, int h, String title, String[] textOptionsArray) {
        this(w, h, title, "", textOptionsArray);
    }

    public MenuScreen(int w, int h, String title, String subtitle, String[] textOptions) {
        this.title = title;
        this.subtitle = subtitle;
        this.textOptions = textOptions;

        WIDTH = w;
        HEIGHT = h;

        XCENTER = WIDTH / 2;

        BUTTON_WIDTH = w / 2;
        BUTTON_HEIGHT = h / 20;
        BUTTON_SPACING = h / 30;

        addKeyListener(new MenuControlKeyListener());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setOptions(String[] textOptionsArray) {
        textOptions = textOptionsArray;
    }

    public void resetSelection() {
        selectedOption = -1;
    }

    public void resetHiglight() {
        highlightedOption = 0;
    }

    protected class MenuControlKeyListener extends KeyAdapter {

        MenuControlKeyListener() {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    changeSelectionUp();
                    break;

                case KeyEvent.VK_DOWN:
                    changeSelectionDown();
                    break;

                case KeyEvent.VK_ENTER:
                    selectCurrentOption();
                    break;

                case KeyEvent.VK_ESCAPE:
                    selectOptionToGoBack();
                    break;

                default:
                    break;
            }
        }

    }

    public void changeSelectionUp() {
        highlightedOption--;
        if (highlightedOption < 0)
            highlightedOption += textOptions.length;
        GameAudio.playSoundMenuChange();
    }

    public void changeSelectionDown() {
        highlightedOption++;
        if (highlightedOption >= textOptions.length)
            highlightedOption -= textOptions.length;
        GameAudio.playSoundMenuChange();
    }

    public void selectCurrentOption() {
        selectedOption = highlightedOption;
        GameAudio.playSoundMenuSelect();
    }

    public void selectOptionToGoBack() {
        selectedOption = -2;
        GameAudio.playSoundMenuSelect();
    }

    @Override
    public void draw(Graphics2D g2) {
        int y = HEIGHT / 4;

        // TITLE
        g2.setColor(Color.ORANGE);
        Utils.drawCenteredText(g2, XCENTER, y, title, 3);

        // SUBTITLE
        y += (BUTTON_HEIGHT + BUTTON_SPACING);
        g2.setColor(Color.YELLOW);
        Utils.drawCenteredText(g2, XCENTER, y, subtitle, 2);

        // OPTIONS
        for (int i = 0; i < textOptions.length; i++) {
            y += (BUTTON_HEIGHT + BUTTON_SPACING);

            g2.setColor(Color.BLACK);
            fillCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            g2.setColor(i == highlightedOption ? Color.RED : Color.WHITE);
            drawCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            Utils.drawCenteredText(g2, XCENTER, y, textOptions[i]);
        }
    }

    protected void drawCenteredRect(Graphics2D g2, double x, double y, double w, double h) {
        (new Rectangle(x, y, w, h)).draw(g2);
    }

    protected void fillCenteredRect(Graphics2D g2, double x, double y, double w, double h) {
        (new Rectangle(x, y, w, h)).fill(g2);
    }

}