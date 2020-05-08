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
        for (String arg : args) {
            if (arg.equals("-f") || arg.equals("-fullscreen")) {
                runInFullscreen = true;
            }
            if (arg.equals("-d") || arg.equals("-debug")) {
                GlobalSettings.DEBUG = true;
            }
        }

        InvadersFrame game = new InvadersFrame(runInFullscreen);

        game.run();

        GameAudio.close();
        System.exit(0);

    }

}