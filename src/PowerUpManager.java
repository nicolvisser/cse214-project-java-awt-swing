import java.util.ArrayList;

import geom.Vector2D;

import java.awt.Graphics2D;

public class PowerUpManager {

    private static final int vw = GlobalSettings.vw;
    private static final int vh = GlobalSettings.vh;

    ArrayList<PowerUp> powerUps = new ArrayList<>();
    private PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values(); // cache types of powerup in array form to be
                                                                        // able to easily get random type

    private int numTypes = types.length;
    private PowerUp[] activePowerUps = new PowerUp[numTypes];
    private TextAnimation[] textAnimationsOnActivate = new TextAnimation[numTypes];

    private long lastTimeMillis = System.currentTimeMillis();

    public PowerUpManager() {
    }

    public void spawnRandomTypeAt(Vector2D position) {
        int randomIndex = (int) (Math.random() * types.length);
        PowerUp.PowerUpType randomType = types[randomIndex];
        powerUps.add(new PowerUp(position.x, position.y, randomType, this));
    }

    public void draw(Graphics2D g2) {
        for (PowerUp powerUp : powerUps)
            powerUp.draw(g2);

        for (int i = 0; i < numTypes; i++) {
            // draw status bar to show remaining duration
            if (activePowerUps[i] != null && activePowerUps[i].remainingLifetime_ms > 0) {
                g2.setColor(activePowerUps[i].color);
                double percentageLifeTime = 100.0 * activePowerUps[i].remainingLifetime_ms
                        / PowerUp.DEFAULT_LIFETIME_MS;
                Utils.drawRoundStatusBar(g2, vw * (10 + 5 * i) / 100, vh * 90 / 100, vw / 80, percentageLifeTime);
            }

            // draw text to show effect is activated
            if (textAnimationsOnActivate[i] != null && !textAnimationsOnActivate[i].finished) {
                g2.setColor(activePowerUps[i].color);
                Utils.scaleFont(g2, 2.0f);
                textAnimationsOnActivate[i].draw(g2);
                Utils.scaleFont(g2, 0.5f);
            }

        }
    }

    public void update() {
        for (PowerUp powerUp : powerUps)
            powerUp.update();

        long currentTimeMillis = System.currentTimeMillis();
        long deltaTimeMillis = currentTimeMillis - lastTimeMillis;
        lastTimeMillis = currentTimeMillis;

        for (int i = 0; i < numTypes; i++) {
            if (activePowerUps[i] != null) {
                PowerUp activePowerUp = activePowerUps[i];
                if (activePowerUp.remainingLifetime_ms > 0) {
                    activePowerUp.remainingLifetime_ms -= deltaTimeMillis;
                } else {
                    activePowerUp.state = PowerUp.PowerUpState.DEAD;
                    handlePowerUpExpiry(activePowerUp);
                }
            }
        }
    }

    public void handleNewPowerUpEquipped(PowerUp powerUp, Shooter shooter) {
        powerUp.shooterRef = shooter;
        powerUp.state = PowerUp.PowerUpState.ACTIVATED;

        for (int i = 0; i < numTypes; i++) {
            if (types[i] == powerUp.type) {
                // if currently no powerup of same type in effect, then add effect to shooter
                if (activePowerUps[i] == null) {
                    powerUp.addEffectTo(shooter);
                }
                // add reference to new powerup in effect
                activePowerUps[i] = powerUp;

                textAnimationsOnActivate[i] = new TextAnimation(powerUp.textOnActivation, vw * 50 / 100,
                        vh * (40 + i * 5) / 100, 1000);
            }
        }
    }

    public void handlePowerUpExpiry(PowerUp powerUp) {
        // signal powerup for garbage collection
        powerUp.state = PowerUp.PowerUpState.DEAD;

        // remove reference as active powerup
        for (int i = 0; i < numTypes; i++) {
            if (types[i] == powerUp.type) {
                activePowerUps[i] = null;
            }
        }

        powerUp.removeEffectFromShooter();

    }

    public void extendEffect() {

    }

}