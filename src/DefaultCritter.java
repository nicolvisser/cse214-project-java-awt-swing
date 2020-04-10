import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import geom.Circle;
import geom.LineSegment;
import geom.Rectangle;
import geom.Shape;
import geom.Vector2D;

public class DefaultCritter extends JComponent {

    private static final long serialVersionUID = 1L;

    protected static int vw = 800; // viewport width
    protected static int vh = 800; // viewport width
    protected static int vmin = 800; // viewport min dimension
    protected static int vmax = 800; // viewport max dimension

    protected static void setCanvasSize(final int w, final int h) {
        vw = w;
        vh = h;
        vmin = Math.min(w, h);
        vmax = Math.max(w, h);
    }

    protected static Rectangle getCanvasRect() {
        return new Rectangle(vw / 2, vh / 2, vw, vh);
    }

    enum CollisionShape {
        RECTANGLE, CIRCLE
    }

    private final CollisionShape boundingShape;
    public double width, height;

    // positive x to right, positive y to bottom (as with swing frame)
    public Vector2D position, velocity, acceleration;

    // orientation correspods to bearing in radians
    // i.e. radians clockwise from north, where north is at top of screen
    public double orientation, angularVelocity, angularAcceleration;

    /**
     * Creates rectangular critter
     */
    public DefaultCritter(double x, double y, double width, double height, double orientation) {
        this(CollisionShape.RECTANGLE, x, y, width, height, orientation);
    }

    /**
     * Creates circular critter
     */
    public DefaultCritter(double x, double y, double radius, double orientation) {
        this(CollisionShape.CIRCLE, x, y, 2 * radius, 2 * radius, orientation);
    }

    private DefaultCritter(CollisionShape boundingShape, double x, double y, double width, double height,
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

    public Shape getCollisionShape() {
        switch (boundingShape) {
            case RECTANGLE:
                return new Rectangle(position.x, position.y, width, height);

            case CIRCLE:
                return new Circle(position.x, position.y, width / 2);

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
        return new Vector2D(Math.sin(orientation), -Math.cos(orientation));
    }

    public void lookAt(DefaultCritter other) {
        orientation = this.positionRelativeTo(other).getBearing();
    }

    public void lookAt(double x, double y) {
        Vector2D relativeVector = new Vector2D(x - position.x, y - position.y);
        orientation = relativeVector.getBearing();
    }

    public void updateRotation() {
        // uses timestep of 1
        // uses final velocity as proxy for average velocity

        angularVelocity += angularAcceleration;
        orientation += angularVelocity + 0.5 * angularAcceleration;
    }

    public void draw(Graphics2D g) {

        g.setColor(Color.RED);

        // Draw body (rotated)
        getCollisionShape().draw(g);

        // Draw lookvector line
        Vector2D lineEnd = position.add(lookVector().scale(height / 2));
        LineSegment line = new LineSegment(position, lineEnd);
        line.draw(g);
    }

    public void update() {
        updateTranslation();
        updateRotation();
    }

    public boolean isCollidingWith(DefaultCritter critter) {
        return this.getCollisionShape().intersects(critter.getCollisionShape());
    }

    public void handleCollisionWith(DefaultCritter critter) {
        // Default do nothing
    }

    public boolean mayBeRemoved() {
        return false; // Default to false
    }

}