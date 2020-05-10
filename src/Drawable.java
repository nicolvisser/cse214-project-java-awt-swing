import java.awt.Graphics2D;

/**
 * A drawable class 'wants' its draw method to be called in whenever the game
 * renders a frame to the graphics object. Additionally, a drawable class does
 * not need any other parameters besides graphics object to draw its contents
 */
public interface Drawable {

    public void draw(Graphics2D g2);

}