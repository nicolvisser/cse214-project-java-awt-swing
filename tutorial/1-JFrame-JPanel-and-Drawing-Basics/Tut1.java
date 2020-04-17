import java.awt.*;
import javax.swing.*;

public class Tut1 {

    public static void main(String[] args) {

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

        Graphics g = panel.getGraphics();
        Graphics g2 = (Graphics2D) g;

        g2.drawString("Hello World!", 50, 50);
        g2.drawString("How are you?", 100, 100);

        g2.setColor(Color.BLUE);
        g2.drawRect(200, 200, 50, 30);

        g2.setColor(Color.PINK);
        g2.fillOval(200, 300, 70, 70);

        g2.dispose();

    }

}