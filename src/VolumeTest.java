
public class VolumeTest {

    public static void main(String[] args) throws InterruptedException {

        for (int vol = 100; vol >= 0; vol -= 10) {
            GameAudio.play("resources/audio/explosion.wav", vol);
            Thread.sleep(150);
        }

        for (int vol = 0; vol <= 100; vol += 10) {
            GameAudio.play("resources/audio/explosion.wav", vol);
            Thread.sleep(150);
        }

    }
}