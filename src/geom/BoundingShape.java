package geom;

public interface BoundingShape extends Shape {

    public boolean contains(double x, double y);

    public boolean contains(Vector2D point);

    public boolean contains(Shape shape);

    public Vector2D getRandomPositionInside();

}