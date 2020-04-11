import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.*;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import geom.Rectangle;

public class MenuScreen extends JComponent {

    private static final long serialVersionUID = 1L;

    private final int WIDTH;
    private final int HEIGHT;

    private final int XCENTER;

    private final int BUTTON_WIDTH;
    private final int BUTTON_HEIGHT;
    private final int BUTTON_SPACING;

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
        BUTTON_HEIGHT = h / 10;
        BUTTON_SPACING = h / 20;

        setKeyBindings();
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

    private void setKeyBindings() {
        // Special thanks to https://www.youtube.com/watch?v=LNizNHaRV84&t=1484s
        // https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        KeyStroke keyUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
        inputMap.put(keyUp, "keyUp");
        actionMap.put("keyUp", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                changeSelectionUp();
            }

        });

        KeyStroke keyDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
        inputMap.put(keyDown, "keyDown");
        actionMap.put("keyDown", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                changeSelectionDown();
            }

        });

        KeyStroke keyEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        inputMap.put(keyEnter, "keyEnter");
        actionMap.put("keyEnter", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                selectCurrentOption();
            }

        });

        KeyStroke keyBack = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        inputMap.put(keyBack, "keyBack");
        actionMap.put("keyBack", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                selectOptionToGoBack();
            }

        });
    }

    public void changeSelectionUp() {
        highlightedOption--;
        if (highlightedOption < 0)
            highlightedOption += textOptions.length;
        StdAudio.play("resources/cut.wav");
    }

    public void changeSelectionDown() {
        highlightedOption++;
        if (highlightedOption >= textOptions.length)
            highlightedOption -= textOptions.length;
        StdAudio.play("resources/cut.wav");
    }

    public void selectCurrentOption() {
        selectedOption = highlightedOption;
        StdAudio.play("resources/click7.wav");
    }

    public void selectOptionToGoBack() {
        selectedOption = -2;
        StdAudio.play("resources/click7.wav");
    }

    public void draw(Graphics2D g2) {
        int y = HEIGHT / 4;

        // TITLE
        g2.setColor(Color.ORANGE);
        drawCenteredText(g2, XCENTER, y, title);

        // SUBTITLE
        y += (BUTTON_HEIGHT + BUTTON_SPACING);
        g2.setColor(Color.YELLOW);
        drawCenteredText(g2, XCENTER, y, subtitle);

        // OPTIONS
        for (int i = 0; i < textOptions.length; i++) {
            y += (BUTTON_HEIGHT + BUTTON_SPACING);

            g2.setColor(Color.BLACK);
            fillCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            g2.setColor(i == highlightedOption ? Color.RED : Color.WHITE);
            drawCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

            drawCenteredText(g2, XCENTER, y, textOptions[i]);
        }
    }

    public void drawCenteredText(Graphics2D g2, double x, double y, String text) {
        FontMetrics fm = g2.getFontMetrics();
        int wStr = fm.stringWidth(text);
        int hStr = fm.getHeight();
        float xStr = (float) (x - wStr / 2);
        float yStr = (float) (y + hStr / 2);
        g2.drawString(text, xStr, yStr);
    }

    public void drawCenteredRect(Graphics2D g2, double x, double y, double w, double h) {
        (new Rectangle(x, y, w, h)).draw(g2);
    }

    public void fillCenteredRect(Graphics2D g2, double x, double y, double w, double h) {
        (new Rectangle(x, y, w, h)).fill(g2);
    }

}