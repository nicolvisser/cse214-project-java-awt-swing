import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import geom.Vector2D;

public class InvadersPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private boolean quitFlag = false;

    private final int pWidth;
    private final int pHeight;

    public enum DisplayState {
        MAIN_MENU, PLAYING, PAUSE, SAVE_GAME, LOAD_GAME, HIGH_SCORES, SETTINGS, CONTROLS, SET_RESOLUTION, GAME_OVER,
        QUIT;
    }

    public DisplayState activeDisplayState;

    private static String[] mainMenuScreenOptions = { "New Game", "Load Game", "High Scores", "Settings", "Quit Game" };
    private static String[] pauseScreenOptions = { "Resume Game", "Save Game", "Quit To Main Menu" };
    private static String[] settingsScreenOptions = { "Set Resolution", "Controls", "Back" };
    private static String[] resolutionScreenOptions = { "600x600", "800x800", "1000x1000", "Cancel" };
    private static String[] saveGameScreenOptions = { "Slot 1", "Slot 2", "Slot 3", "Slot 4", "Cancel" };
    private static String[] loadGameScreenOptions = { "Slot 1", "Slot 2", "Slot 3", "Slot 4", "Cancel" };

    private Starfield starfield;
    private InvaderGameState loadedInvaderGameState;
    private MenuScreen mainMenuScreen;
    private MenuScreen pauseScreen;
    private MenuScreen settingsScreen;
    private MenuScreen resolutionScreen;
    private MenuScreen saveGameScreen;
    private MenuScreen loadGameScreen;
    private HighScoreScreen highScoreScreen;

    public InvadersPanel(int width, int height) {
        pWidth = width;
        pHeight = height;
        setPreferredSize(new Dimension(width, height));
        setIgnoreRepaint(true);
        setKeyBindings();

        starfield = new Starfield(width, height);

        mainMenuScreen = new MenuScreen(width, height, "TestTitle", "TestSubTitle", mainMenuScreenOptions);
        pauseScreen = new MenuScreen(width, height, "Paused", pauseScreenOptions);
        settingsScreen = new MenuScreen(width, height, "Settings", settingsScreenOptions);
        resolutionScreen = new MenuScreen(width, height, "Change Resolution", resolutionScreenOptions);
        saveGameScreen = new MenuScreen(width, height, "Save Game", saveGameScreenOptions);
        loadGameScreen = new MenuScreen(width, height, "Load Game", loadGameScreenOptions);
        highScoreScreen = new HighScoreScreen(width, height);

        activeDisplayState = DisplayState.MAIN_MENU;
        add(mainMenuScreen);
    }

    public void update() {

        if (activeDisplayState == DisplayState.PLAYING)
            starfield.update(loadedInvaderGameState.getVelocityForBackground());
        else
            starfield.update(new Vector2D(-pWidth / 50, -pHeight / 50));

        switch (activeDisplayState) {
            case MAIN_MENU:
                switch (mainMenuScreen.selectedOption) {
                    case -2: // back (nowhere to go back to)
                    case -1: // not yet selected
                        break;

                    case 0: // new game
                        mainMenuScreen.resetSelection();
                        removeAll();
                        loadedInvaderGameState = new InvaderGameState(pWidth, pHeight);
                        add(loadedInvaderGameState);
                        activeDisplayState = DisplayState.PLAYING;
                        break;

                    case 1: // loadgame
                        break;

                    case 2: // high scores
                        mainMenuScreen.resetSelection();
                        removeAll();
                        add(highScoreScreen);
                        activeDisplayState = DisplayState.HIGH_SCORES;
                        break;

                    case 3: // settings
                        mainMenuScreen.resetSelection();
                        removeAll();
                        add(settingsScreen);
                        activeDisplayState = DisplayState.SETTINGS;
                        break;

                    case 4: // quit
                        quitFlag = true;
                        break;

                    default:
                        break;
                }
                break;

            case PLAYING:
                loadedInvaderGameState.update();

                if (loadedInvaderGameState.pauseFlag) {
                    loadedInvaderGameState.resetFlags();
                    activeDisplayState = DisplayState.PAUSE;
                    removeAll();
                    add(pauseScreen);
                    break;
                }

                if (loadedInvaderGameState.gameOverFlag) {
                    loadedInvaderGameState.resetFlags();
                    activeDisplayState = DisplayState.GAME_OVER;
                    break;
                }
                break;

            case PAUSE:

                switch (pauseScreen.selectedOption) {
                    case -2: // back (to playing game)
                        pauseScreen.resetSelection();
                        activeDisplayState = DisplayState.PLAYING;
                        removeAll();
                        add(loadedInvaderGameState);

                    case -1: // not yet selected
                        break;

                    case 0: // resume game
                        pauseScreen.selectOptionToGoBack();
                        break;

                    case 1: // save game
                        pauseScreen.resetSelection();
                        setMenuScreenOptionsFromSaveFiles(saveGameScreen);
                        activeDisplayState = DisplayState.SAVE_GAME;
                        removeAll();
                        add(saveGameScreen);
                        break;

                    case 2: // quit to main menu
                        pauseScreen.resetSelection();
                        pauseScreen.resetHiglight();
                        activeDisplayState = DisplayState.MAIN_MENU;
                        removeAll();
                        add(mainMenuScreen);
                        break;

                    default:
                        break;
                }
                break;

            case SAVE_GAME:
                switch (saveGameScreen.selectedOption) {
                    case -2: // back (to pause screen)
                        saveGameScreen.resetSelection();
                        saveGameScreen.resetHiglight();
                        activeDisplayState = DisplayState.PAUSE;
                        break;

                    case -1: // not yet selected
                        break;

                    case 0: // slot 1
                    case 1: // slot 2
                    case 2: // slot 3
                    case 3: // slot 4s
                        int slot = saveGameScreen.selectedOption + 1;
                        if (saveInvaderGameState(slot)) {
                            saveGameScreen.resetHiglight();
                            activeDisplayState = DisplayState.PLAYING;
                        } else {
                            saveGameScreen.setSubtitle("Failed to save game in slot " + slot + ".");
                        }
                        saveGameScreen.resetSelection();
                        break;

                    case 4: // cancel
                        saveGameScreen.selectOptionToGoBack();
                        break;

                    default:
                        break;
                }
                break;
            case LOAD_GAME:
                break;
            case HIGH_SCORES:
                switch (highScoreScreen.selectedOption) {
                    case -2: // back (to main menu)
                        highScoreScreen.resetSelection();
                        highScoreScreen.resetHiglight();
                        removeAll();
                        add(mainMenuScreen);
                        activeDisplayState = DisplayState.MAIN_MENU;
                        break;

                    case -1: // not yet selected
                        break;

                    case 0: // reset
                        highScoreScreen.resetHighScores();
                        highScoreScreen.resetSelection();
                        break;

                    case 1: // back
                        highScoreScreen.selectOptionToGoBack();
                        break;

                    default:
                        break;
                }
                break;

            case SETTINGS:
                switch (settingsScreen.selectedOption) {
                    case -2: // back (to main menu)
                        settingsScreen.resetSelection();
                        settingsScreen.resetHiglight();
                        removeAll();
                        add(mainMenuScreen);
                        activeDisplayState = DisplayState.MAIN_MENU;
                        break;

                    case -1: // not yet selected
                        break;

                    case 0: // set resolution
                        settingsScreen.resetSelection();
                        removeAll();
                        add(resolutionScreen);
                        activeDisplayState = DisplayState.SET_RESOLUTION;
                        break;

                    case 1: // controls
                        settingsScreen.resetSelection();
                        // removeAll();
                        // TODO: add(controlScreen);
                        // activeDisplayState = DisplayState.CONTROLS;
                        break;

                    case 2: // back
                        settingsScreen.selectOptionToGoBack();
                        break;

                    default:
                        break;
                }
                break;

            case CONTROLS:
                break;
            case SET_RESOLUTION:
                switch (resolutionScreen.selectedOption) {
                    case -2: // back (to setting screen)
                        resolutionScreen.resetSelection();
                        resolutionScreen.resetHiglight();
                        removeAll();
                        add(settingsScreen);
                        activeDisplayState = DisplayState.SETTINGS;
                        break;

                    case -1: // not yet selected
                        break;

                    case 0: // 600x600
                        // TODO: setupStdDrawCanvas(600, 600);
                        resolutionScreen.resetSelection();
                        break;

                    case 1: // 800x800
                        // TODO: setupStdDrawCanvas(800, 800);
                        resolutionScreen.resetSelection();
                        break;

                    case 2: // 1000x1000
                        // TODO: setupStdDrawCanvas(1000, 1000);
                        resolutionScreen.resetSelection();
                        break;

                    case 3: // cancel
                        resolutionScreen.selectOptionToGoBack();
                        break;

                    default:
                        break;
                }
                break;

            case GAME_OVER:
                break;

            default:
                break;
        }
    }

    public void draw(Graphics2D g2) {

        // draw black background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, pWidth, pHeight);

        starfield.draw(g2);

        switch (activeDisplayState) {
            case MAIN_MENU:
                mainMenuScreen.draw(g2);
                break;
            case PLAYING:
                loadedInvaderGameState.draw(g2);
                break;
            case PAUSE:
                pauseScreen.draw(g2);
                break;
            case SAVE_GAME:
                saveGameScreen.draw(g2);
                break;
            case LOAD_GAME:
                loadGameScreen.draw(g2);
                break;
            case HIGH_SCORES:
                highScoreScreen.draw(g2);
                break;
            case SETTINGS:
                settingsScreen.draw(g2);
                break;
            case CONTROLS:
                break;
            case SET_RESOLUTION:
                resolutionScreen.draw(g2);
                break;
            case GAME_OVER:
                break;

            default:
                break;
        }

    }

    private void setKeyBindings() {
        // Special thanks to https://www.youtube.com/watch?v=LNizNHaRV84&t=1484s
        // https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        KeyStroke quitKeyPress = KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false);
        inputMap.put(quitKeyPress, "quitKeyPress");
        actionMap.put("quitKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                quitFlag = true;
            }
        });
    }

    public boolean readyToQuit() {
        return quitFlag;
    }

    static String getPlainTimestamp() {
        String date = java.time.LocalDate.now().toString().replaceAll("-", "");
        String time = java.time.LocalTime.now().toString().substring(0, 6).replaceAll(":", "");
        return date + time;
    }

    static String formatTimestamp(String plainTimestamp) {
        String date = plainTimestamp.substring(0, 4) + "-" + plainTimestamp.substring(4, 6) + "-"
                + plainTimestamp.substring(6, 8);
        String time = plainTimestamp.substring(8, 10) + ":" + plainTimestamp.substring(10, 12);
        return date + " " + time;
    }

    static String filenameOfSaveFile(int slot) {
        File folder = new File(System.getProperty("user.dir")); // gets "working directory"
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String filename = listOfFiles[i].getName();
                if (filename.startsWith("savedata_slot" + slot)) {
                    return filename;
                }
            }
        }
        return null;
    }

    static void setMenuScreenOptionsFromSaveFiles(MenuScreen menuScreen) {
        String[] options = new String[5];

        for (int i = 0; i < 4; i++) {
            String filename = filenameOfSaveFile(i + 1);
            if (filename != null) {
                options[i] = "Slot " + (i + 1) + " - " + formatTimestamp(filename.substring(15));
            } else {
                options[i] = "Slot " + (i + 1) + " - Empty";
            }
        }

        options[4] = "Cancel";

        menuScreen.setOptions(options);

    }

    boolean saveInvaderGameState(int slot) {
        loadedInvaderGameState.resetFlags(); // so as not to save true flags in game state
        try {
            // delete old savegame (if any) of this slot first:
            String existingFilename = filenameOfSaveFile(slot);
            if (existingFilename != null) {
                File file = new File(existingFilename);
                if (file.delete()) {
                    System.out.println("Old savegame, " + existingFilename + ", deleted successfully.");
                }
            }

            String filename = "savedata_slot" + slot + "_" + getPlainTimestamp() + ".dat";
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(loadedInvaderGameState);
            out.close();
            System.out.println("New savegame, " + filename + ", created successfully.");
            return true;
        } catch (Exception e1) {
            e1.printStackTrace();
            // if created a file and save failed, delete that corrupted file
            String existingFilename = filenameOfSaveFile(slot);
            if (existingFilename != null) {
                File file = new File(existingFilename);
                file.delete();
            }
            return false;
            // dealt with error message for user inside gameloop using return value
        }

    }

    boolean loadInvaderGameState(int slot) {
        try {
            String filename = filenameOfSaveFile(slot);
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            loadedInvaderGameState = (InvaderGameState) in.readObject();
            in.close();
            loadGameScreen.setSubtitle(""); // clear error message if any
            return true;
        } catch (Exception e1) {
            // e1.printStackTrace();
            return false;
            // dealt with error message for user inside gameloop using return value
        }
    }

}