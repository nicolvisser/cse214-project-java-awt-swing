
public class VolumeTest {

    public static void main(String[] args) throws InterruptedException {

        for (int vol = 100; vol >= 0; vol -= 10) {
            StdAudio.play("resources/audio/Explosion+1.wav", vol);
            Thread.sleep(150);
        }

        for (int vol = 0; vol <= 100; vol += 10) {
            StdAudio.play("resources/audio/Explosion+1.wav", vol);
            Thread.sleep(150);
        }

    }
}