import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class ControlsScreen extends MenuScreen {

    private static final long serialVersionUID = 1L;

    static final int THRUST_LEFT = 0;
    static final int THRUST_RIGHT = 1;
    static final int ROTATE_LEFT = 2;
    static final int ROTATE_RIGHT = 3;
    static final int SHOOT = 4;
    static final int BLOCK = 5;

    static final String[] MENU_OPTIONS = { "Edit", "Reset to Defaults", "Back" };

    static final String[] actionDescriptions = { "Thrust Left", "Thrust Right", "Rotate Left", "Rotate Right", "Shoot",
            "Block" };
    static final int[] defaultKeyCodes = { 65, 68, 37, 39, 38, 40 };

    int[] currentKeyCodes = new int[defaultKeyCodes.length];
    String[] currentKeyDescriptions = new String[defaultKeyCodes.length];

    public ControlsScreen(int w, int h) {
        super(w, h, "Controls", MENU_OPTIONS);
        subtitle = "Select a key to change";
        setDefaultKeys();
    }

    public void setDefaultKeys() {
        for (int i = 0; i < defaultKeyCodes.length; i++) {
            currentKeyCodes[i] = defaultKeyCodes[i];
            currentKeyDescriptions[i] = lookupKeyDescritionFromFile(defaultKeyCodes[i]);
        }
    }

    public void listenForNextKeyCode(int i) {

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        In in = new In("keyLookup.txt");
        while (in.hasNextLine()) {
            int keyCode = in.readInt();
            String keyDescription = in.readLine().substring(1);

            KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, 0, false);
            inputMap.put(keyStroke, keyCode);
            actionMap.put(keyCode, new AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    currentKeyCodes[i] = keyCode;
                    currentKeyDescriptions[i] = lookupKeyDescritionFromFile(keyCode);
                }
            });
        }
        in.close();

        KeyStroke escPress = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        inputMap.put(escPress, "escPress");
        actionMap.put("escPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                inputMap.clear();
                actionMap.clear();
                setKeyBindings();
            }
        });

    }

    public static String lookupKeyDescritionFromFile(int code) {
        In in = new In("keyLookup.txt");
        while (in.hasNextLine()) {
            int keyCode = in.readInt();
            String keyDescription = in.readLine().substring(1);
            if (keyCode == code) {
                in.close();
                return keyDescription;
            }
        }
        in.close();
        return null;
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

        for (int i = 0; i < defaultKeyCodes.length; i++) {
            y += g2.getFontMetrics().getHeight() * 3;

            g2.setColor(Color.WHITE);

            drawLeftAlignedText(g2, WIDTH * 0.3, y, actionDescriptions[i]);
            drawRightAlignedText(g2, WIDTH * 0.7, y, "" + currentKeyDescriptions[i]);
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