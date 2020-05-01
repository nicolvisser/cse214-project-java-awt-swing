import java.util.ArrayList;

import geom.Vector2D;

import java.awt.Graphics2D;

public class PowerUpManager {

    ArrayList<PowerUp> powerUps = new ArrayList<>();
    private PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values(); // cache types of powerup in array form to be
                                                                        // able to easily get random type

    public PowerUpManager() {
    }

    public void spawnRandomTypeAt(Vector2D position) {
        int randomIndex = (int) (Math.random() * types.length);
        PowerUp.PowerUpType randomType = types[randomIndex];
        powerUps.add(new PowerUp(position.x, position.y, randomType));
    }

    public void draw(Graphics2D g2) {
        for (PowerUp powerUp : powerUps)
            powerUp.draw(g2);
    }

    public void update() {
        // TODO check if out of bounds and kill off

        for (PowerUp powerUp : powerUps)
            powerUp.update();
    }

}