package geom;

import java.awt.Graphics2D;

public interface Shape {

    public boolean intersects(Shape shape);

    public void draw(Graphics2D g);

}