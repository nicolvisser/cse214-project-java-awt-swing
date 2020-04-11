package geom;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class Vector2D implements Serializable {

    private static final long serialVersionUID = 1L;

    public double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2D(" + x + ", " + y + ")";
    }

    public Point2D toPoint() {
        return new Point2D.Double(x, y);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        double mag = magnitude();
        return new Vector2D(x / mag, y / mag);
    }

    public static Vector2D zero() {
        return new Vector2D(0, 0);
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public Vector2D scale(double c) {
        return new Vector2D(c * x, c * y);
    }

    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * Returns angle in radians as measured from negative y axis.
     * 
     * Useful in a swing frame to get angle with North / upwards axis.
     */
    public Double getBearing() {
        return Math.PI / 2 + Math.atan2(y, x);
    }

    public Double getPolarAngle() {
        return Math.atan2(y, x);
    }
}