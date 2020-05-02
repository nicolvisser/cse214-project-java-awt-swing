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

    private Queue<TextAnimation> animations = new LinkedList<>();

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
        animations.add(new TextAnimation("+ " + points, (int) pos.x, (int) pos.y, 500));
    }

    int getScore() {
        return score;
    }

    void draw(Graphics2D g2) {

        Utils.scaleFont(g2, 2f);
        String str = score + "    SCORE";
        g2.setColor(Color.ORANGE);
        int x = vw * 95 / 100;
        int y = vh * 95 / 100;
        Utils.drawRightAlignedText(g2, x, y, str);
        Utils.scaleFont(g2, 0.5f);

        Iterator<TextAnimation> animIterator = animations.iterator();
        while (animIterator.hasNext()) {
            TextAnimation anim = animIterator.next();
            anim.draw(g2);
            if (anim.finished) {
                animIterator.remove();
            }
        }
    }

}