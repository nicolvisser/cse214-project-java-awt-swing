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

public class InvadersPanel extends JPanel implements Drawable {

    private static final long serialVersionUID = 1L;

    private boolean quitFlag = false;

    // stores size of frame passed down via constructor
    private final int width;
    private final int height;

    // define different states panel can be in, which is used to determine what
    // component to display on panel at what time
    public enum DisplayState {
        MAIN_MENU, PLAYING, TUTORIAL, PAUSE, HIGH_SCORES, SETTINGS, CONTROLS_P1, CONTROLS_P2, GAME_OVER, QUIT;
    }

    // stores current state the panel is in
    public DisplayState activeDisplayState;
    public DisplayComponent activeDisplayComponent;

    // define the text for screens that make use of the default MenuScreen object
    private static final String[] mainMenuScreenOptions = { "Quick Tutorial", "Single Player", "Two Player",
            "High Scores", "Settings", "Quit Game" };
    private static final String[] pauseScreenOptions = { "Resume Game", "Restart Game", "Quit To Main Menu" };
    private static final String[] settingsScreenOptions = { "Customize Controls P1", "Customize Controls P2", "Back" };

    // declare different components that will at some point be drawn on panel
    private Starfield starfield;
    private InvaderGameState loadedInvaderGameState;
    private Tutorial tutorial;
    private MenuScreen mainMenuScreen;
    private MenuScreen pauseScreen;
    private MenuScreen settingsScreen;
    private HighScoreScreen highScoreScreen;
    private GameOverScreen gameOverScreen;
    private ControlsScreen controlsScreenPlayer1;
    private ControlsScreen controlsScreenPlayer2;

    public InvadersPanel(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        setIgnoreRepaint(true);
        setKeyBindings();

        GameAudio.loopMenuMusic();

        starfield = new Starfield(width, height); // background

        mainMenuScreen = new MenuScreen(width, height, "Invaders", "Main Menu", mainMenuScreenOptions);
        pauseScreen = new MenuScreen(width, height, "Paused", pauseScreenOptions);
        settingsScreen = new MenuScreen(width, height, "Settings", settingsScreenOptions);
        highScoreScreen = new HighScoreScreen(width, height);
        gameOverScreen = new GameOverScreen(width, height);
        controlsScreenPlayer1 = new ControlsScreen(width, height, "resources/keysP1.txt",
                GlobalSettings.DEFAULT_KEYCODES_P1);
        controlsScreenPlayer2 = new ControlsScreen(width, height, "resources/keysP2.txt",
                GlobalSettings.DEFAULT_KEYCODES_P2);

        goToNewState(DisplayState.MAIN_MENU);
    }

    // returns corresponding JComponent associated with the display state
    private DisplayComponent lookupComponent(DisplayState state) {
        switch (state) {
            case MAIN_MENU:
                return mainMenuScreen;
            case PLAYING:
                return loadedInvaderGameState;
            case TUTORIAL:
                return tutorial;
            case PAUSE:
                return pauseScreen;
            case HIGH_SCORES:
                return highScoreScreen;
            case SETTINGS:
                return settingsScreen;
            case CONTROLS_P1:
                return controlsScreenPlayer1;
            case CONTROLS_P2:
                return controlsScreenPlayer2;
            case GAME_OVER:
                return gameOverScreen;
            default:
                return null;
        }
    }

    private void goToNewState(DisplayState newState) {
        removeAll();
        activeDisplayComponent = lookupComponent(newState);
        activeDisplayState = newState;
        add(activeDisplayComponent);
    }

    private void setKeyBindings() {
        // Using key binding for q key to quit.
        // The keybinding works whether panel has focus or not.
        // Special thanks to https://www.youtube.com/watch?v=LNizNHaRV84&t=1484s
        // Also see https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html
        //
        // Considered using keybinding for other classes as well. It would have worked
        // very nice for creating custom controls. However it seems that key bindings
        // can't recognize a modifier key (shift, alt, control) press on its own.

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        KeyStroke quitKeyPress = KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false);
        inputMap.put(quitKeyPress, "quitKeyPress");
        actionMap.put("quitKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                // don't allow quit key in game over screen or control editing screen, since
                // user may use the key Q here
                if (!(gameOverScreen.hasFocus() || controlsScreenPlayer1.hasFocus()
                        || controlsScreenPlayer2.hasFocus())) {
                    quitFlag = true;
                }
            }
        });
    }

    public void update(int dt) {

        // update starfield based on whether game is playing or menu is showing
        if (activeDisplayState == DisplayState.PLAYING)
            starfield.update(dt, loadedInvaderGameState.getVelocityForBackground());
        else
            starfield.update(dt, new Vector2D(-width / 50, -height / 50));

        // if active screen is not focussed for keyboard input, request focus
        if (!activeDisplayComponent.hasFocus()) {
            activeDisplayComponent.requestFocus();
        }

        // update active screen
        switch (activeDisplayState) {
            case MAIN_MENU:
                // decide what to do if user selected a option in the menu
                switch (mainMenuScreen.selectedOption) {
                    case -2: // back (nowhere to go back to)
                    case -1: // not yet selected
                        break;

                    case 0: // tutorial
                        mainMenuScreen.resetSelection();
                        tutorial = new Tutorial(controlsScreenPlayer1.getCurrentControlsConfig(),
                                controlsScreenPlayer1.getCurrentControlsDescriptions());
                        goToNewState(DisplayState.TUTORIAL);
                        break;

                    case 1: // single player
                        mainMenuScreen.resetSelection();
                        loadedInvaderGameState = new InvaderGameState(controlsScreenPlayer1.getCurrentControlsConfig());
                        GameAudio.fadeOutMusicThenStartGameMusic();
                        goToNewState(DisplayState.PLAYING);
                        break;

                    case 2: // two player
                        mainMenuScreen.resetSelection();
                        loadedInvaderGameState = new InvaderGameState(controlsScreenPlayer1.getCurrentControlsConfig(),
                                controlsScreenPlayer2.getCurrentControlsConfig());
                        GameAudio.fadeOutMusicThenStartGameMusic();
                        goToNewState(DisplayState.PLAYING);
                        break;

                    case 3: // high scores
                        mainMenuScreen.resetSelection();
                        highScoreScreen.loadFromFile();
                        goToNewState(DisplayState.HIGH_SCORES);
                        break;

                    case 4: // settings
                        mainMenuScreen.resetSelection();
                        goToNewState(DisplayState.SETTINGS);
                        break;

                    case 5: // quit
                        quitFlag = true;
                        break;

                    default:
                        break;
                }
                break;

            case PLAYING:

                // update game state
                loadedInvaderGameState.update(dt);

                // if game state signals a pause, handle it
                if (loadedInvaderGameState.pauseFlag) {
                    loadedInvaderGameState.resetFlags();
                    GameAudio.pauseBackgroundMusic();
                    goToNewState(DisplayState.PAUSE);
                    break;
                }

                // if game state signals game over, handle it
                if (loadedInvaderGameState.gameOverFlag) {
                    loadedInvaderGameState.resetFlags();
                    gameOverScreen.loadFromFile();
                    GameAudio.fadeOutMusicThenStartMenuMusic();
                    goToNewState(DisplayState.GAME_OVER);
                    break;
                }
                break;

            case TUTORIAL:

                // update tutorial state
                tutorial.update(dt);

                // if tutorial signals a exit, handle it
                if (tutorial.exitFlag) {
                    tutorial = null;
                    goToNewState(DisplayState.MAIN_MENU);
                    break;
                }

                break;

            case PAUSE:

                // decide what to do if user selected a option in the menu
                switch (pauseScreen.selectedOption) {
                    case -2: // back (to playing game)
                        pauseScreen.resetSelection();
                        GameAudio.resumeBackgroundMusic();
                        goToNewState(DisplayState.PLAYING);
                    case -1: // not yet selected
                        break;

                    case 0: // resume game
                        pauseScreen.selectOptionToGoBack();
                        break;

                    case 1: // restart game
                        pauseScreen.resetSelection();
                        if (loadedInvaderGameState.getNumberOfPlayers() == 2) {
                            loadedInvaderGameState = new InvaderGameState(
                                    controlsScreenPlayer1.getCurrentControlsConfig(),
                                    controlsScreenPlayer2.getCurrentControlsConfig());
                        } else {
                            loadedInvaderGameState = new InvaderGameState(
                                    controlsScreenPlayer1.getCurrentControlsConfig());
                        }
                        GameAudio.fadeOutMusicThenStartGameMusic();
                        goToNewState(DisplayState.PLAYING);
                        break;

                    case 2: // quit to main menu
                        pauseScreen.resetSelection();
                        pauseScreen.resetHiglight();
                        GameAudio.fadeOutMusicThenStartMenuMusic();
                        goToNewState(DisplayState.MAIN_MENU);
                        break;

                    default:
                        break;
                }
                break;

            case HIGH_SCORES:

                // decide what to do if user selected a option in the menu
                switch (highScoreScreen.selectedOption) {
                    case -2: // back (to main menu)
                        highScoreScreen.resetSelection();
                        highScoreScreen.resetHiglight();
                        goToNewState(DisplayState.MAIN_MENU);
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

                // decide what to do if user selected a option in the menu
                switch (settingsScreen.selectedOption) {
                    case -2: // back (to main menu)
                        settingsScreen.resetSelection();
                        settingsScreen.resetHiglight();
                        goToNewState(DisplayState.MAIN_MENU);
                        break;

                    case -1: // not yet selected
                        break;

                    case 0: // controls for player 1
                        settingsScreen.resetSelection();
                        goToNewState(DisplayState.CONTROLS_P1);
                        break;

                    case 1: // controls for player 2
                        settingsScreen.resetSelection();
                        goToNewState(DisplayState.CONTROLS_P2);
                        break;

                    case 2: // back
                        settingsScreen.selectOptionToGoBack();
                        break;

                    default:
                        break;
                }
                break;

            case CONTROLS_P1:

                // decide what to do if user selected a option in the menu
                switch (controlsScreenPlayer1.selectedOption) {
                    case -2: // back (to setting screen)
                        controlsScreenPlayer1.resetSelection();
                        controlsScreenPlayer1.resetHiglight();
                        goToNewState(DisplayState.SETTINGS);
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
                        controlsScreenPlayer1.resetSelection();
                        controlsScreenPlayer1.setDefaultKeys();
                        break;

                    case 7: // back
                        controlsScreenPlayer1.selectOptionToGoBack();

                    default:
                        break;
                }
                break;

            case CONTROLS_P2:

                // decide what to do if user selected a option in the menu
                switch (controlsScreenPlayer2.selectedOption) {
                    case -2: // back (to setting screen)
                        controlsScreenPlayer2.resetSelection();
                        controlsScreenPlayer2.resetHiglight();
                        goToNewState(DisplayState.SETTINGS);
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
                        controlsScreenPlayer2.resetSelection();
                        controlsScreenPlayer2.setDefaultKeys();
                        break;

                    case 7: // back
                        controlsScreenPlayer2.selectOptionToGoBack();

                    default:
                        break;
                }
                break;

            case GAME_OVER:

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
                        goToNewState(DisplayState.MAIN_MENU);
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

    @Override
    public void draw(Graphics2D g2) {

        // scale text based on screen resolution / frame size
        Utils.scaleFont(g2, GlobalSettings.vmin / 1000f);

        // draw black background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        // draw starfield
        starfield.draw(g2);

        // call active DisplayComponent's draw method
        activeDisplayComponent.draw(g2);
    }

    public boolean isReadyToQuit() {
        return quitFlag;
    }

}