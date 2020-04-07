package geom;

import java.awt.Graphics;

public interface Shape {

    public boolean intersects(Shape shape);

    public void draw(Graphics g);

}