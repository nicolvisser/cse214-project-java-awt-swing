import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Line2D;
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

    private static final double DEFAULT_ORIENTATION = -Math.PI / 2; // upwards

    enum BoundingShape {
        RECTANGLE, ELLIPSE
    }

    private final BoundingShape boundingShape;
    public double width, height;
    public Vector2D position, velocity, acceleration;
    public double orientation, angularVelocity, angularAcceleration;

    public DefaultCritter() {
        this(BoundingShape.RECTANGLE, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);

    }

    public DefaultCritter(BoundingShape boundingShape, double x, double y, double width, double height) {
        this(boundingShape, x, y, width, height, DEFAULT_ORIENTATION);
    }

    public DefaultCritter(BoundingShape boundingShape, double x, double y, double width, double height,
            double orientation) {

        this.boundingShape = boundingShape;

        this.width = width;
        this.height = height;

        position = new Vector2D(x, y);
        velocity = Vector2D.zero();
        acceleration = Vector2D.zero();

        this.orientation = orientation;
        angularVelocity = 0;
        angularAcceleration = 0;

        setIgnoreRepaint(true);
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

    // =============== METHODS ASSOCIATED WITH POSITION =============== >>>

    public Vector2D positionRelativeTo(DefaultCritter other) {
        return this.position.subtract(other.position);
    }

    public double distanceTo(DefaultCritter other) {
        return positionRelativeTo(other).magnitude();
    }

    public void updateTranslation() {
        // uses timestep of 1
        // uses final velocity as proxy for average velocity

        velocity = velocity.add(acceleration);
        position = position.add(velocity).add(acceleration.scale(0.5));
    }

    // =============== METHODS ASSOCIATED WITH ORIENTATION =============== >>>

    public Double getOrientationInDegrees() {
        return orientation / Math.PI * 180;
    }

    public Vector2D lookVector() {
        return new Vector2D(Math.cos(orientation), Math.sin(orientation));
    }

    public void lookAt(DefaultCritter other) {
        orientation = this.positionRelativeTo(other).getPolarAngle();
    }

    public void lookAt(double x, double y) {
        Vector2D relativeVector = new Vector2D(x - position.x, y - position.y);
        orientation = relativeVector.getPolarAngle();
    }

    public void updateRotation() {
        // uses timestep of 1
        // uses final velocity as proxy for average velocity

        angularVelocity += angularAcceleration;
        orientation += angularVelocity + 0.5 * angularAcceleration;
    }

    // ===== METHODS ASSOCIATED WITH DRAWABLE AND UPDATABLE INTERFACES ===== >>>

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.draw(getBoundingShape());

        Vector2D lineStart = new Vector2D(position.x + width / 2, position.y + height / 2); // center of object
        Vector2D lineEnd = lineStart.add(lookVector().scale(height / 2));
        Shape line = new Line2D.Double(lineStart.toPoint(), lineEnd.toPoint());
        g2.draw(line);
    }

    @Override
    public void update() {
        updateTranslation();
        updateRotation();
    }

}