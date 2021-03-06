import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.swing.ImageIcon;

import geom.BoundingShape;
import geom.Circle;
import geom.LineSegment;
import geom.Ray;
import geom.Vector2D;

public class Shooter extends DefaultCritter {

    private static final int DEFAULT_HEALTH_POINTS = 250;
    private static final int DEFAULT_ENERGY_POINTS = 100;

    private static final int vw = GameSettings.vw;
    private static final int vh = GameSettings.vh;
    private static final int vmin = GameSettings.vmin;
    private static final int vmax = GameSettings.vmax;

    private static final int DEFAULT_COLLISION_RADIUS = vmin * 3 / 100;
    private static final int DEFAULT_TURRET_RADIUS = DEFAULT_COLLISION_RADIUS * 3 / 4; // for drawing purposes

    private static final int DEFAULT_POSITION_X = vw * 50 / 100;
    private static final int DEFAULT_POSITION_Y = vh * 90 / 100;

    private static final int MOVEMENT_BOUNDARY_XMIN = vw * 5 / 100;
    private static final int MOVEMENT_BOUNDARY_XMAX = vw * 95 / 100;

    private static final double DEFAULT_THRUSTER_ACCELERATION = vmax / 2000.0;

    private static final ImageIcon IMAGE_ICON_SHIP = new ImageIcon("resources/images/carrier.png");
    private static final ImageIcon IMAGE_ICON_SHIP2 = new ImageIcon("resources/images/cargoship.png");
    private static final ImageIcon IMAGE_ICON_TURRET = new ImageIcon("resources/images/destroyer.png");
    private static final ImageIcon IMAGE_ICON_SHIELD = new ImageIcon("resources/images/shield.png");
    private int shipType = 0;

    public void changeShipType(int type) {
        shipType = type;
    }

    private AnimatedImage explosion = new AnimatedImage("resources/images/blueExplosion", "png", 17,
            AnimatedImage.AnimationType.ONCE);

    public enum ShooterState {
        ALIVE, EXPLODING, DEAD;
    }

    public ShooterState state = ShooterState.ALIVE;

    public enum ActiveWeapon {
        MISSILE_GUN, LASER_GUN;
    }

    public ActiveWeapon activeWeapon = ActiveWeapon.MISSILE_GUN;

    public double healthPoints = DEFAULT_HEALTH_POINTS;
    public double energyPoints = DEFAULT_ENERGY_POINTS;

    public double healthPointsRegenerationPerSecond = 0;
    public double energyPointsRegenerationPerSecond = 0;

    boolean isLeftThrusterActive = false;
    boolean isRightThrusterActive = false;

    private static final double DEFAULT_ANGULAR_ACCELERATION = 0.003;

    boolean isRotatingLeft = false;
    boolean isRotatingRight = false;

    public int normalReloadTime = 300; // made public and non-static so that permanant-faster-reload
                                       // PowerUp can change the default value and decrease it over time
    int currentReloadTime = normalReloadTime;
    private int reloadTimer = currentReloadTime; // ready to shoot from start

    public ArrayList<Missile> missiles = new ArrayList<>();

    public ScoreKeeper score;

    // variables to do with aim line and laser:
    EnemyGroup enemyGroupObstacle;
    ArrayList<Bunker> bunkersObstacle;
    boolean isLaserActive;
    boolean isLaserActiveOnTarget;
    private int timeSinceLastPlayedLaserSound = 0;
    private final int laserSoundDelay = 500;

    boolean isShieldActive;
    private static final int SHIELD_COLLISION_RADIUS = DEFAULT_COLLISION_RADIUS * 2;
    private static final int SHIELD_ENERGY_USAGE_INITIAL = 10;
    private static final double SHIELD_ENERGY_USAGE_PER_SECOND = 10;

    Shakeable gameScreen;

    PowerUpManager powerUpManager = null;

    public Shooter(ScoreKeeper score, Shakeable gameScreen) {
        this(DEFAULT_POSITION_X, DEFAULT_POSITION_Y, DEFAULT_COLLISION_RADIUS, score, gameScreen);
    }

    public Shooter(double x, double y, double collisionRadius, ScoreKeeper score, Shakeable gameScreen) {
        super(x, y, collisionRadius, 0);
        this.score = score;
        this.gameScreen = gameScreen;
    }

    public void takeDamage(int damagePoints) {
        healthPoints -= damagePoints;
        if (healthPoints <= 0) {
            explode();
        }
    }

    public int getHealthPercentage() {
        return (int) Math.max(0, Math.round(healthPoints * 100 / (double) DEFAULT_HEALTH_POINTS));
    }

    public int getEnergyPercentage() {
        return (int) Math.max(0, Math.round(energyPoints * 100 / (double) DEFAULT_ENERGY_POINTS));
    }

    public void onShootButtonPress() {
        switch (activeWeapon) {
            case LASER_GUN:
                isLaserActive = true;
                break;

            default:
                break;
        }
    }

    public void onShootButtonRelease() {
        switch (activeWeapon) {
            case MISSILE_GUN:
                shootMissile();
                break;
            case LASER_GUN:
                isLaserActive = false;
                break;

            default:
                break;
        }
    }

    private void shootMissile() {
        if (state == ShooterState.ALIVE) {
            if (reloadTimer >= currentReloadTime) {
                GameAudio.playSoundMissileFire();
                Missile missile = new Missile(position, lookVector(), this);
                missiles.add(missile);
                reloadTimer = 0;
            }
        }
    }

    private void handleLaserCollision() {
        if (state == ShooterState.ALIVE) {
            Object target = getAimTarget(); // TODO: maybe don't use Object class here, but create a new class or
                                            // interface to do this

            isLaserActiveOnTarget = target instanceof Enemy;

            if (isLaserActiveOnTarget) {
                Enemy enemy = (Enemy) target;
                enemy.takeDamage(7); // TODO: Fix hardcoding

                if (enemy.isHealthDepleted()) {
                    score.addPoints(Enemy.DEFAULT_HEALTH_POINTS, enemy.position);
                }

                if (timeSinceLastPlayedLaserSound > laserSoundDelay) {
                    GameAudio.playSoundBuzz();
                    timeSinceLastPlayedLaserSound = 0;
                }
            }

        }
    }

    public void explode() {
        state = ShooterState.EXPLODING;
    }

    public void activateShield() {
        if (!isShieldActive && energyPoints > SHIELD_ENERGY_USAGE_INITIAL) {
            GameAudio.playSoundShieldActivate();
            // boundingCircle.radius = SHIELD_COLLISION_RADIUS; // TODO: Extend shield
            // radius
            energyPoints -= SHIELD_ENERGY_USAGE_INITIAL;
            isShieldActive = true;
        }
    }

    public void deactivateShield() {
        if (isShieldActive) {
            GameAudio.playSoundShieldDeactivate();
            // boundingCircle.radius = DEFAULT_COLLISION_RADIUS; // TODO: Extend shield
            // radius
            isShieldActive = false;
        }
    }

    private Vector2D getTurretEndPosition() {
        return position.add(lookVector().scale(DEFAULT_TURRET_RADIUS));
    }

    // Possible future improvement: instead of explicity using enemyGroup and
    // bunkers as obstacles could make method more general and use collidable
    // interface
    public LineSegment getAimLine() {
        Vector2D start = getTurretEndPosition();

        Ray aimRay = new Ray(start, lookVector());
        double lengthOfAimLine = vw + vh; // will always extend outside frame

        if (enemyGroupObstacle != null && enemyGroupObstacle.getCollisionShape().intersects(aimRay)) {
            for (Enemy enemy : enemyGroupObstacle.enemies) {
                if (enemy.state == Enemy.EnemyState.ALIVE) {
                    Double lengthUntilCollision = aimRay.lengthUntilIntersection(enemy.getCollisionShape());
                    if (Double.isFinite(lengthUntilCollision) && lengthUntilCollision < lengthOfAimLine) {
                        lengthOfAimLine = lengthUntilCollision;
                    }
                }
            }
        }

        for (Bunker bunker : bunkersObstacle) {
            if (bunker.getCollisionShape().intersects(aimRay)) {
                for (Bunker.Block block : bunker.blocks) {
                    Double lengthUntilCollision = aimRay.lengthUntilIntersection(block.getCollisionShape());
                    if (Double.isFinite(lengthUntilCollision) && lengthUntilCollision < lengthOfAimLine) {
                        lengthOfAimLine = lengthUntilCollision;
                    }
                }
            }
        }

        return new LineSegment(start, lookVector(), lengthOfAimLine);
    }

    public Object getAimTarget() {
        Vector2D start = getTurretEndPosition();

        Ray aimRay = new Ray(start, lookVector());

        Object target = null;

        double lengthOfAimLine = vw + vh; // will always extend outside frame

        if (enemyGroupObstacle != null && enemyGroupObstacle.getCollisionShape().intersects(aimRay)) {
            for (Enemy enemy : enemyGroupObstacle.enemies) {
                if (enemy.state == Enemy.EnemyState.ALIVE) {
                    Double lengthUntilCollision = aimRay.lengthUntilIntersection(enemy.getCollisionShape());
                    if (Double.isFinite(lengthUntilCollision) && lengthUntilCollision < lengthOfAimLine) {
                        lengthOfAimLine = lengthUntilCollision;
                        target = enemy;
                    }
                }
            }
        }

        for (Bunker bunker : bunkersObstacle) {
            if (bunker.getCollisionShape().intersects(aimRay)) {
                for (Bunker.Block block : bunker.blocks) {
                    Double lengthUntilCollision = aimRay.lengthUntilIntersection(block.getCollisionShape());
                    if (Double.isFinite(lengthUntilCollision) && lengthUntilCollision < lengthOfAimLine) {
                        lengthOfAimLine = lengthUntilCollision;
                        target = block;
                    }
                }
            }
        }

        return target;
    }

    public void drawAimLine(Graphics2D g2) {
        if (state == ShooterState.ALIVE) {
            if (isLaserActive && isLaserActiveOnTarget) {
                g2.setColor(Color.GREEN);
            } else if (isLaserActive) {
                g2.setColor(new Color(0, 1, 0, 0.35f));
            } else {
                g2.setColor(new Color(0, 0.75f, 1, 0.35f));
            }
            getAimLine().draw(g2);
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        switch (state) {
            case ALIVE:

                // fine tune image position and size
                int w = (int) (width * 1.2);
                int h = (int) (height * 1.2);
                int x = (int) (position.x - w / 2);
                int y = (int) (position.y - h / 2);

                if (shipType == 0) {
                    g2.drawImage(IMAGE_ICON_SHIP.getImage(), x, y, w, h, null);
                } else {
                    g2.drawImage(IMAGE_ICON_SHIP2.getImage(), x, y, w, h, null);
                }

                g2.rotate(orientation, position.x, position.y);

                w = (int) (2 * DEFAULT_TURRET_RADIUS);
                h = (int) (2 * DEFAULT_TURRET_RADIUS);
                x = (int) (position.x - w / 2);
                y = (int) (position.y - h / 2);

                g2.drawImage(IMAGE_ICON_TURRET.getImage(), x, y, w, h, null);

                g2.rotate(-orientation, position.x, position.y);

                w = (int) (width * 2);
                h = (int) (height * 2);
                x = (int) (position.x - w / 2);
                y = (int) (position.y - h / 2);

                if (isShieldActive)
                    g2.drawImage(IMAGE_ICON_SHIELD.getImage(), x, y, 2 * SHIELD_COLLISION_RADIUS,
                            2 * SHIELD_COLLISION_RADIUS, null);

                break;

            case EXPLODING:

                w = (int) (width * 2);
                h = (int) (height * 2);
                x = (int) (position.x - w / 2);
                y = (int) (position.y - h / 2);

                explosion.draw(g2, x, y, w, h);

                break;

            default:
                break;
        }

        try {
            for (Missile missile : missiles) {
                missile.draw(g2);
            }
        } catch (ConcurrentModificationException e) {
            // handled by skipping draw for single frame
        }

        if (powerUpManager != null)
            powerUpManager.draw(g2);

        // Show Collision Boundary for Debugging: --->>
        if (GameSettings.DEBUG)
            super.draw(g2);
        // <-------------------------------------------

    }

    @Override
    public void update(int dt) {

        if (timeSinceLastPlayedLaserSound < laserSoundDelay) {
            timeSinceLastPlayedLaserSound += dt;
        }

        if (isLaserActive) {
            handleLaserCollision();
        }

        if (isShieldActive) {
            energyPoints -= SHIELD_ENERGY_USAGE_PER_SECOND * dt / 1000;
        }

        if (explosion.isComplete) {
            state = ShooterState.DEAD;
        }

        // set acceleration from thruster statuses
        if (isLeftThrusterActive && !isRightThrusterActive) {
            acceleration.x = -DEFAULT_THRUSTER_ACCELERATION;
        } else if (!isLeftThrusterActive && isRightThrusterActive) {
            acceleration.x = DEFAULT_THRUSTER_ACCELERATION;
        } else {
            acceleration.x = 0;
        }

        // if almost no 'thrust' applied or thrust applied in opposite direction than
        // movement, then slow down shooter for fast stopping or turning
        if (velocity.dot(acceleration) < 0.00001) {
            velocity = velocity.scale(0.8);
        }

        // set acceleration from thruster statuses
        if (isRotatingLeft && !isRotatingRight) {
            angularAcceleration = -DEFAULT_ANGULAR_ACCELERATION;
        } else if (!isRotatingLeft && isRotatingRight) {
            angularAcceleration = DEFAULT_ANGULAR_ACCELERATION;
        } else {
            angularAcceleration = 0;
        }

        // if almost no 'thrust' applied or thrust applied in opposite direction than
        // movement, then slow down shooter for fast stopping or turning
        if (angularVelocity * angularAcceleration < 0.00001) {
            angularVelocity = 0.8 * angularVelocity;
        }

        // update movement via super class
        super.update(dt);

        // keep player within movement boundary
        if (position.x > MOVEMENT_BOUNDARY_XMAX) {
            position.x = MOVEMENT_BOUNDARY_XMAX;
            velocity.x = 0;
            acceleration.x = 0;
        } else if (position.x < MOVEMENT_BOUNDARY_XMIN) {
            position.x = MOVEMENT_BOUNDARY_XMIN;
            velocity.x = 0;
            acceleration.x = 0;
        }

        // keep orientation in [-PI/2, PI/2] interval
        if (orientation > Math.PI / 2) {
            orientation = Math.PI / 2;
            angularVelocity = 0;
            angularAcceleration = 0;
        } else if (orientation < -Math.PI / 2) {
            orientation = -Math.PI / 2;
            angularVelocity = 0;
            angularAcceleration = 0;
        }

        reloadTimer += dt;

        for (Missile missile : missiles) {
            missile.update(dt);
        }

        if (powerUpManager != null)
            powerUpManager.update(dt);

        // health regeneration
        healthPoints = Math.min(DEFAULT_HEALTH_POINTS, healthPoints + healthPointsRegenerationPerSecond * dt / 1000.0);
        // energy regeneration
        energyPoints = Math.min(DEFAULT_ENERGY_POINTS, energyPoints + energyPointsRegenerationPerSecond * dt / 1000.0);

        Disposable.handleDisposing(missiles);
    }

    @Override
    public BoundingShape getCollisionShape() {
        if (isShieldActive) {
            return new Circle(position.x, position.y, SHIELD_COLLISION_RADIUS);
        } else {
            return super.getCollisionShape();
        }
    }

    @Override
    public boolean isCollidingWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Missile) {
            return isCollidingWith((Missile) otherCollidable);
        } else if (otherCollidable instanceof PowerUp) {
            return isCollidingWith((PowerUp) otherCollidable);
        } else {
            return super.isCollidingWith(otherCollidable);
        }
    }

    public boolean isCollidingWith(Missile missile) {
        return missile.isCollidingWith(this); // reuse code in missile class
    }

    public boolean isCollidingWith(PowerUp powerUp) {
        return powerUp.isCollidingWith(this); // reuse code in PowerUp class
    }

    @Override
    public void handleCollisionWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Missile) {
            handleCollisionWith((Missile) otherCollidable);
        } else if (otherCollidable instanceof PowerUp) {
            handleCollisionWith((PowerUp) otherCollidable);
        } else {
            super.handleCollisionWith(otherCollidable);
        }
    }

    public void handleCollisionWith(Missile missile) {
        missile.handleCollisionWith(this); // reuse code in missile class
    }

    public void handleCollisionWith(PowerUp powerUp) {
        powerUp.handleCollisionWith(this); // reuse code in PowerUp class
    }

    public void initializePowerUpManager(ArrayList<PowerUp> gamePowerUps, int drawArea) {
        this.powerUpManager = new PowerUpManager(this, gamePowerUps, drawArea);
    }

    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

}