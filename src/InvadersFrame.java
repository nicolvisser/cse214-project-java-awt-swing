import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class InvadersFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final boolean DEBUG = false;

    private static final Dimension DEFAULT_FRAME_SIZE_IF_NOT_FULLSCREEN = new Dimension(800, 600);

    public static int width;
    public static int height;

    private int TARGET_FPS = 60;
    private int TARGET_TIME_PER_FRAME = 1000000000 / TARGET_FPS;
    private int fpsCounter = 0;
    private int fps = 60;

    private boolean running = true;
    private BufferStrategy bufferStrategy;

    InvadersPanel panel;

    public InvadersFrame(boolean fullscreen) {
        super("Invaders");
        if (fullscreen) {
            initFullScreenMode();
        } else {
            initWindowedMode();
        }
    }

    // https://docs.oracle.com/javase/tutorial/extra/fullscreen/exclusivemode.html
    // https://www.oreilly.com/library/view/killer-game-programming/0596007302/ch04.html
    private void initFullScreenMode() {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        setUndecorated(true);
        setIgnoreRepaint(true);
        setResizable(false);

        if (!gd.isFullScreenSupported()) {
            System.out.println("Full-screen exclusive mode not supported");
            System.exit(0);
        }
        gd.setFullScreenWindow(this);

        width = getBounds().width;
        height = getBounds().height;
        System.out.println("Fullscreen: " + width + " x " + height);

        // TODO Configure Display Modes

        // BUFFER STRATEGY
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    createBufferStrategy(2);
                }
            });
        } catch (Exception e) {
            System.out.println("Error while creating buffer strategy");
            System.exit(0);
        }

        try { // sleep to give time for buffer strategy to be done
            Thread.sleep(500); // 0.5 sec
        } catch (InterruptedException ex) {
        }

        panel = new InvadersPanel(width, height);
        add(panel);
    }

    private void initWindowedMode() {
        // TODO Issue: (at least on MacOS) Title bar draws over drawing area. This is
        // because we are not using swing's built in paint method but our own. Our own
        // method is faster and better suited for active rendereing but can not yet
        // handle all window related stuff such as window clipping areas etc.

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setIgnoreRepaint(true);
        setPreferredSize(DEFAULT_FRAME_SIZE_IF_NOT_FULLSCREEN);
        setLayout(null);

        width = (int) DEFAULT_FRAME_SIZE_IF_NOT_FULLSCREEN.getWidth();
        height = (int) DEFAULT_FRAME_SIZE_IF_NOT_FULLSCREEN.getHeight();
        System.out.println("Windowed: " + width + " x " + height);

        panel = new InvadersPanel(width, height);
        add(panel);
        pack();

        // BUFFER STRATEGY
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    createBufferStrategy(2);
                }
            });
        } catch (Exception e) {
            System.out.println("Error while creating buffer strategy");
            System.exit(0);
        }

        try { // sleep to give time for buffer strategy to be done
            Thread.sleep(500); // 0.5 sec
        } catch (InterruptedException ex) {
        }

        setVisible(true);
    }

    // Tutorial at
    // https://docs.oracle.com/javase/tutorial/extra/fullscreen/rendering.html
    // Also some credit to http://www.java-gaming.org/index.php?topic=24220.0
    public void run() {

        bufferStrategy = getBufferStrategy();

        long timer = 0;

        while (running) {

            long startTime = System.nanoTime();

            // UPDATE GAME
            gameUpdate();

            // DRAW FRAME
            gameDraw();
            fpsCounter++;

            long afterWorkTime = System.nanoTime();

            long remainingTime = TARGET_TIME_PER_FRAME - (afterWorkTime - startTime);

            if (remainingTime > 0) {
                try {
                    Thread.sleep(remainingTime / 1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            long endTime = System.nanoTime();

            timer += (endTime - startTime);

            if (timer > 1000000000) {
                fps = fpsCounter;
                fpsCounter = 0;
                timer = 0;
            }
        }
    }

    public void gameUpdate() {
        panel.update();

    }

    public void gameDraw() {
        Graphics2D g2 = (Graphics2D) bufferStrategy.getDrawGraphics();

        // draw contents of panel
        panel.draw(g2);

        // draw green boundary and overlay
        g2.setColor(Color.GREEN);
        g2.drawString("FPS: " + fps, 10, 20);
        g2.drawRect(0, 0, width, height);

        g2.dispose();
        bufferStrategy.show();
    }

    public static void main(String[] args) {

        InvadersFrame game = new InvadersFrame(false);

        game.run();

    }

}