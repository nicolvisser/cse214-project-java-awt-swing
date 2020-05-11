import java.awt.*;

/**
 * Helper class to draw fading text animation on a graphics object
 */
public class TextAnimation {

    private String text;
    private int x, y;
    private int duration, remainingMillis;
    private long lastTimeMillis = -1;
    boolean finished = false;

    TextAnimation(String text, int x, int y, int duration) {
        this.duration = duration;
        remainingMillis = duration;
        this.text = text;
        this.x = x;
        this.y = y;
    }

    void draw(Graphics2D g2) {
        if (remainingMillis > 0) {

            if (lastTimeMillis == -1) {
                lastTimeMillis = System.currentTimeMillis();
            } else {
                long currentTileMillis = System.currentTimeMillis();
                remainingMillis -= (int) (currentTileMillis - lastTimeMillis);
                lastTimeMillis = currentTileMillis;
            }

            int r = g2.getColor().getRed();
            int g = g2.getColor().getGreen();
            int b = g2.getColor().getBlue();
            int a = 255 * Math.max(remainingMillis, 0) / duration;
            g2.setColor(new Color(r, g, b, a));

            Utils.drawCenteredText(g2, x, y, text);
        } else {
            finished = true;
        }
    }
}