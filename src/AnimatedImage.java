import java.awt.Graphics2D;

import javax.swing.ImageIcon;

public class AnimatedImage {

    public enum AnimationType {
        ONCE, LOOP
    }

    private ImageIcon[] imageIcons;
    private final int numFrames;
    private final AnimationType animationType;
    private int currentIdx = 0;
    private int speedFactor, callsRemainingBeforeNextFrame;
    public boolean isComplete = false;

    public AnimatedImage(String filenameNoExt, String extension, int numFrames, AnimationType animationType) {
        this(filenameNoExt, extension, numFrames, animationType, 1);
    }

    public AnimatedImage(String filenameNoExt, String extension, int numFrames, AnimationType animationType,
            int speedFactor) {
        this.numFrames = numFrames;
        this.animationType = animationType;

        imageIcons = new ImageIcon[numFrames];

        this.speedFactor = speedFactor;
        this.callsRemainingBeforeNextFrame = speedFactor;

        for (int i = 0; i < numFrames; i++) {
            String filename = String.format("%s%05d.%s", filenameNoExt, i, extension);
            imageIcons[i] = new ImageIcon(filename);
        }
    }

    public void draw(Graphics2D g, int x, int y, int w, int h) {
        g.drawImage(imageIcons[currentIdx].getImage(), x, y, w, h, null);

        callsRemainingBeforeNextFrame--;
        if (callsRemainingBeforeNextFrame == 0) {
            updateIndex();
            callsRemainingBeforeNextFrame = speedFactor;
        }
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