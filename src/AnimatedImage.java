import java.awt.Graphics2D;
import java.io.Serializable;

import javax.swing.ImageIcon;

public class AnimatedImage implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum AnimationType {
        ONCE, LOOP
    }

    private ImageIcon[] imageIcons;
    private final int numFrames;
    private final AnimationType animationType;
    private int currentIdx = 0;
    public boolean isComplete = false;

    public AnimatedImage(String filenameNoExt, String extension, int numFrames, AnimationType animationType) {
        this.numFrames = numFrames;
        this.animationType = animationType;

        imageIcons = new ImageIcon[numFrames];

        for (int i = 0; i < numFrames; i++) {
            String filename = String.format("%s%05d.%s", filenameNoExt, i, extension);
            imageIcons[i] = new ImageIcon(filename);
        }
    }

    public void draw(Graphics2D g, int x, int y, int w, int h) {
        g.drawImage(imageIcons[currentIdx].getImage(), x, y, w, h, null);
        updateIndex();
    }

    public void reset() {
        currentIdx = 0;
        isComplete = false;
    }

    private void updateIndex() {

        switch (animationType) {
            case LOOP:
                if (++currentIdx == numFrames) {
                    currentIdx = 0;
                }
                break;

            case ONCE:
                if (!isComplete && ++currentIdx >= numFrames - 1) {
                    isComplete = true;
                }
                break;

            default:
                break;
        }

    }

}