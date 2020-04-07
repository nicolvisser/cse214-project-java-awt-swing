import java.awt.*;
import java.awt.geom.*;
import javax.swing.JComponent;

public class DefaultCritter extends JComponent implements Updatable, Drawable {

    private static final long serialVersionUID = 1L;

    protected static int vw = 800; // viewport width
    protected static int vh = 800; // viewport width
    protected static int vmin = 800; // viewport min dimension
    protected static int vmax = 800; // viewport max dimension

    public static void setCanvasSize(int w, int h) {
        vw = w;
        vh = h;
        vmin = Math.min(w, h);
        vmax = Math.max(w, h);
    }

    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFAULT_HEIGHT = 50;

    enum BoundingShape {
        RECTANGLE, ELLIPSE
    }

    private final BoundingShape boundingShape;
    public double width, height;
    public Vector2D position, velocity, acceleration;

    public DefaultCritter() {
        this(BoundingShape.RECTANGLE, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setIgnoreRepaint(true);
    }

    public DefaultCritter(BoundingShape boundingShape, double x, double y, double width, double height) {
        this.boundingShape = boundingShape;
        this.width = width;
        this.height = height;
        position = new Vector2D(x, y);
        velocity = Vector2D.zero();
        acceleration = Vector2D.zero();
    }

    public RectangularShape getBoundingShape() {
        switch (boundingShape) {
            case RECTANGLE:
                return new Rectangle2D.Double(position.x, position.y, width, height);

            case ELLIPSE:
                return new Ellipse2D.Double(position.x, position.y, width, height);

            default:
                return null;
        }
    }

    @Override
    public void draw(Graphics g) {

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, vw, vh);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.draw(getBoundingShape());
    }

    @Override
    public void update() {
        velocity = velocity.add(acceleration);
        position = position.add(velocity);
    }

}