import java.util.ArrayList;
import java.awt.Graphics2D;

public class PowerUpManager {

    ArrayList<PowerUp> powerUps = new ArrayList<>();

    public PowerUpManager(int pWidth, int pHeight) {
        powerUps.add(new PowerUp(0.5 * pWidth, -0.00 * pHeight, PowerUp.PowerUpType.FAST_RELOAD));
        powerUps.add(new PowerUp(0.5 * pWidth, -0.25 * pHeight, PowerUp.PowerUpType.BLUE));
        powerUps.add(new PowerUp(0.5 * pWidth, -0.50 * pHeight, PowerUp.PowerUpType.GREEN));
        powerUps.add(new PowerUp(0.5 * pWidth, -0.75 * pHeight, PowerUp.PowerUpType.RED));
    }

    public void draw(Graphics2D g2) {
        for (PowerUp powerUp : powerUps)
            powerUp.draw(g2);
    }

    public void update() {
        for (PowerUp powerUp : powerUps)
            powerUp.update();
    }

}