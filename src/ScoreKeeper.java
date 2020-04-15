import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import geom.Vector2D;

/**
 * ScoreKeeper is a simple score incrementer with draw method. You can store the
 * score in one class and pass a reference to other classes that might want to
 * update it.
 */
public class ScoreKeeper {

    private int score = 0;
    private int vw; // view width
    private int vh; // view height

    private Queue<PointsAnimation> animations = new LinkedList<>();

    ScoreKeeper(int w, int h) {
        vw = w;
        vh = h;
    }

    /**
     * Adds points to score
     * 
     * @param points Points to be added to score
     */
    void addPoints(int points) {
        score += points;
    }

    /**
     * Adds points to score and adds animation to queue. A small animation of amount
     * of points added will be drawn in the next couple of frames
     * 
     * @param points Points to be added to score
     * @param pos    Position of animation
     */
    void addPoints(int points, Vector2D pos) {
        score += points;
        animations.add(new PointsAnimation(points, (int) pos.x, (int) pos.y));
    }

    int getScore() {
        return score;
    }

    void draw(Graphics2D g2) {
        String str = "Score: " + score;
        int strWidth = g2.getFontMetrics().stringWidth(str);
        int x = vw * 95 / 100 - strWidth;
        int y = vh * 95 / 100;
        g2.setColor(Color.ORANGE);
        g2.drawString(str, x, y);

        Iterator<PointsAnimation> animIterator = animations.iterator();
        while (animIterator.hasNext()) {
            PointsAnimation anim = animIterator.next();
            anim.draw(g2);
            if (anim.finished) {
                animIterator.remove();
            }
        }
    }

    private class PointsAnimation {

        private static final int DEFUALT_DURATION_MILLIS = 500;
        private int points, x, y;
        private int remainingMillis = DEFUALT_DURATION_MILLIS;
        private long lastTimeMillis = System.currentTimeMillis();
        boolean finished = false;

        PointsAnimation(int points, int x, int y) {
            this.points = points;
            this.x = x;
            this.y = y;
        }

        void draw(Graphics2D g2) {
            if (remainingMillis > 0) {
                float opacity = (float) remainingMillis / DEFUALT_DURATION_MILLIS;

                long currentTileMillis = System.currentTimeMillis();
                remainingMillis -= (int) (currentTileMillis - lastTimeMillis);
                lastTimeMillis = currentTileMillis;

                g2.setColor(new Color(0, 1, 0, opacity));
                g2.drawString("+" + points, x, y);
            } else {
                finished = true;
            }
        }
    }

}