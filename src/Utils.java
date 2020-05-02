import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Utils {

    public static void drawCenteredText(Graphics2D g2, double x, double y, String text) {
        drawCenteredText(g2, x, y, text, 1.0f);
    }

    public static void drawCenteredText(Graphics2D g2, double x, double y, String text, float scale) {

        scaleFont(g2, scale);

        FontMetrics fm = g2.getFontMetrics();
        int wStr = fm.stringWidth(text);
        int hStr = fm.getHeight();
        float xStr = (float) (x - wStr / 2);
        float yStr = (float) (y + hStr / 3);
        g2.drawString(text, xStr, yStr);

        scaleFont(g2, 1 / scale);
    }

    public static void drawLeftAlignedText(Graphics2D g2, double x, double y, String text) {
        drawLeftAlignedText(g2, x, y, text, 1.0f);
    }

    public static void drawLeftAlignedText(Graphics2D g2, double x, double y, String text, float scale) {

        scaleFont(g2, scale);

        FontMetrics fm = g2.getFontMetrics();
        int hStr = fm.getHeight();
        float xStr = (float) (x);
        float yStr = (float) (y + hStr / 3);
        g2.drawString(text, xStr, yStr);

        scaleFont(g2, 1 / scale);
    }

    public static void drawRightAlignedText(Graphics2D g2, double x, double y, String text) {
        drawRightAlignedText(g2, x, y, text, 1.0f);
    }

    public static void drawRightAlignedText(Graphics2D g2, double x, double y, String text, float scale) {

        scaleFont(g2, scale);

        FontMetrics fm = g2.getFontMetrics();
        int wStr = fm.stringWidth(text);
        int hStr = fm.getHeight();
        float xStr = (float) (x - wStr);
        float yStr = (float) (y + hStr / 3);
        g2.drawString(text, xStr, yStr);

        scaleFont(g2, 1 / scale);
    }

    public static void scaleFont(Graphics2D g2, float scale) {
        // Change font size. Credits to:
        // https://stackoverflow.com/questions/18249592/how-to-change-font-size-in-drawstring-java
        Font currentFont = g2.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * scale);
        g2.setFont(newFont);
    }

}