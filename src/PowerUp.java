import java.awt.Color;
import java.awt.Graphics2D;

import geom.Vector2D;

public class PowerUp extends DefaultCritter {

    private static final int vw = GameSettings.vw;
    private static final int vh = GameSettings.vh;
    // private static final int vmin = GlobalSettings.vmin;
    // private static final int vmax = GlobalSettings.vmax;

    enum PowerUpType {
        ENERGY_REGEN, HEALTH_REGEN, LASER_GUN, FAST_RELOAD, PERMANENT_FASTER_RELOAD;
    }

    enum PowerUpState {
        ALIVE, ACTIVATED, DEAD;
    }

    public static final long DEFAULT_LIFETIME_MS = 10000;
    private static final int DEFAULT_COLLISION_RADIUS = vw / 80;

    PowerUpType type;
    PowerUpState state = PowerUpState.ALIVE;

    long remainingLifetime_ms = DEFAULT_LIFETIME_MS;
    Color color;
    String textOnActivation = "";

    private AnimatedImage animatedPowerUpSprite;

    public PowerUp(double x, double y, PowerUpType type, PowerUpManager powerUpManagerRef) {
        super(x, y, DEFAULT_COLLISION_RADIUS, 0);

        this.type = type;

        velocity = new Vector2D(0, vh / 500f);

        String filename = "";

        switch (type) {
            case ENERGY_REGEN:
                filename = "resources/images/powerUpBlue";
                textOnActivation = "ENERGY REGENERATION!";
                color = Color.BLUE;
                break;
            case HEALTH_REGEN:
                filename = "resources/images/powerUpRed";
                textOnActivation = "HEALTH REGENERATION!";
                color = Color.RED;
                break;
            case LASER_GUN:
                filename = "resources/images/powerUpGreen";
                textOnActivation = "LASER GUN! Hold to fire.";
                color = Color.GREEN;
                break;
            case FAST_RELOAD:
                filename = "resources/images/powerUpYellow";
                textOnActivation = "FAST RELOAD!";
                color = Color.YELLOW;
                break;
            case PERMANENT_FASTER_RELOAD:
                filename = "resources/images/powerUpYellow";
                textOnActivation = "PERMANENT FASTER RELOAD!";
                remainingLifetime_ms = 2000; // effect permanent thus lifetime can be short before removed from canvas
                color = Color.YELLOW;
                break;
        }

        animatedPowerUpSprite = new AnimatedImage(filename, "png", 6, AnimatedImage.AnimationType.LOOP, 2);

    }

    public void addEffectTo(Shooter shooter) {
        switch (type) {
            case ENERGY_REGEN:
                shooter.energyPointsRegenerationPerSecond = 10;
                break;
            case HEALTH_REGEN:
                shooter.healthPointsRegenerationPerSecond = 10;
                break;
            case LASER_GUN:
                shooter.activeWeapon = Shooter.ActiveWeapon.LASER_GUN;
                break;
            case FAST_RELOAD:
                shooter.currentReloadTime = shooter.normalReloadTime / 8;
                break;
            case PERMANENT_FASTER_RELOAD:
                shooter.normalReloadTime = shooter.normalReloadTime * 90 / 100; // 95 % of previous value
                shooter.currentReloadTime = shooter.normalReloadTime;
                break;
        }
    }

    public void removeEffectFromShooter(Shooter shooter) {
        switch (type) {
            case ENERGY_REGEN:
                shooter.energyPointsRegenerationPerSecond = 0;
                break;
            case HEALTH_REGEN:
                shooter.healthPointsRegenerationPerSecond = 0;
                break;
            case LASER_GUN:
                shooter.isLaserActive = false;
                shooter.isLaserActiveOnTarget = false;
                shooter.activeWeapon = Shooter.ActiveWeapon.MISSILE_GUN;
                break;
            case FAST_RELOAD:
                shooter.currentReloadTime = shooter.normalReloadTime;
                break;
            case PERMANENT_FASTER_RELOAD:
                // permanent thus don't do anything on removal
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

            default:
                break;
        }

        // Show Collision Boundary for Debugging: --->>
        if (GameSettings.DEBUG)
            super.draw(g2);
        // <-------------------------------------------
    }

    @Override
    public void update(int dt) {

        switch (state) {
            case ALIVE:
                super.update(dt);
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
        shooter.getPowerUpManager().handleNewPowerUpEquipped(this);
    }

    public void handleCollisionWith(Missile missile) {
        if (missile.owner instanceof Shooter) {
            Shooter shooter = (Shooter) missile.owner;
            shooter.getPowerUpManager().handleNewPowerUpEquipped(this);
        }
    }

    @Override
    public boolean mayBeDisposed() {
        return state == PowerUpState.DEAD || position.y < 0;
    }

}