import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public ArrayList<Drawable> drawableChildren = new ArrayList<>();
    public ArrayList<Updatable> updatableChildren = new ArrayList<>();

    public MainPanel() {
        setLayout(null);
        setIgnoreRepaint(true);

        DefaultCritter circ = new DefaultCritter(DefaultCritter.BoundingShape.ELLIPSE, 0, 0, 100, 100);
        circ.velocity = new Vector2D(1, 1);
        add(circ);

        DefaultCritter rect = new DefaultCritter(DefaultCritter.BoundingShape.RECTANGLE, 500, 500, 100, 50);
        rect.velocity = new Vector2D(-1, 1);
        add(rect);

        Shooter shooter = new Shooter();
        add(shooter);

    }

    @Override
    public Component add(Component comp) {
        if (comp instanceof Drawable) {
            drawableChildren.add((Drawable) comp);
        }
        if (comp instanceof Updatable) {
            updatableChildren.add((Updatable) comp);
        }
        return super.add(comp);
    }

    public void draw(Graphics g) {

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getSize().width, getSize().height);

        for (Drawable drawableChild : drawableChildren) {
            drawableChild.draw(g);
        }
    }

    public void update() {
        for (Updatable updatableChild : updatableChildren) {
            updatableChild.update();
        }
    }

}