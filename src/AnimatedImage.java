import java.awt.Graphics2D;

import javax.swing.ImageIcon;

/**
 * AnimatedImage is a helper tool to draw an animated image based on a series of
 * images on a Graphics2D object.
 */
public class AnimatedImage {

    public enum AnimationType {
        ONCE, LOOP
    }

    private final ImageIcon[] imageIcons;
    private final int numFrames;
    private final AnimationType animationType;
    private int currentIdx = 0;
    private int speedFactor, callsRemainingBeforeNextFrame;
    public boolean isComplete = false;

    /**
     * The images should be stored with the following naming convention:
     * name00000.png, name00001.png, name000002.png
     * 
     * i.e. filename THEN index from 0 to N-1 which is displayed as 5 digits THEN
     * extension
     */
    public AnimatedImage(String filenameNoExt, String extension, int numFrames, AnimationType animationType) {
        this(filenameNoExt, extension, numFrames, animationType, 1);
    }

    /**
     * Constructor with additional argument that controls rate at which images are
     * drawn. If the speed factor is 0.5 then the animated image will only show a
     * new frame ever 2nd render of game frames
     */
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

    // draws animated image at current state on graphics2D object at specified
    // position and at specified size THEN updates the index of image frame to
    // display for next render
    public void draw(Graphics2D g2, int x, int y, int w, int h) {
        g2.drawImage(imageIcons[currentIdx].getImage(), x, y, w, h, null);

        callsRemainingBeforeNextFrame--;
        if (callsRemainingBeforeNextFrame == 0) {
            updateIndex();
            callsRemainingBeforeNextFrame = speedFactor;
        }
    }

    // resets to initial state
    public void reset() {
        currentIdx = 0;
        isComplete = false;
    }

    // helper method to update index based on animation type
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