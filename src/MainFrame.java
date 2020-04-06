import java.awt.*;
import java.awt.image.BufferStrategy;
//import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    boolean done;

    private static int WIDTH = 800;
    private static int HEIGHT = 800;

    MainPanel panel;
    BufferStrategy bufferStrategy;

    public MainFrame() {
        super("Tutorial");
        getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setIgnoreRepaint(true);
        panel = new MainPanel();
        add(panel);
    }

    public void draw(Graphics g) {
        panel.draw(g);
    }

    // https://docs.oracle.com/javase/tutorial/extra/fullscreen/rendering.html
    public void myRenderingLoop() {
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
        while (true) {
            Graphics g = bufferStrategy.getDrawGraphics();
            draw(g);
            g.dispose();
            bufferStrategy.show();
        }
    }

    public static void main(String[] args) {

        // https://docs.oracle.com/javase/tutorial/extra/fullscreen/exclusivemode.html
        // http://www.java2s.com/Code/Java/2D-Graphics-GUI/GettheGraphicsEnvironmentandGraphicsDevice.htm
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();

        // https://docs.oracle.com/javase/tutorial/extra/fullscreen/displaymode.html
        // <--- READ MORE: can also change display mode... e.g to lower resolution while
        // in full screen for better performance
        DisplayMode displayMode = defaultScreen.getDisplayMode();
        int displayWidth = displayMode.getWidth();
        int displayHeight = displayMode.getHeight();
        int refreshRate = displayMode.getRefreshRate();

        int targetFPS = Math.min(refreshRate, 60);

        MainFrame frame = new MainFrame();

        if (defaultScreen.isFullScreenSupported()) {
            try {
                defaultScreen.setFullScreenWindow(frame);

                frame.myRenderingLoop();

            } catch (Exception e) {
                defaultScreen.setFullScreenWindow(null);

                frame.myRenderingLoop();

            } finally {
                defaultScreen.setFullScreenWindow(null);
            }
        }

    }

}