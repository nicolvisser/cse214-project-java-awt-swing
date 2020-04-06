import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.font.*;

public class MainPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private int fps = 60;

    long lastNanoTime;

    JLabel fpsLabel;

    DefaultCritter circular, rectangular;

    public MainPanel() {
        setLayout(null);
        setIgnoreRepaint(true);

        lastNanoTime = System.nanoTime();

        circular = new DefaultCritter(DefaultCritter.BoundingShape.ELLIPSE, 0, 0, 100, 100);
        circular.velocity = new Vector2D(2000, 2000);
        add(circular);

        rectangular = new DefaultCritter(DefaultCritter.BoundingShape.RECTANGLE, 500, 500, 100, 50);
        rectangular.velocity = new Vector2D(-10000, 1000);
        add(rectangular);

    }

    public void draw(Graphics g) {

        g.setColor(Color.GRAY);
        g.fillRect(5, 5, getSize().width - 10, getSize().height - 10);

        circular.draw(g);
        rectangular.draw(g);

        g.drawString("FPS: ", 10, 10);
    }

    private void render(double dt) {
        long currentNanoTime = System.nanoTime();
        double fps_actual = 1e9 / (currentNanoTime - lastNanoTime);
        fpsLabel.setText(String.format("FPS: %4.1f", fps_actual));
        lastNanoTime = currentNanoTime;

        circular.render(dt);
        rectangular.render(dt);
        repaint();
    }

}