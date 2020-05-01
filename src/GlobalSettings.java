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

    static int volume = 10;

    static boolean DEBUG = false; // enable visual debugging mode or not

    static void setViewSize(final int w, final int h) {
        vw = w;
        vh = h;
        vmin = Math.min(w, h);
        vmax = Math.max(w, h);
    }

}