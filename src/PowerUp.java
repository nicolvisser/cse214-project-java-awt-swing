import java.awt.Color;
import java.awt.Graphics2D;

import geom.Vector2D;

public class PowerUp extends DefaultCritter {

    private static final int vw = GlobalSettings.vw;
    private static final int vh = GlobalSettings.vh;
    private static final int vmin = GlobalSettings.vmin;
    // private static final int vmax = GlobalSettings.vmax;

    enum PowerUpType {
        BLUE, RED, GREEN, FAST_RELOAD;
    }

    enum PowerUpState {
        ALIVE, ACTIVATED, DEAD;
    }

    public static final long DEFAULT_LIFETIME_MS = 10000;
    private static final int DEFAULT_COLLISION_RADIUS = vw / 80;

    PowerUpType type;
    PowerUpState state = PowerUpState.ALIVE;

    PowerUpManager powerUpManagerRef;
    Shooter shooterRef; // stores reference to shooter once shooter obtained powerup

    long remainingLifetime_ms = DEFAULT_LIFETIME_MS;
    Color color;
    String textOnActivation = "";

    private AnimatedImage animatedPowerUpSprite;

    public PowerUp(double x, double y, PowerUpType type, PowerUpManager powerUpManagerRef) {
        super(x, y, DEFAULT_COLLISION_RADIUS, 0);

        this.type = type;
        this.powerUpManagerRef = powerUpManagerRef;

        velocity = new Vector2D(0, vh / 500f);

        String filename = "";

        switch (type) {
            case BLUE:
                filename = "resources/powerUpBlue";
                textOnActivation = "BLUE POWER!";
                color = Color.BLUE;
                break;
            case RED:
                filename = "resources/powerUpRed";
                textOnActivation = "RED POWER!";
                color = Color.RED;
                break;
            case GREEN:
                filename = "resources/powerUpGreen";
                textOnActivation = "GREEN POWER!";
                color = Color.GREEN;
                break;
            case FAST_RELOAD:
                filename = "resources/powerUpYellow";
                textOnActivation = "FAST RELOAD!";
                color = Color.YELLOW;
                break;
        }

        animatedPowerUpSprite = new AnimatedImage(filename, "png", 6, AnimatedImage.AnimationType.LOOP, 2);

    }

    public void addEffectTo(Shooter shooter) {
        switch (type) {
            case BLUE:
                break;
            case RED:
                break;
            case GREEN:
                break;
            case FAST_RELOAD:
                shooter.currentReloadTime = Shooter.DEFAULT_RELOAD_TIME / 8;
                break;
        }
    }

    public void removeEffectFromShooter() {
        switch (type) {
            case BLUE:
                break;
            case RED:
                break;
            case GREEN:
                break;
            case FAST_RELOAD:
                shooterRef.currentReloadTime = Shooter.DEFAULT_RELOAD_TIME;
                break;
        }

    }

    @Override
    public void draw(Graphics2D g2) {
        int w, h, x, y;
        switch (state) {
            case ALIVE:

                w = (int) width;
                h = (int) height;
                x = (int) (position.x - w / 2);
                y = (int) (position.y - h / 2);

                animatedPowerUpSprite.draw(g2, x, y, w, (int) h);
                break;

            case ACTIVATED:

                // draw a fading frame/border based on power up color
                int frameWidthPixels = vmin / 20;

                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                for (int i = 0; i < frameWidthPixels; i++) {
                    int a = 50 - 50 * i / frameWidthPixels;
                    g2.setColor(new Color(r, g, b, a));
                    g2.drawRect(i, i, vw - 2 * i, vh - 2 * i);
                }

                break;

            default:
                break;
        }

        // Show Collision Boundary for Debugging: --->>
        if (GlobalSettings.DEBUG)
            super.draw(g2);
        // <-------------------------------------------
    }

    @Override
    public void update() {

        switch (state) {
            case ALIVE:
                super.update();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean isCollidingWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Shooter) {
            return this.isCollidingWith((Shooter) otherCollidable);
        } else if (otherCollidable instanceof Missile) {
            return this.isCollidingWith((Missile) otherCollidable);
        }
        return super.isCollidingWith(otherCollidable);
    }

    public boolean isCollidingWith(Shooter shooter) {
        if (shooter.state == Shooter.ShooterState.ALIVE && this.state == PowerUpState.ALIVE) {
            return super.isCollidingWith(shooter);
        } else {
            return false;
        }
    }

    public boolean isCollidingWith(Missile missile) {
        if (missile.state == Missile.MissileState.ALIVE && this.state == PowerUpState.ALIVE) {
            return super.isCollidingWith(missile);
        } else {
            return false;
        }
    }

    @Override
    public void handleCollisionWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Shooter) {
            this.handleCollisionWith((Shooter) otherCollidable);
        } else if (otherCollidable instanceof Missile) {
            this.handleCollisionWith((Missile) otherCollidable);
        } else {
            super.handleCollisionWith(otherCollidable);
        }
    }

    public void handleCollisionWith(Shooter shooter) {
        powerUpManagerRef.handleNewPowerUpEquipped(this, shooter);
    }

    public void handleCollisionWith(Missile missile) {
        if (missile.owner instanceof Shooter) {
            Shooter shooter = (Shooter) missile.owner;
            powerUpManagerRef.handleNewPowerUpEquipped(this, shooter);
        }
    }

    @Override
    public boolean mayBeDisposed() {
        return state == PowerUpState.DEAD || position.y < 0;
    }

}