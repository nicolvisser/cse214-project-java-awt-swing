import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ControlsScreen extends MenuScreen {

    private static final long serialVersionUID = 1L;

    // Constants relating to MenuScreen class
    private static final String[] MENU_OPTIONS = { "Thrust Left", "Thrust Right", "Rotate Left", "Rotate Right",
            "Shoot", "Block", "Reset to Defaults", "Back" };
    private static final String DEFAULT_SUBTITLE = "Please select an option or key from the menu:";

    // Constants relating to ControlsScreen class
    private static final int NUM_CONTROLS = 6;
    private static final int[] DEFAULT_KEYCODES = { 65, 68, 37, 39, 38, 40 };
    private static String FILENAME_KEY_CONFIG = "resources/keys.txt";
    private static String FILENAME_KEY_LOOKUP = "resources/keyLookup.txt";

    private int[] currentKeyCodes = new int[NUM_CONTROLS];
    private String[] currentKeyDescriptions = new String[NUM_CONTROLS];
    private int currentlyEditingOption = -1; // used to let GUI menu option that is currently being edited flash

    public ControlsScreen(int w, int h) {
        super(w, h, "Controls", MENU_OPTIONS);
        subtitle = DEFAULT_SUBTITLE;
        setKeysFromFile();


        setFocusTraversalKeysEnabled(false);
        // <--- this is to allow TAB key to be picked up by keyListener, see
        // https://stackoverflow.com/questions/8275204/how-can-i-listen-to-a-tab-key-pressed-typed-in-java
    }

    private static String lookupKeyDescriptionFromFile(int code) {
        In in = new In(FILENAME_KEY_LOOKUP);
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

    public void setDefaultKeys() {
        for (int i = 0; i < NUM_CONTROLS; i++) {
            currentKeyCodes[i] = DEFAULT_KEYCODES[i];
            currentKeyDescriptions[i] = lookupKeyDescriptionFromFile(DEFAULT_KEYCODES[i]);
        }
        saveKeysToFile();
    }

    private void setKeysFromFile() {
        In in = null;
        try {
            in = new In(FILENAME_KEY_CONFIG);
            for (int i = 0; i < NUM_CONTROLS; i++) {
                currentKeyCodes[i] = in.readInt();
                currentKeyDescriptions[i] = lookupKeyDescriptionFromFile(currentKeyCodes[i]);
            }
            in.close();
        } catch (Exception e) {
            if (in != null) {
                System.out.println("Failed to load key configuration file. Resetting to default.");
                // e.printStackTrace();
                in.close();
            }
            setDefaultKeys();
        }
    }

    private void saveKeysToFile() {
        Out out = new Out(FILENAME_KEY_CONFIG);
        for (int key : currentKeyCodes) {
            out.println(key);
        }
        out.close();
    }

    private class ChangeControlToNextKeyPressedListener extends KeyAdapter {

        int controlIndex;

        ChangeControlToNextKeyPressedListener(int controlIndex) {
            this.controlIndex = controlIndex;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                stopListeningForUserInput();
            } else {
                String keyDescription = lookupKeyDescriptionFromFile(e.getKeyCode());
                if (keyDescription != null) {
                    currentKeyCodes[controlIndex] = e.getKeyCode();
                    currentKeyDescriptions[controlIndex] = keyDescription;
                    saveKeysToFile();
                    stopListeningForUserInput();
                }
            }
        }
    }

    private void startListeningForUserInput(int controlIndex) {

        // remove active key listeners
        for (KeyListener l : getKeyListeners()) {
            removeKeyListener(l);
        }

        addKeyListener(new ChangeControlToNextKeyPressedListener(controlIndex));

        subtitle = "Press a key to change. Press escape to cancel.";
        currentlyEditingOption = controlIndex; // for draw method to be able to flash option
    }

    private void stopListeningForUserInput() {

        // remove active key listeners
        for (KeyListener l : getKeyListeners()) {
            removeKeyListener(l);
        }

        addKeyListener(new MenuControlKeyListener());

        subtitle = DEFAULT_SUBTITLE;
        currentlyEditingOption = -1; // for draw method to stop flashing
    }

    public int[] getCurrentControlsConfig() {
        return currentKeyCodes;
    }

    // Override some methods from MenuScreen class:

    @Override
    public void draw(Graphics2D g2) {
        double y = 0.1 * HEIGHT;

        // TITLE
        g2.setColor(Color.ORANGE);
        Utils.drawCenteredText(g2, XCENTER, y, title);

        // SUBTITLE
        y += (BUTTON_HEIGHT + BUTTON_SPACING);
        g2.setColor(Color.YELLOW);
        Utils.drawCenteredText(g2, XCENTER, y, subtitle);

        for (int i = 0; i < textOptions.length; i++) {
            y += (BUTTON_HEIGHT + BUTTON_SPACING);

            if (i < NUM_CONTROLS) {
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
                Utils.drawLeftAlignedText(g2, XCENTER - BUTTON_WIDTH / 2.5, y, textOptions[i]);
                Utils.drawRightAlignedText(g2, XCENTER + BUTTON_WIDTH / 2.5, y, "" + currentKeyDescriptions[i]);

            } else {
                // IS A DEFAULT TYPE OF MENU OPTION

                g2.setColor(Color.BLACK);
                fillCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

                g2.setColor(i == highlightedOption ? Color.RED : Color.WHITE);
                drawCenteredRect(g2, XCENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);

                Utils.drawCenteredText(g2, XCENTER, y, textOptions[i]);
            }

        }
    }

    @Override
    public void selectCurrentOption() {
        if (highlightedOption < NUM_CONTROLS) {
            // start listening for user input to set control
            startListeningForUserInput(highlightedOption);
            resetSelection();
        } else {
            // handle like normal menu button
            super.selectCurrentOption();
        }

    }

}