import java.awt.*;
import java.awt.image.BufferStrategy;
//import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private final int TARGET_FPS = 60;
    private final long TARGET_NANO_TIME = 1000000000 / TARGET_FPS;
    private final double TARGET_DELTA = TARGET_NANO_TIME / 1e9;
    private final double MAX_DELTA = 2 * TARGET_NANO_TIME / 1e9;

    private int currentFPS;

    private boolean done = false;
    private MainPanel panel;
    private BufferStrategy bufferStrategy;

    public MainFrame() {
        super("Cosmic Conquistadors");

        getContentPane().setPreferredSize(new Dimension(800, 800));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIgnoreRepaint(true);
        panel = new MainPanel();
        add(panel);
        setVisible(true);
    }

    public void printSize() {
        System.out.println("Width: " + getSize().width);
        System.out.println("Height: " + getSize().height);
    }

    public void draw(Graphics g) {
        panel.draw(g);
        g.drawString("FPS: " + currentFPS, 10, 10);
    }

    public void update(double dt) {
        panel.update(dt);
    }

    // Tutorial at
    // https://docs.oracle.com/javase/tutorial/extra/fullscreen/rendering.html
    // Also some credit to http://www.java-gaming.org/index.php?topic=24220.0
    public void myRenderingLoop() {
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();

        long lastNanoTimestamp = System.nanoTime();
        long nanoTimer = 0;
        int frameCounter = 0;

        while (!done) {

            long currentNanoTimestamp = System.nanoTime();
            long frameNanoTime = currentNanoTimestamp - lastNanoTimestamp;
            nanoTimer += frameNanoTime;
            frameCounter++;

            double delta = TARGET_DELTA;

            if (frameNanoTime < TARGET_NANO_TIME) {
                System.out.println("under");
                long sleepNanoTime = TARGET_NANO_TIME - frameNanoTime;
                try {
                    Thread.sleep(sleepNanoTime / 1000000);
                    // Thread.sleep(sleepNanoTime / 1000000, (int) (sleepNanoTime % 1000000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (frameNanoTime > TARGET_NANO_TIME) {
                System.out.println("over");
                long catchUpNanoTime = frameNanoTime - TARGET_NANO_TIME;
                delta = Math.min(MAX_DELTA, TARGET_DELTA + catchUpNanoTime / 1e9);
            }
            // TODO put max delta in here, where game should rather start to lag real time.
            // (instead of taking very large steps)

            if (nanoTimer >= 1000000000) {
                currentFPS = frameCounter;
                System.out.println("FPS:      " + currentFPS);
                nanoTimer = 0;
                frameCounter = 0;
            }

            lastNanoTimestamp = currentNanoTimestamp;

            Graphics g = bufferStrategy.getDrawGraphics();
            draw(g);
            update(delta);
            g.dispose();

            bufferStrategy.show();

        }
    }

    public static void main(String[] args) {

        // Tutorial at
        // https://docs.oracle.com/javase/tutorial/extra/fullscreen/exclusivemode.html
        // http://www.java2s.com/Code/Java/2D-Graphics-GUI/GettheGraphicsEnvironmentandGraphicsDevice.htm
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();

        // Tutorial at
        // https://docs.oracle.com/javase/tutorial/extra/fullscreen/displaymode.html
        // TODO <--- READ MORE: can also change display mode... e.g to lower resolution
        // while in full screen for better performance
        //// DisplayMode displayMode = defaultScreen.getDisplayMode();
        //// int displayWidth = displayMode.getWidth();
        //// int displayHeight = displayMode.getHeight();
        //// int refreshRate = displayMode.getRefreshRate();

        MainFrame frame = new MainFrame();

        if (defaultScreen.isFullScreenSupported()) {
            try {
                // RUN IN FULL SCREEN MODE:
                defaultScreen.setFullScreenWindow(frame);
                frame.myRenderingLoop();

            } catch (Exception e) {
                // RUN IN WINDOWED MODE:
                defaultScreen.setFullScreenWindow(null);
                frame.myRenderingLoop();

            } finally {
                defaultScreen.setFullScreenWindow(null);
            }
        }

    }

}