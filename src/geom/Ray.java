package geom;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Ray implements Shape {

    Vector2D start;
    Vector2D direction;

    public Ray(Vector2D start, Vector2D direction) {
        this.start = new Vector2D(start.x, start.y);
        this.direction = new Vector2D(direction.x, direction.y).normalize();
    }

    public double lengthUntilIntersection(Shape shape) {
        if (shape instanceof Rectangle) {
            return lengthUntilIntersection((Rectangle) shape);
        } else if (shape instanceof Circle) {
            return lengthUntilIntersection((Circle) shape);
        } else if (shape instanceof LineSegment) {
            return lengthUntilIntersection((LineSegment) shape);
        } else if (shape instanceof Ray) {
            return lengthUntilIntersection((Ray) shape);
        } else
            return Double.POSITIVE_INFINITY;
    }

    // following zacharmarz's answer at
    // https://gamedev.stackexchange.com/questions/18436/most-efficient-aabb-vs-ray-collision-algorithms
    public double lengthUntilIntersection(Rectangle rect) {
        double t1 = (rect.xmin() - start.x) / direction.x;
        double t2 = (rect.xmax() - start.x) / direction.x;
        double t3 = (rect.ymin() - start.y) / direction.y;
        double t4 = (rect.ymax() - start.y) / direction.y;

        double tmin = Math.max(Math.min(t1, t2), Math.min(t3, t4));
        double tmax = Math.min(Math.max(t1, t2), Math.max(t3, t4));

        if (tmax < 0) {
            // extended line of ray is intersecting AABB, but the whole AABB is behind ray
            return Double.POSITIVE_INFINITY;
        }

        if (tmin > tmax) {
            // ray doesn't intersect AABB
            return Double.POSITIVE_INFINITY;
        }

        return Math.max(0, tmin);
    }

    public double lengthUntilIntersection(Circle circ) {

        Vector2D vStartToCenter = circ.center.subtract(start);
        if (vStartToCenter.magnitude() <= circ.radius) { // start inside circle
            return 0;
        }

        double projection = this.direction.dot(vStartToCenter);
        if (projection < 0) { // circle behind line
            return Double.POSITIVE_INFINITY;
        }

        double perpendic = (vStartToCenter.subtract(direction.scale(projection))).magnitude();
        if (perpendic > circ.radius) { // line outside of circle
            return Double.POSITIVE_INFINITY;
        }

        double l = Math.sqrt(circ.radius * circ.radius - perpendic * perpendic);
        return projection - l;
    }

    public double lengthUntilIntersection(LineSegment lineSeg) {
        // To be implemented in future to complete API. not necessary for game at
        // current stage
        return Double.POSITIVE_INFINITY;
    }

    public double lengthUntilIntersection(Ray ray) {
        // To be implemented in future to complete API. not necessary for game at
        // current stage
        return Double.POSITIVE_INFINITY;
    }

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

    public boolean intersects(Rectangle rect) {
        return Double.isFinite(lengthUntilIntersection(rect));
    }

    public boolean intersects(Circle circ) {
        return Double.isFinite(lengthUntilIntersection(circ));
    }

    public boolean intersects(LineSegment lineSeg) {
        // To be implemented in future to complete API. not necessary for game at
        // current stage
        return false;
    }

    public boolean intersects(Ray ray) {
        // To be implemented in future to complete API. not necessary for game at
        // current stage
        return false;
    }

    @Override
    public void draw(Graphics2D g) {
        Vector2D end = start.add(direction.scale(999999));
        Line2D line2D = new Line2D.Double(start.x, start.y, end.x, end.y);
        g.draw(line2D);
    }

}