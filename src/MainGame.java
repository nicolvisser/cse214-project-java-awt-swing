public class MainGame {

    /**
     * A main method to run the game.
     * 
     * @param args if no parameters specified game will run in windowed mode.
     *             Alternatively you can add one or more of the following
     *             parameters: '-f' or '-fullscreen' to run the game in full screen
     *             exclusive mode, '-d' or '-debug' ro run the game in visual
     *             debugging mode.
     *
     */
    public static void main(String[] args) {
        // handle arguments passed
        boolean runInFullscreen = false;
        int w = -1, h = -1;
        for (String arg : args) {
            if (arg.equals("-f") || arg.equals("-fullscreen")) {
                runInFullscreen = true;
            } else if (arg.equals("-d") || arg.equals("-debug")) {
                GameSettings.DEBUG = true;
            } else {
                // try and parse arguments as integer and store
                try {
                    int num = Integer.parseInt(arg);
                    if (w == -1) {
                        w = num;
                    } else if (h == -1) {
                        h = num;
                    }
                } catch (NumberFormatException e) {
                    // skip argument
                }
            }
        }

        // if at least one size dimension was specified, change window size in
        // GameSettings
        if (w != -1 && h == -1) {
            GameSettings.setViewSize(w, w); // square
        } else if (w != -1 && h != -1) {
            GameSettings.setViewSize(w, h);
        }

        InvadersFrame game = new InvadersFrame(runInFullscreen);

        game.run();

        GameAudio.close();
        System.exit(0);

    }

}