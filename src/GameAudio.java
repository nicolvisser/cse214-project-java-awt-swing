public class GameAudio {

    public static void playSoundMenuChange() {
        StdAudio.play("resources/audio/menuChange.wav", GlobalSettings.volume);
    }

    public static void playSoundMenuSelect() {
        StdAudio.play("resources/audio/menuSelect.wav", GlobalSettings.volume);
    }

    public static void playSoundExplosion() {
        StdAudio.play("resources/audio/explosion.wav", GlobalSettings.volume);
    }

    public static void playSoundPulse() {
        StdAudio.play("resources/audio/pulse.wav", GlobalSettings.volume);
    }

}