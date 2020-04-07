import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    long lastNanoTime;

    JLabel fpsLabel;

    DefaultCritter circular, rectangular;

    public MainPanel() {
        setLayout(null);
        setIgnoreRepaint(true);

        lastNanoTime = System.nanoTime();

        circular = new DefaultCritter(DefaultCritter.BoundingShape.ELLIPSE, 0, 0, 100, 100);
        circular.velocity = new Vector2D(1, 1);

        rectangular = new DefaultCritter(DefaultCritter.BoundingShape.RECTANGLE, 500, 500, 100, 50);
        rectangular.velocity = new Vector2D(-1, 1);

    }

    public void draw(Graphics g) {

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getSize().width, getSize().height);

        circular.draw(g);
        rectangular.draw(g);
    }

    public void update() {
        circular.update();
        rectangular.update();
    }

}