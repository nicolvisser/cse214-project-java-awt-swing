package geom;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class LineSegment implements Shape {

    Vector2D start;
    Vector2D end;

    public LineSegment(Vector2D start, Vector2D end) {
        this.start = new Vector2D(start.x, start.y);
        this.end = new Vector2D(end.x, end.y);
    }

    public LineSegment(Vector2D start, Vector2D direction, double length) {
        this.start = new Vector2D(start.x, start.y);
        this.end = start.add(direction.scale(length));
    }

    public Line2D toLine2D() {
        return new Line2D.Double(start.x, start.y, end.x, end.y);
    }

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

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.draw(toLine2D());
    }

}