
/******************************************************************************
 *  
 *  - A stripped down and modified version of Princeton's StdAudio Class
 *  - Simple library for playing .wav files.
 *
 *
 *  Limitations
 *  -----------
 *    - Assumes the audio is monaural, little endian, with sampling rate
 *      of 44,100
 *    - check when reading .wav files from a .jar file ?
 *
 ******************************************************************************/

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A stripped down and modified version of Princeton's StdAudio Class
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class StdAudio {

    /**
     * The sample rate: 44,100 Hz for CD quality audio.
     */
    public static final int SAMPLE_RATE = 44100;

    private static final int BYTES_PER_SAMPLE = 2; // 16-bit audio
    private static final int BITS_PER_SAMPLE = 16; // 16-bit audio
    private static final int SAMPLE_BUFFER_SIZE = 4096;

    private static final int MONO = 1;
    private static final boolean LITTLE_ENDIAN = false;
    private static final boolean SIGNED = true;

    private static SourceDataLine line; // to play the sound

    private StdAudio() {
        // can not instantiate
    }

    // static initializer
    static {
        init();
    }

    // open up an audio stream
    private static void init() {
        try {
            // 44,100 Hz, 16-bit audio, mono, signed PCM, little endian
            AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, MONO, SIGNED, LITTLE_ENDIAN);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);

        } catch (LineUnavailableException e) {
            System.out.println(e.getMessage());
        }

        // no sound gets made before this call
        line.start();
    }

    // get an AudioInputStream object from a file
    private static AudioInputStream getAudioInputStreamFromFile(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("filename is null");
        }

        try {
            // first try to read file from local file system
            File file = new File(filename);
            if (file.exists()) {
                return AudioSystem.getAudioInputStream(file);
            }

            // resource relative to .class file
            InputStream is1 = StdAudio.class.getResourceAsStream(filename);
            if (is1 != null) {
                return AudioSystem.getAudioInputStream(is1);
            }

            // resource relative to classloader root
            InputStream is2 = StdAudio.class.getClassLoader().getResourceAsStream(filename);
            if (is2 != null) {
                return AudioSystem.getAudioInputStream(is2);
            }

            // give up
            else {
                throw new IllegalArgumentException("could not read '" + filename + "'");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("could not read '" + filename + "'", e);
        } catch (UnsupportedAudioFileException e) {
            throw new IllegalArgumentException("file of unsupported audio format: '" + filename + "'", e);
        }
    }

    /**
     * Closes standard audio.
     */
    public static void close() {
        line.drain();
        line.stop();
    }

    /**
     * Plays an audio file (in .wav, .mid, or .au format) in a background thread.
     *
     * @param filename the name of the audio file
     * @throws IllegalArgumentException if unable to play {@code filename}
     * @throws IllegalArgumentException if {@code filename} is {@code null}
     */
    public static synchronized void play(final String filename) {
        play(filename, 100);
    }

    public static synchronized void play(final String filename, int loudnessPerc) {
        new Thread(new Runnable() {
            public void run() {
                AudioInputStream ais = getAudioInputStreamFromFile(filename);
                stream(ais, loudnessPerc);
            }
        }).start();
    }

    // https://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
    // play a wav or aif file
    // javax.sound.sampled.Clip fails for long clips (on some systems), perhaps
    // because
    // JVM closes (see remedy in loop)

    // private static void stream(AudioInputStream ais) {
    // stream(ais, 100);
    // }

    private static void stream(AudioInputStream ais, int loudnessPerc) {
        SourceDataLine line = null;
        int BUFFER_SIZE = 4096; // 4K buffer

        try {
            AudioFormat audioFormat = ais.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

            line = (SourceDataLine) AudioSystem.getLine(info);

            line.open(audioFormat);

            FloatControl volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);

            // loundnessPerc is a measure for perceived loudness of audio. (100 is
            // full loudness and 50 is perceived as half as loud). Note this is approximate
            // and subjective!!!!
            //
            // volumePerc corresponds to range of decibels of audio line as retreived from
            // FloatControl MASTER_GAIN type: (100 is max volume in decibels, whereas 50 is
            // half the range of decibels i.e. midway between max and min)
            //
            // assume that 'half as load' corresponds to 10 percentage points of decibel
            // range
            //
            // now, map perceived loudness percentage to volume percentage ->
            float volumePerc;
            if (loudnessPerc <= 0.01) {
                volumePerc = 0;
            } else {
                volumePerc = (float) (100 + 10 * (Math.log(loudnessPerc / 100f) / Math.log(2)));
            }

            // ensure volumePerc is between 0 and 100 after mapping
            volumePerc = Math.min(volumePerc, 100);
            volumePerc = Math.max(volumePerc, 0);

            // use volumePerc to determine new volumne level
            float newVolumne = volumeControl.getMinimum()
                    + (volumeControl.getMaximum() - volumeControl.getMinimum()) * volumePerc / 100f;

            // set line volume to this volume
            volumeControl.setValue(newVolumne);

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

    /**
     * Loops an audio file (in .wav, .mid, or .au format) in a background thread.
     *
     * @param filename the name of the audio file
     * @throws IllegalArgumentException if {@code filename} is {@code null}
     */
    public static synchronized void loop(String filename) {
        if (filename == null)
            throw new IllegalArgumentException();

        final AudioInputStream ais = getAudioInputStreamFromFile(filename);

        try {
            Clip clip = AudioSystem.getClip();
            // Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // keep JVM open
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}