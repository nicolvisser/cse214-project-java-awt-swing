import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class GameAudio {

    private static final int SAMPLE_RATE = 44100;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int SAMPLE_BUFFER_SIZE = 4096;
    private static final int MONO = 1;
    private static final boolean LITTLE_ENDIAN = false;
    private static final boolean SIGNED = true;

    private static SourceDataLine line; // to play the sound

    static {
        try {
            AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, MONO, SIGNED, LITTLE_ENDIAN);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
        } catch (LineUnavailableException e) {
            System.out.println(e.getMessage());
        }
        line.start();
    }

    public static void close() {
        line.drain();
        line.stop();
    }

    public static void playSoundMenuChange() {
        play("resources/audio/menuChange.wav");
    }

    public static void playSoundMenuSelect() {
        play("resources/audio/menuSelect.wav");
    }

    public static void playSoundExplosion() {
        play("resources/audio/explosion.wav");
    }

    public static void playSoundPulse() {
        play("resources/audio/pulse.wav");
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

    public static synchronized void play(final String filename) {
        play(filename, GlobalSettings.volumeSounds);
    }

    public static synchronized void play(final String filename, int volume) {
        new Thread(new Runnable() {
            public void run() {
                AudioInputStream ais = getAudioInputStreamFromFile(filename);
                stream(ais, volume);
            }
        }).start();
    }

    // ===============================================================================================

    private static void stream(AudioInputStream ais, int volume) {
        SourceDataLine line = null;
        int BUFFER_SIZE = 4096; // 4K buffer

        try {
            AudioFormat audioFormat = ais.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);

            FloatControl volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            setVolumeViaControl(volumeControl, volume);

            line.start();
            byte[] samples = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count = ais.read(samples, 0, BUFFER_SIZE)) != -1) {
                line.write(samples, 0, count);
            }
        } catch (

        IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } finally {
            if (line != null) {
                line.drain();
                line.close();
            }
        }
    }

    // =================== BACKGROUND MUSIC =====================================

    private static Clip backgroundMusic;
    private static int backgroundMusicVolume = 40;
    private static long backgroundMusicPausedPlayBackPosition;

    private static synchronized void fadeOutBackgroundMusicThenStartNewTrack(String filename) {
        new Thread(new Runnable() {
            public void run() {
                while (backgroundMusicVolume > 0) {
                    try {
                        FloatControl volumeControl = (FloatControl) backgroundMusic
                                .getControl(FloatControl.Type.MASTER_GAIN);
                        setVolumeViaControl(volumeControl, backgroundMusicVolume--);
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

    private static void loopMusic(String filename) {
        final AudioInputStream ais = getAudioInputStreamFromFile(filename);

        try {
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(ais);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }

        FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
        setVolumeViaControl(volumeControl, backgroundMusicVolume);

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

    private static void setVolumeViaControl(FloatControl volumeControl, int volume) {
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
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();

        // use volumePerc to determine new volume level
        float newVolume = min + (max - min) * dbPercentage / 100f;

        // use volumecontrol to set new volume
        volumeControl.setValue(newVolume);
    }

}