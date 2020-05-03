package geom;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Circle implements BoundingShape {

    public Vector2D center;
    public double radius;

    // ===== Constructors =====>

    public Circle(double x, double y, double radius) {
        this.center = new Vector2D(x, y);
        this.radius = radius;
    }

    public Circle(Vector2D center, double radius) {
        this.center = new Vector2D(center.x, center.y);
        this.radius = radius;
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
        }
        return false;
    }

    private boolean intersects(Rectangle rect) {
        return rect.intersects(this);
    }

    private boolean intersects(Circle other) {
        double dx = this.center.x - other.center.x;
        double dy = this.center.y - other.center.y;
        return dx * dx + dy * dy <= (this.radius + other.radius) * (this.radius + other.radius);
    }

    private boolean intersects(LineSegment lineSeg) {
        // To be implemented in future to complete API. not necessary for game at
        // current stage
        return false;
    }

    private boolean intersects(Ray ray) {
        return ray.intersects(this); // get code from Ray class instead
    }

    private Ellipse2D toEllipse2D() {
        return new Ellipse2D.Double(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
    }

    @Override
    public void draw(Graphics2D g) {
        g.draw(toEllipse2D());
    }

    // ===== Methods related to BoundedShape interface =====>

    @Override
    public Vector2D getCenter() {
        return new Vector2D(center.x, center.y);
    }

    @Override
    public boolean contains(double x, double y) {
        double dx = this.center.x - x;
        double dy = this.center.y - y;
        return dx * dx + dy * dy <= radius * radius;
    }

    @Override
    public boolean contains(Vector2D point) {
        double dx = this.center.x - point.x;
        double dy = this.center.y - point.y;
        return dx * dx + dy * dy <= radius * radius;
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

    // see JimBalter's comment @
    // https://stackoverflow.com/questions/14097290/check-if-circle-contains-rectangle
    private boolean contains(Rectangle rect) {
        double dx = Math.max(center.x - rect.xmin(), rect.xmax() - center.x);
        double dy = Math.max(center.y - rect.ymin(), rect.ymax() - center.y);
        return radius * radius >= dx * dx + dy * dy;
    }

    private boolean contains(Circle other) {
        double dx = this.center.x - other.center.x;
        double dy = this.center.y - other.center.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double maxRadius = Math.max(this.radius, other.radius);
        double minRadius = Math.min(this.radius, other.radius);
        return distance + minRadius <= maxRadius;
    }

    private boolean contains(LineSegment lineSeg) {
        // To be implemented in future to complete API. not necessary for game at
        // current stage
        return false;
    }

    private boolean contains(Ray ray) {
        return false;
    }

    @Override
    public Vector2D getRandomPositionInside() {
        double rx, ry;
        do {
            rx = 2 * Math.random() - 1;
            ry = 2 * Math.random() - 1;
        } while (rx * rx + ry * ry > 1);
        return new Vector2D(center.x + rx * radius, center.y + ry * radius);
    }

}