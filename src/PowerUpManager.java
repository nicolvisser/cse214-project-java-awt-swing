import java.util.ArrayList;
import java.awt.Color;

import geom.Vector2D;

import java.awt.Graphics2D;

public class PowerUpManager {

    private static final int vw = GlobalSettings.vw;
    private static final int vh = GlobalSettings.vh;
    private static final int vmin = GlobalSettings.vmin;

    ArrayList<PowerUp> powerUps = new ArrayList<>();
    private PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values(); // cache types of powerup in array form to be
                                                                        // able to easily get random type

    private int numTypes = types.length;
    private PowerUp[] activePowerUps = new PowerUp[numTypes];
    private TextAnimation[] textAnimationsOnActivate = new TextAnimation[numTypes];

    public PowerUpManager() {
    }

    public void spawnRandomTypeAt(Vector2D position) {
        int randomIndex = (int) (Math.random() * types.length);
        PowerUp.PowerUpType randomType = types[randomIndex];

        // override for testing:
        // randomType = PowerUp.PowerUpType.FAST_RELOAD;

        powerUps.add(new PowerUp(position.x, position.y, randomType, this));
    }

    public void draw(Graphics2D g2) {
        for (PowerUp powerUp : powerUps)
            powerUp.draw(g2);

        for (int i = 0; i < numTypes; i++) {

            if (activePowerUps[i] != null && activePowerUps[i].remainingLifetime_ms > 0) {

                // draw status bar to show remaining duration
                g2.setColor(activePowerUps[i].color);
                double percentageLifeTime = 100.0 * activePowerUps[i].remainingLifetime_ms
                        / PowerUp.DEFAULT_LIFETIME_MS;
                Utils.drawRoundStatusBar(g2, vw * (10 + 5 * i) / 100, vh * 90 / 100, vw / 80, percentageLifeTime);

                // draw text to show effect is activated
                if (textAnimationsOnActivate[i] != null && !textAnimationsOnActivate[i].finished) {
                    g2.setColor(activePowerUps[i].color);
                    Utils.scaleFont(g2, 2.0f);
                    textAnimationsOnActivate[i].draw(g2);
                    Utils.scaleFont(g2, 0.5f);
                }

                // draw a fading frame/border based on power up color
                int frameWidthPixels = vmin / 20;
                int r = activePowerUps[i].color.getRed();
                int g = activePowerUps[i].color.getGreen();
                int b = activePowerUps[i].color.getBlue();

                for (int j = 0; j < frameWidthPixels; j++) {
                    int a = 50 - 50 * j / frameWidthPixels;
                    g2.setColor(new Color(r, g, b, a));
                    g2.drawRect(j, j, vw - 2 * j, vh - 2 * j);
                }
            }

        }
    }

    public void update(int dt) {
        for (PowerUp powerUp : powerUps)
            powerUp.update(dt);

        for (int i = 0; i < numTypes; i++) {
            if (activePowerUps[i] != null) {
                PowerUp activePowerUp = activePowerUps[i];
                if (activePowerUp.remainingLifetime_ms > 0) {
                    activePowerUp.remainingLifetime_ms -= dt;
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
                        vh * (40 + i * 5) / 100, 2000);
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
}