import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class InvadersPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final int pWidth;
    private final int pHeight;

    public enum DisplayState {
        MAIN_MENU, PLAYING, PAUSE, SAVE_GAME, LOAD_GAME, HIGH_SCORES, SETTINGS, CONTROLS, SET_RESOLUTION, GAME_OVER,
        QUIT;
    }

    public DisplayState activeDisplayState = DisplayState.MAIN_MENU;

    private static String[] mainMenuScreenOptions = { "New Game", "Load Game", "High Scores", "Settings", "Quit Game" };
    private static String[] pauseScreenOptions = { "Resume Game", "Save Game", "Quit To Main Menu" };
    private static String[] settingsScreenOptions = { "Set Resolution", "Controls", "Back" };
    private static String[] resolutionScreenOptions = { "600x600", "800x800", "1000x1000", "Cancel" };
    private static String[] saveGameScreenOptions = { "Slot 1", "Slot 2", "Slot 3", "Slot 4", "Cancel" };
    private static String[] loadGameScreenOptions = { "Slot 1", "Slot 2", "Slot 3", "Slot 4", "Cancel" };

    InvaderGameState loadedInvaderGameState;
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

        mainMenuScreen = new MenuScreen(width, height, "TestTitle", "TestSubTitle", mainMenuScreenOptions);
        pauseScreen = new MenuScreen(width, height, "Paused", pauseScreenOptions);
        settingsScreen = new MenuScreen(width, height, "Settings", settingsScreenOptions);
        resolutionScreen = new MenuScreen(width, height, "Change Resolution", resolutionScreenOptions);
        saveGameScreen = new MenuScreen(width, height, "Save Game", saveGameScreenOptions);
        loadGameScreen = new MenuScreen(width, height, "Load Game", loadGameScreenOptions);
        highScoreScreen = new HighScoreScreen(width, height);

        add(mainMenuScreen); // add jcomponent to panel to let swing handle key bindings
    }

    public void update() {

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
                        break;

                    case 4: // quit
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
                break;
            case CONTROLS:
                break;
            case SET_RESOLUTION:
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
                System.exit(0);
            }
        });
    }

}