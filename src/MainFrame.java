import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;




public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public static int numMissiles = 0;

    private int TARGET_FPS = 60;
    private int TARGET_TIME_PER_FRAME = 1000000000 / TARGET_FPS;
    private int fpsCounter = 0;
    private int fps = 60;

    private boolean running = true;

    private MainPanel panel;
    private BufferStrategy bufferStrategy;

    public MainFrame() {
        super("Cosmic Conquistadors");

        getContentPane().setPreferredSize(new Dimension(800, 800));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIgnoreRepaint(true);
        setVisible(true);
    }

    public void addGamePanel() {
        panel = new MainPanel();
        add(panel);
    }

    public void printSize() {
        System.out.println("Width: " + getSize().width);
        System.out.println("Height: " + getSize().height);
    }

    public void draw(Graphics g) {
        panel.draw(g);
        g.drawString("FPS: " + fps, 10, 10);
        g.drawString("Num Missiles: " + numMissiles, 10, 20);
    }

    public void update() {
        panel.update();
    }

    // Tutorial at
    // https://docs.oracle.com/javase/tutorial/extra/fullscreen/rendering.html
    // Also some credit to http://www.java-gaming.org/index.php?topic=24220.0
    public void gameLoop() {
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();

        long timer = 0;

        while (running) {

            long startTime = System.nanoTime();

            // UPDATE GAME
            update();

            // DRAW FRAME
            Graphics g = bufferStrategy.getDrawGraphics();
            draw(g);
            g.dispose();
            bufferStrategy.show();
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

            timer += endTime - startTime;

            if (timer > 1000000000) {
                fps = fpsCounter;
                fpsCounter = 0;
                timer = 0;
            }

        }

    }

    

    public static void main(String[] args) {

        // Tutorial at
        // https://docs.oracle.com/javase/tutorial/extra/fullscreen/exclusivemode.html
        // http://www.java2s.com/Code/Java/2D-Graphics-GUI/GettheGraphicsEnvironmentandGraphicsDevice.htm
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();

        MainFrame frame = new MainFrame();

        if (defaultScreen.isFullScreenSupported()) {
            try {
                // RUN IN FULL SCREEN MODE:
                defaultScreen.setFullScreenWindow(frame);

                DefaultCritter.setCanvasSize(frame.getSize().width, frame.getSize().height);

                frame.addGamePanel();

                frame.gameLoop();

            } catch (Exception e) {
                // COULD NOT RUN FULL SCREEN
                e.printStackTrace();

            } finally {
                defaultScreen.setFullScreenWindow(null);
            }
        }

    }

}