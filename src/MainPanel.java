import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private int fps = 60;

    long lastNanoTime;

    JLabel fpsLabel;

    DefaultCritter circular, rectangular;

    public MainPanel() {
        setLayout(null);
        setBackground(Color.GRAY);
        setIgnoreRepaint(true);

        lastNanoTime = System.nanoTime();

        fpsLabel = new JLabel();
        fpsLabel.setBounds(0, 0, 100, 10);
        fpsLabel.setText("FPS: 60");
        add(fpsLabel);

        circular = new DefaultCritter(DefaultCritter.BoundingShape.ELLIPSE, 0, 0, 100, 100);
        circular.velocity = new Vector2D(2000, 2000);
        add(circular);

        rectangular = new DefaultCritter(DefaultCritter.BoundingShape.RECTANGLE, 500, 500, 100, 50);
        rectangular.velocity = new Vector2D(-10000, 1000);
        add(rectangular);

    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 300, 200);
        circular.draw(g);
        rectangular.draw(g);
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