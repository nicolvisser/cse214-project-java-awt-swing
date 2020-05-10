import java.util.ArrayList;
import java.awt.Color;

import geom.Vector2D;

import java.awt.Graphics2D;

public class PowerUpManager implements Drawable, Updateable {

    private static final int vw = GlobalSettings.vw;
    private static final int vh = GlobalSettings.vh;
    private static final int vmin = GlobalSettings.vmin;

    private static PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values(); // cache types of powerup in array form
                                                                               // to be able to easily get random type

    private static int numTypes = types.length;
    private static TextAnimation[] textAnimationsOnActivate = new TextAnimation[numTypes];

    // where to draw status bars. e.g. for second player you might want to draw
    // powerup indicators to the right (2) of the screen
    static final int LEFT = 0;
    static final int RIGHT = 1;
    private int drawArea;

    private ArrayList<PowerUp> gamePowerUpsRef; // reference to a list of all the game's powerups unequipped and
                                                // equipped

    private Shooter owner; // reference to the owner of this powerup manager
    private PowerUp[] activePowerUps = new PowerUp[numTypes]; // array containing owner's active powerups. each entry
                                                              // represents a different type. if that entry is null then
                                                              // that type is not active

    public PowerUpManager(Shooter shooter, ArrayList<PowerUp> powerUps, int drawArea) {
        owner = shooter;
        gamePowerUpsRef = powerUps;
        this.drawArea = drawArea;
    }

    public void spawnRandomTypeAt(Vector2D position) {
        int randomIndex = (int) (Math.random() * types.length);
        PowerUp.PowerUpType randomType = types[randomIndex];

        // override for testing:
        // randomType = PowerUp.PowerUpType.FAST_RELOAD;

        gamePowerUpsRef.add(new PowerUp(position.x, position.y, randomType, this));
    }

    @Override
    public void draw(Graphics2D g2) {
        for (int i = 0; i < numTypes; i++) {

            if (activePowerUps[i] != null && activePowerUps[i].remainingLifetime_ms > 0) {

                // draw status bar to show remaining duration
                int x = drawArea == 0 ? vw * (10 + 5 * i) / 100 : vw * (90 - 5 * i) / 100;
                g2.setColor(activePowerUps[i].color);
                double percentageLifeTime = 100.0 * activePowerUps[i].remainingLifetime_ms
                        / PowerUp.DEFAULT_LIFETIME_MS;
                Utils.drawRoundStatusBar(g2, x, vh * 90 / 100, vw / 80, percentageLifeTime);

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

    @Override
    public void update(int dt) {
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

    public void handleNewPowerUpEquipped(PowerUp powerUp) {
        powerUp.state = PowerUp.PowerUpState.ACTIVATED;

        switch (powerUp.type) {
            case FAST_RELOAD:
                GameAudio.playVoiceFastReload();
                break;
            case ENERGY_REGEN:
                GameAudio.playVoiceEnergyRegen();
                break;
            case HEALTH_REGEN:
                GameAudio.playVoiceHealthRegen();
                break;
            case LASER_GUN:
                GameAudio.playVoiceLaserGun();
                break;

            default:
                break;
        }

        for (int i = 0; i < numTypes; i++) {
            if (types[i] == powerUp.type) {

                if (activePowerUps[i] == null) {
                    // IF NONE OF SAME TYPE ALREADY IN EFFECT:
                    // add new effect to shooter
                    powerUp.addEffectTo(owner);
                } else {
                    // IF SAME TYPE ALREADY IN EFFECT:
                    // ready old power up for disposal
                    activePowerUps[i].state = PowerUp.PowerUpState.DEAD;
                }

                // IN ANY CASE:
                activePowerUps[i] = powerUp;
                textAnimationsOnActivate[i] = new TextAnimation(powerUp.textOnActivation, vw * 50 / 100,
                        vh * (40 + i * 5) / 100, 2000);
            }
        }
    }

    public void handlePowerUpExpiry(PowerUp powerUp) {
        // signal powerup for disposal
        powerUp.state = PowerUp.PowerUpState.DEAD;

        // remove reference as active powerup
        for (int i = 0; i < numTypes; i++) {
            if (types[i] == powerUp.type) {
                activePowerUps[i] = null;
            }
        }

        powerUp.removeEffectFromShooter(owner);
    }
}