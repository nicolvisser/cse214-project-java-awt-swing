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

    static final String[] MENU_OPTIONS = { "Thrust Left", "Thrust Right", "Rotate Left", "Rotate Right", "Shoot",
            "Block", "Reset to Defaults", "Back" };
    static final String DEFAULT_SUBTITLE = "Please select an option or key from the menu:";

    static final int numControls = 6;
    static final int[] defaultKeyCodes = { 65, 68, 37, 39, 38, 40 };

    int[] currentKeyCodes = new int[numControls];
    String[] currentKeyDescriptions = new String[numControls];

    int currentlyEditingOption = -1;

    public ControlsScreen(int w, int h) {
        super(w, h, "Controls", MENU_OPTIONS);
        subtitle = DEFAULT_SUBTITLE;
        setDefaultKeys();
    }

    public void setDefaultKeys() {
        for (int i = 0; i < numControls; i++) {
            currentKeyCodes[i] = defaultKeyCodes[i];
            currentKeyDescriptions[i] = lookupKeyDescritionFromFile(defaultKeyCodes[i]);
        }
    }

    public void letUserSetKeyCodeForControl(int controlIndex) {

        subtitle = "Press a key to change. Press escape to cancel.";
        currentlyEditingOption = controlIndex;

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // for each predefined keycode in a textfile
        // create a key binding that will set selected control to that keycode
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
                    currentKeyCodes[controlIndex] = keyCode;
                    currentKeyDescriptions[controlIndex] = keyDescription;

                    // clear current key bindings for this panel
                    inputMap.clear();
                    actionMap.clear();

                    // reinstate original key bindings for panel
                    setKeyBindings();

                    // no longer editing any option (no need to highlight in green)
                    currentlyEditingOption = -1;

                    subtitle = DEFAULT_SUBTITLE;

                }
            });
        }
        in.close();

        // set key binding for escape - a way for user to exit listen loop
        KeyStroke escPress = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        inputMap.put(escPress, "escPress");
        actionMap.put("escPress", new AbstractAction() {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) { // if escape is pressed
                // clear current key bindings for this panel
                inputMap.clear();
                actionMap.clear();

                // reinstate original key bindings for panel
                setKeyBindings();

                // no longer editing any option (no need to highlight in green)
                currentlyEditingOption = -1;

                subtitle = DEFAULT_SUBTITLE;

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

        for (int i = 0; i < textOptions.length; i++) {
            y += (BUTTON_HEIGHT + BUTTON_SPACING);

            if (i < numControls) {
                // IS A EDITABLE CONTROL

                g2.setColor(Color.BLACK);
                fillCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

                g2.setColor(i == highlightedOption ? Color.RED : Color.WHITE);
                drawCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

                // let content flash to indicate editing
                if (i == currentlyEditingOption) {
                    if (System.nanoTime() % 1000000000 < 500000000) {
                        continue;
                    }
                }
                drawLeftAlignedText(g2, XCENTER - BUTTON_WIDTH / 2.5, y, textOptions[i]);
                drawRightAlignedText(g2, XCENTER + BUTTON_WIDTH / 2.5, y, "" + currentKeyDescriptions[i]);

            } else {
                // IS A DEFAULT TYPE OF MENU OPTION

                g2.setColor(Color.BLACK);
                fillCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

                g2.setColor(i == highlightedOption ? Color.RED : Color.WHITE);
                drawCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

                drawCenteredText(g2, XCENTER, y, textOptions[i]);
            }

        }
    }

    @Override
    public void selectCurrentOption() {
        if (highlightedOption < numControls) {
            // start listening for user input to set control
            letUserSetKeyCodeForControl(highlightedOption);
            resetSelection();
        } else {
            // handle like normal menu button
            super.selectCurrentOption();
        }

    }

}