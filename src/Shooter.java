import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import javax.swing.ImageIcon;

import geom.LineSegment;
import geom.Ray;
import geom.Vector2D;

public class Shooter extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_HEALTH_POINTS = 250;
    private static final int DEFAULT_ENERGY_POINTS = 100;

    private static final int DEFAULT_WIDTH = vmin * 8 / 100;
    private static final int DEFAULT_HEIGHT = vmin * 12 / 100;

    private static final int DEFAULT_TURRET_WIDTH = DEFAULT_WIDTH * 3 / 4;
    private static final int DEFAULT_TURRET_HEIGHT = DEFAULT_HEIGHT * 3 / 4;

    private static final int DEFAULT_POSITION_X = vw * 50 / 100;
    private static final int DEFAULT_POSITION_Y = vh * 90 / 100;

    private static final int MOVEMENT_BOUNDARY_XMIN = vw * 5 / 100;
    private static final int MOVEMENT_BOUNDARY_XMAX = vw * 95 / 100;

    private static final double DEFAULT_THRUSTER_ACCELERATION = 0.5;

    private static final ImageIcon IMAGE_ICON_SHIP = new ImageIcon("resources/carrier.png");
    private static final ImageIcon IMAGE_ICON_TURRET = new ImageIcon("resources/destroyer.png");

    private AnimatedImage explosion = new AnimatedImage("resources/blueExplosion", "png", 17,
            AnimatedImage.AnimationType.ONCE);

    public enum ShooterState {
        ALIVE, EXPLODING, DEAD;
    }

    public ShooterState state = ShooterState.ALIVE;

    public int healthPoints = DEFAULT_HEALTH_POINTS;
    public int energyPoints = DEFAULT_ENERGY_POINTS;

    boolean isLeftThrusterActive = false;
    boolean isRightThrusterActive = false;

    private static final double DEFAULT_ANGULAR_ACCELERATION = 0.005;

    boolean isRotatingLeft = false;
    boolean isRotatingRight = false;

    static final int DEFAULT_RELOAD_TIME = 20; // in number of frames as unit // TODO change to time based not frame
    int currentReloadTime = DEFAULT_RELOAD_TIME;
    private int reloadTimer = currentReloadTime; // ready to shoot from start

    public ArrayList<Missile> missiles = new ArrayList<>();

    public Shooter() {
        this(DEFAULT_POSITION_X, DEFAULT_POSITION_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Shooter(double x, double y, double width, double height) {
        super(x, y, width, height, 0);
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

    public void shootMissile() {
        if (state == ShooterState.ALIVE) {
            if (reloadTimer >= currentReloadTime) {
                StdAudio.play("resources/heartbeat.wav");
                Missile missile = new Missile(position, lookVector(), this);
                missiles.add(missile);
                reloadTimer = 0;
            }
        }
    }

    public void explode() {
        state = ShooterState.EXPLODING;
    }

    // TODO: Change parameters to be more general (obstacles)
    public LineSegment getAimLine(EnemyGroup enemyGroup, ArrayList<Bunker> bunkers) {
        Vector2D start = position; // TODO: change to end of turret position

        Ray aimRay = new Ray(start, lookVector());
        double lengthOfAimLine = 2000; // TODO: fix hardcoding

        if (enemyGroup.getCollisionShape().intersects(aimRay)) {
            for (Enemy enemy : enemyGroup.enemies) {
                if (enemy.state == Enemy.EnemyState.ALIVE) {
                    Double lengthUntilCollision = aimRay.lengthUntilIntersection(enemy.getCollisionShape());
                    if (Double.isFinite(lengthUntilCollision) && lengthUntilCollision < lengthOfAimLine) {
                        lengthOfAimLine = lengthUntilCollision;
                    }
                }
            }
        }

        for (Bunker bunker : bunkers) {
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

    public void drawAimLine(Graphics2D g2, EnemyGroup enemyGroup, ArrayList<Bunker> bunkers) {
        if (state == ShooterState.ALIVE) {
            g2.setColor(new Color(0, 0.75f, 1, 0.25f));
            getAimLine(enemyGroup, bunkers).draw(g2);
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        switch (state) {
            case ALIVE:

                // fine tune image position and size to fit collisionShape
                int w = (int) (width * 1.2);
                int h = (int) (height * 1.2);
                int x = (int) (position.x - w / 2);
                int y = (int) (position.y - h / 2);

                g2.drawImage(IMAGE_ICON_SHIP.getImage(), x, y, w, h, null);

                g2.rotate(orientation, position.x, position.y);

                w = (int) (DEFAULT_TURRET_WIDTH);
                h = (int) (DEFAULT_TURRET_HEIGHT);
                x = (int) (position.x - w / 2);
                y = (int) (position.y - h / 2 - h / 8);

                g2.drawImage(IMAGE_ICON_TURRET.getImage(), x, y, w, h, null);

                g2.rotate(-orientation, position.x, position.y);

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
            System.out.println("===== EXCEPTION IDENTIFIED: ================================");
            e.printStackTrace();
            System.out.println("------------------------------------------------------------");
            System.out.println("Handled by skipping draw of missile for a single frame");
            System.out.println("============================================================");
        }

        // Show Collision Boundary for Debugging: --->>
        if (InvadersFrame.DEBUG)
            super.draw(g2);
        // <-------------------------------------------

    }

    @Override
    public void update() {

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
        if (velocity.x * acceleration.x < 0.00001) {
            velocity.x = 0.8 * velocity.x;
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
        super.update();

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

        reloadTimer++;

        for (Missile missile : missiles) {
            missile.update();
        }

        try {
            Iterator<Missile> missileIter = missiles.iterator();
            while (missileIter.hasNext()) {
                Missile missile = missileIter.next();
                missile.update();
                if (missile.state == Missile.MissileState.DEAD) {
                    missileIter.remove();
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("===== EXCEPTION IDENTIFIED: ================================");
            e.printStackTrace();
            System.out.println("------------------------------------------------------------");
            System.out.println("Handled by skipping update/removal of missile this frame");
            System.out.println("============================================================");
        }

    }

    @Override
    public boolean isCollidingWith(DefaultCritter critter) {
        if (critter instanceof Missile) {
            return isCollidingWith((Missile) critter);
        } else if (critter instanceof PowerUp) {
            return isCollidingWith((PowerUp) critter);
        } else {
            return super.isCollidingWith(critter);
        }
    }

    public boolean isCollidingWith(Missile missile) {
        return missile.isCollidingWith(this); // reuse code in missile class
    }

    public boolean isCollidingWith(PowerUp powerUp) {
        return powerUp.isCollidingWith(this); // reuse code in PowerUp class
    }

    @Override
    public void handleCollisionWith(DefaultCritter critter) {
        if (critter instanceof Missile) {
            handleCollisionWith((Missile) critter);
        } else if (critter instanceof PowerUp) {
            handleCollisionWith((PowerUp) critter);
        } else {
            super.handleCollisionWith(critter);
        }
    }

    public void handleCollisionWith(Missile missile) {
        missile.handleCollisionWith(this); // reuse code in missile class
    }

    public void handleCollisionWith(PowerUp powerUp) {
        powerUp.handleCollisionWith(this); // reuse code in PowerUp class
    }

}