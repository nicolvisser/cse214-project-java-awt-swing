import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

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

    public static void loopMenuMusic() {
        loopMusic("resources/audio/Death Is Just Another Path.wav");
    }

    public static void fadeOutMusicThenStartGameMusic() {
        fadeOutBackgroundMusicThenStartNewTrack("resources/audio/ambientmain_0.wav");
    }

    public static void fadeOutMusicThenStartMenuMusic() {
        fadeOutBackgroundMusicThenStartNewTrack("resources/audio/Death Is Just Another Path.wav");
    }

    public static void pauseBackgroundMusic() {
        backgroundMusicPausedPlayBackPosition = backgroundMusic.getMicrosecondPosition();
        backgroundMusic.stop();
    }

    public static void resumeBackgroundMusic() {
        System.out.println("Here");
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        backgroundMusic.setMicrosecondPosition(backgroundMusicPausedPlayBackPosition);
        backgroundMusic.start();
    }

    // ===============================================================================================

    private static void fadeOutBackgroundMusicThenStartNewTrack(String filename) {
        new Thread(new Runnable() {
            public void run() {
                while (backgroundMusicVolume > 0) {
                    try {
                        setVolumeOfClip(backgroundMusic, backgroundMusicVolume--);
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                backgroundMusic.stop();
                backgroundMusic.close();
                backgroundMusicVolume = 40;
                loopMusic(filename);
            }
        }).start();
    }

    public static Clip backgroundMusic;
    public static int backgroundMusicVolume = 40;
    public static long backgroundMusicPausedPlayBackPosition;

    private static void loopMusic(String filename) {
        final AudioInputStream ais = getAudioInputStreamFromFile(filename);

        try {
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(ais);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }

        setVolumeOfClip(backgroundMusic, backgroundMusicVolume);
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        backgroundMusic.start();
    }

    private static AudioInputStream getAudioInputStreamFromFile(String filename) {
        AudioInputStream ais = null;
        try {
            File file = new File(filename);
            ais = AudioSystem.getAudioInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ais;
    }

    private static void setVolumeOfClip(Clip clip, int volume) {
        // map volumne to decibel percentage (a non-linear relationship)
        float dbPercentage;
        if (volume <= 0.01) {
            dbPercentage = 0;
        } else {
            dbPercentage = (float) (100 + 10 * (Math.log(volume / 100f) / Math.log(2)));
        }

        // ensure dbPercentage is between 0 and 100 after mapping
        dbPercentage = Math.min(dbPercentage, 100);
        dbPercentage = Math.max(dbPercentage, 0);

        // get min and max volumne from volume control
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();

        // use volumePerc to determine new volume level
        float newVolume = min + (max - min) * dbPercentage / 100f;

        // use volumecontrol to set new volume
        volumeControl.setValue(newVolume);
    }

}