/**
 * GlobalSettings
 */
public class GlobalSettings {

    static final int DEFAULT_WINDOWED_MODE_WIDTH = 800;
    static final int DEFAULT_WINDOWED_MODE_HEIGHT = 800;

    static int vw = DEFAULT_WINDOWED_MODE_WIDTH; // viewport width
    static int vh = DEFAULT_WINDOWED_MODE_HEIGHT; // viewport width
    static int vmin = Math.min(vw, vh); // viewport min dimension
    static int vmax = Math.max(vw, vh);; // viewport max dimension

    static int volumeSounds = 15;
    static int volumeMusic = 100;

    static boolean DEBUG = false; // enable visual debugging mode or not

    public static boolean isFullscreen; // don't change here. change via frame object

    // default key codes: used in reset to defaults or if key file becomes corrupt
    public static final int[] DEFAULT_KEYCODES_P1 = { 65, 68, 37, 39, 38, 40 };
    public static final int[] DEFAULT_KEYCODES_P2 = { 90, 67, 74, 76, 73, 75 };

    static void setViewSize(final int w, final int h) {
        vw = w;
        vh = h;
        vmin = Math.min(w, h);
        vmax = Math.max(w, h);
    }

}