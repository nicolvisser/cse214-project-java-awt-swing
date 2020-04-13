import java.awt.Graphics2D;

public class PowerUp extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    enum PowerUpType {
        BLUE, RED, GREEN, FAST_RELOAD;
    }

    enum PowerUpState {
        ALIVE, ACTIVATED, DEAD;
    }

    private static final long DEFAULT_LIFETIME_MS = 10000;
    private static final int DEFAULT_COLLISION_RADIUS = vw / 100;

    public PowerUpState state = PowerUpState.ALIVE;

    private PowerUpType type;
    private long lastTimeMillis = System.currentTimeMillis();
    private long remainingLifetime_ms = DEFAULT_LIFETIME_MS;
    private Shooter shooter;
    private AnimatedImage animatedPowerUpSprite;

    // private Circle boundingCircle;

    public PowerUp(double x, double y, PowerUpType type) {
        super(x, y, DEFAULT_COLLISION_RADIUS, 0);

        this.type = type;

        String filename = "";
        switch (type) {
            case BLUE:
                filename = "resources/powerUpBlue";
                break;
            case RED:
                filename = "resources/powerUpRed";
                break;
            case GREEN:
                filename = "resources/powerUpGreen";
                break;
            case FAST_RELOAD:
                filename = "resources/powerUpYellow";
                break;
        }
        animatedPowerUpSprite = new AnimatedImage(filename, "png", 6, AnimatedImage.AnimationType.LOOP);
    }

    @Override
    public void draw(Graphics2D g2) {
        switch (state) {
            case ALIVE:
                animatedPowerUpSprite.draw(g2, (int) position.x, (int) position.y, (int) width, (int) height);
                break;

            default:
                break;
        }

        // Show Collision Boundary for Debugging: --->>
        if (InvadersFrame.DEBUG)
            super.draw(g2);
        // <-------------------------------------------
    }

    @Override
    public void update() {

        switch (state) {
            case ALIVE:
                super.update();
                break;

            case ACTIVATED:
                long currentTimeMillis = System.currentTimeMillis();
                remainingLifetime_ms -= (currentTimeMillis - lastTimeMillis);
                lastTimeMillis = currentTimeMillis;
                if (remainingLifetime_ms < 0) {
                    deactivateEffect();
                }
                break;

            default:
                break;
        }
    }

    public void addEffectTo(Shooter shooter) {
        this.shooter = shooter;

        lastTimeMillis = System.currentTimeMillis();

        switch (type) {
            case BLUE:
                break;
            case RED:
                break;
            case GREEN:
                break;
            case FAST_RELOAD:
                shooter.currentReloadTime = Shooter.DEFAULT_RELOAD_TIME / 8;
                System.out.println("fast reload time: " + shooter.currentReloadTime);
                break;
        }

        state = PowerUpState.ACTIVATED;
    }

    public void deactivateEffect() {

        switch (type) {
            case BLUE:
                break;
            case RED:
                break;
            case GREEN:
                break;
            case FAST_RELOAD:
                shooter.currentReloadTime = Shooter.DEFAULT_RELOAD_TIME;
                System.out.println("default reload time: " + shooter.currentReloadTime);
                break;
        }

        state = PowerUpState.DEAD;
    }

    @Override
    public boolean isCollidingWith(DefaultCritter critter) {
        if (critter instanceof Shooter) {
            return this.isCollidingWith((Shooter) critter);
        } else if (critter instanceof Missile) {
            return this.isCollidingWith((Missile) critter);
        }
        return super.isCollidingWith(critter);
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
    public void handleCollisionWith(DefaultCritter critter) {
        if (critter instanceof Shooter) {
            this.handleCollisionWith((Shooter) critter);
        } else if (critter instanceof Missile) {
            this.handleCollisionWith((Missile) critter);
        } else {
            super.handleCollisionWith(critter);
        }
    }

    public void handleCollisionWith(Shooter shooter) {
        addEffectTo(shooter);
    }

    public void handleCollisionWith(Missile missile) {
        if (missile.owner instanceof Shooter) {
            Shooter shooter = (Shooter) missile.owner;
            addEffectTo(shooter);
        }
    }

    @Override
    public boolean mayBeRemoved() {
        return state == PowerUpState.DEAD;
    }
}