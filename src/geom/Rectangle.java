package geom;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Rectangle implements BoundingShape {

    public double width, height;
    public Vector2D center;

    // ===== Constructors =====>

    public Rectangle(double x, double y, double width, double height) {
        this.center = new Vector2D(x, y);
        this.width = width;
        this.height = height;
    }

    public Rectangle(Vector2D center, double width, double height) {
        this.center = new Vector2D(center.x, center.y);
        this.width = width;
        this.height = height;
    }

    // ===== Rectangle specific methods =====>

    public double xmin() {
        return center.x - width / 2;
    }

    public double xmax() {
        return center.x + width / 2;
    }

    public double ymin() {
        return center.y - height / 2;
    }

    public double ymax() {
        return center.y + height / 2;
    }

    public void setPosition(double x, double y) {
        center.x = x;
        center.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Rectangle zero() {
        return new Rectangle(0, 0, 0, 0);
    }

    public boolean isEmpty() {
        return (width == 0) || (height == 0);
    }

    public static Rectangle infinite() {
        return new Rectangle(0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public boolean isInfinite() {
        return Double.isInfinite(width) || Double.isInfinite(height);
    }

    public Rectangle intersection(Rectangle other) {
        if (intersects(other)) {
            double newXmin = Math.max(this.xmin(), other.xmin());
            double newXmax = Math.min(this.xmax(), other.xmax());
            double newYmin = Math.max(this.ymin(), other.ymin());
            double newYmax = Math.min(this.ymax(), other.ymax());

            double newX = (newXmin + newXmax) / 2;
            double newY = (newYmin + newYmax) / 2;
            double newWidth = newXmax - newXmin;
            double newHeight = newYmax - newYmin;

            return new Rectangle(newX, newY, newWidth, newHeight);// possibly also returns rectangle of zero width
        } else {
            return zero();
        }
    }

    // ===== Methods related to Shape interface =====>

    @Override
    public boolean intersects(Shape shape) {
        if (shape instanceof Rectangle) {
            return intersects((Rectangle) shape);
        } else if (shape instanceof Circle) {
            return intersects((Circle) shape);
        } else if (shape instanceof LineSegment) {
            return intersects((LineSegment) shape);
        } else if (shape instanceof Ray) {
            return intersects((Ray) shape);
        } else
            return false;
    }

    public boolean intersects(Rectangle other) {
        return !(this.xmin() > other.xmax() || this.xmax() < other.xmin() || this.ymin() > other.ymax()
                || this.ymax() < other.ymin());
    }

    // see http://www.jeffreythompson.org/collision-detection/circle-rect.php
    public boolean intersects(Circle circle) {
        // temporary variables to set edges for testing
        double testX = circle.center.x;
        double testY = circle.center.y;

        // which edge is closest?
        if (circle.center.x < xmin())
            testX = xmin(); // test left edge
        else if (circle.center.x > xmax())
            testX = xmax(); // right edge
        if (circle.center.y < ymin())
            testY = ymin(); // bottom edge
        else if (circle.center.y > ymax())
            testY = ymax(); // top edge

        // get distance from closest edges
        double distX = circle.center.x - testX;
        double distY = circle.center.y - testY;
        double distance = Math.sqrt((distX * distX) + (distY * distY));

        // if the distance is less than the radius, collision!
        return distance <= circle.radius;
    }

    public boolean intersects(LineSegment lineSeg) {
        return lineSeg.intersects(this);
    }

    public boolean intersects(Ray ray) {
        return ray.intersects(this);
    }

    public Rectangle2D toRectangle2D() {
        return new Rectangle2D.Double(xmin(), ymin(), width, height);
    }

    @Override
    public void draw(Graphics2D g) {
        g.draw(toRectangle2D());
    }

    // ===== Methods related to BoundedShape interface =====>

    @Override
    public Vector2D getCenter() {
        return new Vector2D(center.x, center.y);
    }

    @Override
    public boolean contains(double x, double y) {
        return (x >= xmin()) && (x <= xmax()) && (y >= ymin()) && (y <= ymax());
    }

    @Override
    public boolean contains(Vector2D point) {
        return contains(point.x, point.y);
    }

    @Override
    public boolean contains(Shape shape) {
        if (shape instanceof Rectangle) {
            return contains((Rectangle) shape);
        } else if (shape instanceof Circle) {
            return contains((Circle) shape);
        } else if (shape instanceof LineSegment) {
            return contains((LineSegment) shape);
        } else if (shape instanceof Ray) {
            return contains((Ray) shape);
        }
        return false;
    }

    public boolean contains(Rectangle other) {
        return (other.xmin() >= this.xmin() && other.xmax() <= this.xmax() && other.ymin() >= this.ymin()
                && other.ymax() <= this.ymax());
    }

    public boolean contains(Circle circle) {
        return (circle.center.x + circle.radius <= xmax()) && (circle.center.x - circle.radius >= xmin())
                && (circle.center.y + circle.radius <= ymax()) && (circle.center.y - circle.radius >= ymin());
    }

    private boolean contains(Ray ray) {
        // TODO implement
        return false;
    }

    private boolean contains(LineSegment lineSeg) {
        return false;
    }

    public Vector2D getRandomPositionInside() {
        double rx = xmin() + Math.random() * width;
        double ry = ymin() + Math.random() * height;
        return new Vector2D(rx, ry);
    }

}