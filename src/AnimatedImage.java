import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class AnimatedImage {

    private ImageIcon[] imageIcons;
    private final int numFrames;
    private int currentIdx = 0;

    public AnimatedImage(String filenameNoExt, String extension, int numFrames) {
        this.numFrames = numFrames;

        imageIcons = new ImageIcon[numFrames];

        for (int i = 0; i < numFrames; i++) {
            String filename = String.format("%s%05d.%s", filenameNoExt, i, extension);
            imageIcons[i] = new ImageIcon(filename);
        }
    }

    public void draw(Graphics2D g, int x, int y, int w, int h) {
        if (++currentIdx == numFrames) {
            currentIdx = 0;
        }
        g.drawImage(imageIcons[currentIdx].getImage(), x, y, w, h, null);
    }

}