import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class Tut2 {

    static int x = 0;
    static int y = 0;

    public static void main(String[] args) throws Exception {

        JFrame frame = new JFrame("My Frame");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 500));

        frame.add(panel);
        frame.pack();

        frame.setIgnoreRepaint(true);
        panel.setIgnoreRepaint(true);

        while (true) {

            // update game parameters
            update();

            // draw current state to graphics object
            Graphics2D g2 = (Graphics2D) frame.getGraphics();
            draw(g2);
            g2.dispose();

            Thread.sleep(50);
        }

    }

    static void update() {
        x += 5;
        y += 5;
    }

    static void draw(Graphics2D g2) {

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 500, 500);

        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, 20, 20);

    }

}