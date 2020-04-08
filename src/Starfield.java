import geom.*;
import java.awt.Graphics;
import java.awt.Color;

public class Starfield {

    private static final int NUM_STARS = 1000;
    private static final double STARS_PARALLAX_SENSITIVITY_MAX = 0.083;

    private final Rectangle drawArea;
    private final Star[] stars;

    private static class Star {
        public Vector2D position, velocity;
        public double parallaxSensitivity;

        public Star(Vector2D position, double parallaxSensitivity) {
            this.position = position;
            this.velocity = Vector2D.zero();
            this.parallaxSensitivity = parallaxSensitivity;
        }

        public void update() {
            position = position.add(velocity);
        }
    }

    public Starfield(Rectangle drawArea) {
        this.drawArea = drawArea;
        stars = new Star[NUM_STARS];

        for (int i = 0; i < NUM_STARS; i++) {
            Vector2D position = drawArea.getRandomPositionInside();
            double parallaxSensitivity = Math.random() * STARS_PARALLAX_SENSITIVITY_MAX;
            stars[i] = new Star(position, parallaxSensitivity);
        }
    }

    public void update(Vector2D observerVelocity) {
        for (Star star : stars) {
            // give parallax effect based on observer velocity
            star.velocity = observerVelocity.scale(-star.parallaxSensitivity);
            star.update();

            // recycle stars that go out of x or bounds
            if (star.position.x < drawArea.xmin()) {
                star.position.x = drawArea.xmax();
            } else if (star.position.x > drawArea.xmax()) {
                star.position.x = drawArea.xmin();
            }
            if (star.position.y < drawArea.ymin()) {
                star.position.y = drawArea.ymax();
            } else if (star.position.y > drawArea.ymax()) {
                star.position.y = drawArea.ymin();
            }
        }
    }

    public void draw(Graphics g) {
        // draw each star with a flicker effect
        for (Star star : stars) {
            int grayLevel = 130 + (int) (Math.random() * 126);
            g.setColor(new Color(grayLevel, grayLevel, grayLevel));
            int x = (int) star.position.x;
            int y = (int) star.position.y;
            g.drawLine(x, y, x, y);
        }
    }
}