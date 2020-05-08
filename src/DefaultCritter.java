import java.awt.Color;
import java.awt.Graphics2D;

import geom.Circle;
import geom.LineSegment;
import geom.BoundingShape;
import geom.Vector2D;

public class DefaultCritter implements Collidable, Disposable {

    public double width, height;

    // positive x to right, positive y to bottom (as with swing frame)
    public Vector2D position, velocity, acceleration;

    // orientation correspods to bearing in radians
    // i.e. radians clockwise from north, where north is at top of screen
    public double orientation, angularVelocity, angularAcceleration;

    public DefaultCritter(double x, double y, double radius, double orientation) {

        this.width = 2 * radius;
        this.height = 2 * radius;

        position = new Vector2D(x, y);
        velocity = Vector2D.zero();
        acceleration = Vector2D.zero();

        this.orientation = orientation;
        angularVelocity = 0;
        angularAcceleration = 0;
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
        orientation = other.positionRelativeTo(this).getBearing();
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

    public void update(int dt) {
        updateTranslation();
        updateRotation();
    }

    // ============ METHODS ASSOCIATED WITH COLIDABLE INTERFACE ============= >>>

    public BoundingShape getCollisionShape() {
        return new Circle(position.x, position.y, width / 2);
    }

    public boolean isCollidingWith(Collidable otherCollidable) {
        // use geom package to check if collision shapes intersect
        return this.getCollisionShape().intersects(otherCollidable.getCollisionShape());
    }

    public void handleCollisionWith(Collidable otherCollidable) {
        // defaults to nothing, need to implement in each sub class
    }

    // ============ METHODS ASSOCIATED WITH DISPOSABLE INTERFACE ============= >>>

    @Override
    public boolean mayBeDisposed() {
        return false; // defaults to false, need to implement in each sub class
    }

}