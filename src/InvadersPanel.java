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

import geom.Vector2D;

public class InvadersPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private boolean quitFlag = false;

    // stores size of frame passed down via constructor
    private final int width;
    private final int height;

    // defined different states panel can be in, which is used to determine what
    // component to display on panel at what time
    public enum DisplayState {
        MAIN_MENU, PLAYING, PAUSE, HIGH_SCORES, SETTINGS, CONTROLS, SET_RESOLUTION, GAME_OVER, QUIT;
    }

    // stores current state the panel is in
    public DisplayState activeDisplayState;

    // define the text for screens that make use of the default MenuScreen object
    private static final String[] mainMenuScreenOptions = { "New Game", "High Scores", "Settings", "Quit Game" };
    private static final String[] pauseScreenOptions = { "Resume Game", "Quit To Main Menu" };
    private static final String[] settingsScreenOptions = { "Set Resolution", "Controls", "Back" };
    private static final String[] resolutionScreenOptions = { "600x600", "800x800", "1000x1000", "Cancel" }; // TODO:
                                                                                                             // Placeholders
                                                                                                             // only,
                                                                                                             // not
                                                                                                             // functioning
                                                                                                             // yet

    // declare components different components that will at some point be drawn on
    // panel
    private Starfield starfield;
    private InvaderGameState loadedInvaderGameState;
    private MenuScreen mainMenuScreen;
    private MenuScreen pauseScreen;
    private MenuScreen settingsScreen;
    private MenuScreen resolutionScreen;
    private HighScoreScreen highScoreScreen;
    private GameOverScreen gameOverScreen;
    private ControlsScreen controlsScreen;

    public InvadersPanel(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        setIgnoreRepaint(true);
        setKeyBindings();

        starfield = new Starfield(width, height);

        mainMenuScreen = new MenuScreen(width, height, "Invaders", "Main Menu", mainMenuScreenOptions);
        pauseScreen = new MenuScreen(width, height, "Paused", pauseScreenOptions);
        settingsScreen = new MenuScreen(width, height, "Settings", settingsScreenOptions);
        resolutionScreen = new MenuScreen(width, height, "Change Resolution", resolutionScreenOptions);
        highScoreScreen = new HighScoreScreen(width, height);
        gameOverScreen = new GameOverScreen(width, height);
        controlsScreen = new ControlsScreen(width, height);

        activeDisplayState = DisplayState.MAIN_MENU;
        add(mainMenuScreen);
    }

    private void setKeyBindings() {
        // TODO: Sort out issue with quit on Q press when renaming highscore

        // Using key binding for q key to quit.
        // The keybinding works whether panel has focus or not.
        // Special thanks to https://www.youtube.com/watch?v=LNizNHaRV84&t=1484s
        // Also see https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html
        //
        // Considered using keybinding for other classes as well. It would have worked
        // very nice for
        // creating custom controls. However it seems that key bindings can't recognize
        // a modifier key
        // (shift, alt, control) press on its own.

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

    public void update() {

        // update starfield based on whether game is playing or menu is showing
        if (activeDisplayState == DisplayState.PLAYING)
            starfield.update(loadedInvaderGameState.getVelocityForBackground());
        else
            starfield.update(new Vector2D(-width / 50, -height / 50));

        // update active scren
        switch (activeDisplayState) {
            case MAIN_MENU:

                // ensure the current screen has focus in order for keylistener to work
                // TODO try and combine repeating code for every screen
                if (!mainMenuScreen.hasFocus()) {
                    mainMenuScreen.requestFocusInWindow();
                }

                // decide what to do if user selected a option in the menu
                switch (mainMenuScreen.selectedOption) {
                    case -2: // back (nowhere to go back to)
                    case -1: // not yet selected
                        break;

                    case 0: // new game
                        mainMenuScreen.resetSelection();
                        removeAll();
                        loadedInvaderGameState = new InvaderGameState(controlsScreen.getCurrentControlsConfig());
                        add(loadedInvaderGameState);
                        activeDisplayState = DisplayState.PLAYING;
                        break;

                    case 1: // high scores
                        mainMenuScreen.resetSelection();
                        removeAll();
                        add(highScoreScreen);
                        activeDisplayState = DisplayState.HIGH_SCORES;
                        highScoreScreen.loadFromFile();
                        break;

                    case 2: // settings
                        mainMenuScreen.resetSelection();
                        removeAll();
                        add(settingsScreen);
                        activeDisplayState = DisplayState.SETTINGS;
                        break;

                    case 3: // quit
                        quitFlag = true;
                        break;

                    default:
                        break;
                }
                break;

            case PLAYING:

                // ensure the current screen has focus in order for keylistener to work
                if (!loadedInvaderGameState.hasFocus()) {
                    loadedInvaderGameState.requestFocusInWindow();
                }

                // update game state
                loadedInvaderGameState.update();

                // if game state signals a pause, handle it
                if (loadedInvaderGameState.pauseFlag) {
                    loadedInvaderGameState.resetFlags();
                    activeDisplayState = DisplayState.PAUSE;
                    removeAll();
                    add(pauseScreen);
                    break;
                }

                // if game state signals game over, handle it
                if (loadedInvaderGameState.gameOverFlag) {
                    loadedInvaderGameState.resetFlags();
                    activeDisplayState = DisplayState.GAME_OVER;
                    removeAll();
                    add(gameOverScreen);
                    gameOverScreen.loadFromFile();
                    break;
                }
                break;

            case PAUSE:

                // ensure the current screen has focus in order for keylistener to work
                if (!pauseScreen.hasFocus()) {
                    pauseScreen.requestFocusInWindow();
                }

                // decide what to do if user selected a option in the menu
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

                    case 1: // quit to main menu
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

            case HIGH_SCORES:

                // ensure the current screen has focus in order for keylistener to work
                if (!highScoreScreen.hasFocus()) {
                    highScoreScreen.requestFocusInWindow();
                }

                // decide what to do if user selected a option in the menu
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

                // ensure the current screen has focus in order for keylistener to work
                if (!settingsScreen.hasFocus()) {
                    settingsScreen.requestFocusInWindow();
                }

                // decide what to do if user selected a option in the menu
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
                        removeAll();
                        add(controlsScreen);
                        activeDisplayState = DisplayState.CONTROLS;
                        break;

                    case 2: // back
                        settingsScreen.selectOptionToGoBack();
                        break;

                    default:
                        break;
                }
                break;

            case CONTROLS:

                // ensure the current screen has focus in order for keylistener to work
                if (!controlsScreen.hasFocus()) {
                    controlsScreen.requestFocusInWindow();
                }

                // decide what to do if user selected a option in the menu
                switch (controlsScreen.selectedOption) {
                    case -2: // back (to setting screen)
                        controlsScreen.resetSelection();
                        controlsScreen.resetHiglight();
                        removeAll();
                        add(settingsScreen);
                        activeDisplayState = DisplayState.SETTINGS;
                        break;

                    case -1: // not yet selected
                        break;

                    case 0: // edit corresponding control (handled inside controlScreen's class)
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        break;

                    case 6: // reset to defaults
                        controlsScreen.resetSelection();
                        controlsScreen.setDefaultKeys();
                        break;

                    case 7: // back
                        controlsScreen.selectOptionToGoBack();

                    default:
                        break;
                }
                break;

            case SET_RESOLUTION:

                // ensure the current screen has focus in order for keylistener to work
                if (!resolutionScreen.hasFocus()) {
                    resolutionScreen.requestFocusInWindow();
                }

                // decide what to do if user selected a option in the menu
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

                // ensure the current screen has focus in order for keylistener to work
                if (!gameOverScreen.hasFocus()) {
                    gameOverScreen.requestFocusInWindow();
                }

                // update high score screen with the game's score
                if (loadedInvaderGameState != null) {
                    int score = loadedInvaderGameState.score.getScore();
                    gameOverScreen.setLastGameScore(score);
                    loadedInvaderGameState = null;
                }

                // decide what to do if user selected a option in the menu
                switch (gameOverScreen.selectedOption) {
                    case -2: // back (to main menu)
                        gameOverScreen.resetSelection();
                        gameOverScreen.resetHiglight();
                        removeAll();
                        add(mainMenuScreen);
                        activeDisplayState = DisplayState.MAIN_MENU;
                        break;

                    case -1: // not yet selected
                        // do nothing
                        break;

                    case 0: // rename
                        // do nothing, handled inside gameOverScreen class
                        break;

                    case 1: // back to main menu
                        gameOverScreen.selectOptionToGoBack();
                        break;

                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }

    public void draw(Graphics2D g2) {

        // draw black background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        // draw starfield
        starfield.draw(g2);

        // draw active screen
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
            case HIGH_SCORES:
                highScoreScreen.draw(g2);
                break;
            case SETTINGS:
                settingsScreen.draw(g2);
                break;
            case CONTROLS:
                controlsScreen.draw(g2);
                break;
            case SET_RESOLUTION:
                resolutionScreen.draw(g2);
                break;
            case GAME_OVER:
                gameOverScreen.draw(g2);
                break;

            default:
                break;
        }

    }

    public boolean isReadyToQuit() {
        return quitFlag;
    }

}