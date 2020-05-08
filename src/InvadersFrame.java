import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class InvadersFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // to store actual game frame size determined during runtime
    public static int width;
    public static int height;

    // to target a specified frame rate
    private static final int TARGET_FPS = 60;
    private static final int TARGET_TIME_PER_FRAME = 1000000000 / TARGET_FPS; // in nano seconds

    // to keep track of actual frames per second and to display it
    private int fpsCounter = 0;
    private int fps = 60;

    // to store bufferStrategy from which we can get the graphics object during game
    // loop
    private BufferStrategy bufferStrategy;

    // the JPanel child that holds more of current game logic
    private InvadersPanel panel;

    // to control the game loop
    private boolean running = true;

    /**
     * Constructor to initialise a frame where invaders game will run in
     * 
     * @param fullscreen true to try and run game in full screen executive mode
     */
    public InvadersFrame(boolean fullscreen) {
        super("Invaders");
        if (fullscreen) {
            initFullScreenMode();
        } else {
            initWindowedMode();
        }
    }

    // Resources used to build this game loop:
    // 1 -
    // https://docs.oracle.com/javase/tutorial/extra/fullscreen/exclusivemode.html
    // 2 -
    // https://www.oreilly.com/library/view/killer-game-programming/0596007302/ch04.html
    private void initFullScreenMode() {
        // set some properties of the frame:
        setUndecorated(true);
        setResizable(false);
        setIgnoreRepaint(true); // for active rendering (see
                                // https://docs.oracle.com/javase/tutorial/extra/fullscreen/rendering.html)

        // check if default monitor supports full screen mode, else quit with message
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (!gd.isFullScreenSupported()) {
            System.out.println("Full-screen exclusive mode not supported");
            System.exit(0);
        }

        // make this frame fullscreen on default monitor
        gd.setFullScreenWindow(this);

        // now get store and print the size of the frame
        width = getBounds().width;
        height = getBounds().height;

        // also store size in GlobalSettings class in public static variable so that
        // other classes can have easy access to it without having to pass it as
        // argument via constructors to each class that needs it
        GlobalSettings.setViewSize(width, height);
        GlobalSettings.isFullscreen = true;

        // set a double buffer strategy
        try {
            createBufferStrategy(2);
        } catch (Exception e) {
            System.out.println("Could not create double buffer strategy");
            System.exit(0);
        }

        // create a new game panel and add to the frame
        panel = new InvadersPanel(width, height);
        add(panel);
    }

    private void initWindowedMode() {
        // Small Issue: (at least on MacOS). Title bar draws over drawing area. This is
        // because we are not using swing's built in paint method but our own. Our own
        // method is faster and better suited for active rendering but can not yet
        // handle all window related stuff such as window clipping areas etc.
        // This is not a major issue, and might not be addressed by project submission
        // date.

        // set some properties of the frame:
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setIgnoreRepaint(true);
        setPreferredSize(
                new Dimension(GlobalSettings.DEFAULT_WINDOWED_MODE_WIDTH, GlobalSettings.DEFAULT_WINDOWED_MODE_HEIGHT));
        setLayout(null);

        // store and print the size of the frame
        width = GlobalSettings.DEFAULT_WINDOWED_MODE_WIDTH;
        height = GlobalSettings.DEFAULT_WINDOWED_MODE_HEIGHT;
        System.out.println("Windowed: " + width + " x " + height);

        // create a new game panel and add to the frame
        panel = new InvadersPanel(width, height);
        add(panel);

        // pack the frame with its new contents
        pack();

        // set a double buffer strategy
        try {
            createBufferStrategy(2);
        } catch (Exception e) {
            System.out.println("Could not create double buffer strategy");
            System.exit(0);
        }

        setVisible(true);
    }

    public void changeWindowSize(int width, int height) {
        Dimension newSize = new Dimension(width, height);
        setPreferredSize(newSize);
        panel.setPreferredSize(newSize);
        pack();
    }

    // Tutorial at
    // https://docs.oracle.com/javase/tutorial/extra/fullscreen/rendering.html
    // Also some credit to http://www.java-gaming.org/index.php?topic=24220.0
    public void run() {

        // store buffer strategy to be used in loop
        bufferStrategy = getBufferStrategy();

        long timer = 0;

        while (running) {

            long startTime = System.nanoTime();

            // UPDATE GAME
            gameUpdate(TARGET_TIME_PER_FRAME / 1000000); // pass down approx time per frame for updates that might be
                                                         // related to time - not very accurate, just an approximation

            // DRAW FRAME
            gameDraw();
            fpsCounter++;

            long afterWorkTime = System.nanoTime();

            long remainingTime = TARGET_TIME_PER_FRAME - (afterWorkTime - startTime);

            // If it took less time to update the game and draw than target time per frame,
            // then rest for the remaining duration
            if (remainingTime > 0) {
                try {
                    Thread.sleep(remainingTime / 1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // update timer
            long endTime = System.nanoTime();
            timer += (endTime - startTime);

            // if a second has passes, update fps counter and reset timer
            if (timer > 1000000000) {
                fps = fpsCounter;
                fpsCounter = 0;
                timer = 0;
            }
        }
    }

    public void stop() {
        running = false; // to quit game loop

        // take monitor out of fullscreen mode
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(null);
    }

    public void gameUpdate(int dt) {

        if (panel.isReadyToQuit()) {
            // if the quit flag was raised inside the panel class, then stop the game loop
            // in this class
            this.stop();

        } else {
            // update state of panel
            panel.update(dt);

        }
    }

    public void gameDraw() {
        // get graphics object of the frame and then cast it to Graphics2D object which
        // has more applicable methods for this game's requirements
        Graphics2D g2 = (Graphics2D) bufferStrategy.getDrawGraphics();

        // draw contents of panel in the buffer
        panel.draw(g2);

        // show the contents drawn in the previous frame (double buffering)
        bufferStrategy.show();

        // draw a green rectangle for the frame's boundary as well as the most recent
        // FPS count
        g2.setColor(Color.GREEN);
        g2.drawRect(0, 0, width, height);
        Utils.drawLeftAlignedText(g2, 10, height - 10, "FPS: " + fps);

        // dispose the graphicsobject (which is a buffer type object)
        g2.dispose();
    }

}