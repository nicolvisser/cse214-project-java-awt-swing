import java.awt.*;
import java.awt.geom.*;

public class DefaultCritter implements Updatable, Drawable {

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