import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import geom.Vector2D;

public class Missile extends DefaultCritter {

    private static final int vw = GlobalSettings.vw;
    private static final int vh = GlobalSettings.vh;
    private static final int vmin = GlobalSettings.vmin;
    //// private static final int vmax = GlobalSettings.vmax;

    private static final int DEFAULT_RADIUS = vmin / 150;
    private static final double DEFAULT_SPEED = 0.012 * vmin;
    public static final int DEFAULT_DAMAGE_POINTS = 50;

    private static ImageIcon imgIcnBulletBlue = new ImageIcon("resources/images/bullet.png");
    private static ImageIcon imgIcnBulletRed = new ImageIcon("resources/images/bullet_red.png");

    public enum MissileState {
        ALIVE, EXPLODING, DEAD;
    }

    MissileState state = MissileState.ALIVE;

    public final Object owner;

    private AnimatedImage explosion;
    private double explosionOrientation = Math.random() * 3 * Math.PI;

    public Missile(Vector2D position, Vector2D direction, Object owner) {
        super(position.x, position.y, DEFAULT_RADIUS, direction.getBearing());
        velocity = direction.normalize().scale(DEFAULT_SPEED);
        this.owner = owner;

        if (owner instanceof Shooter) {
            explosion = new AnimatedImage("resources/images/blueExplosion", "png", 17,
                    AnimatedImage.AnimationType.ONCE);
        } else {
            explosion = new AnimatedImage("resources/images/redExplosion", "png", 17, AnimatedImage.AnimationType.ONCE);
        }

    }

    public void explode() {
        GameAudio.playSoundExplosion();
        state = MissileState.EXPLODING;
    }

    @Override
    public void draw(Graphics2D g2) {

        int w, h, x, y;
        switch (state) {
            case ALIVE:
                g2.rotate(orientation, position.x, position.y);

                w = (int) (width * 1.5);
                h = (int) (height * 1.5);
                x = (int) (position.x - w / 2);
                y = (int) (position.y - h / 2);

                Image imgBullet = owner instanceof Shooter ? imgIcnBulletBlue.getImage() : imgIcnBulletRed.getImage();

                g2.drawImage(imgBullet, x, y, w, h, null);

                g2.rotate(-orientation, position.x, position.y);

                break;

            case EXPLODING:

                w = (int) (width * 4);
                h = (int) (height * 4);
                x = (int) (position.x - w / 2);
                y = (int) (position.y - h / 2);

                g2.rotate(explosionOrientation, position.x, position.y);
                explosion.draw(g2, x, y, w, h);
                g2.rotate(-explosionOrientation, position.x, position.y);

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
    public void update(int dt) {

        switch (state) {
            case ALIVE:

                super.update(dt);

                if (position.x < 0 || position.x > vw || position.y < 0 || position.y > vh) {
                    state = MissileState.DEAD;
                }

                break;

            case EXPLODING:

                super.update(dt);

                velocity = velocity.scale(0.9);

                if (explosion.isComplete) {
                    state = MissileState.DEAD;
                }

                break;

            default:
                break;
        }

    }

    @Override
    public boolean isCollidingWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Enemy) {
            return isCollidingWith((Enemy) otherCollidable);
        } else if (otherCollidable instanceof Shooter) {
            return isCollidingWith((Shooter) otherCollidable);
        } else if (otherCollidable instanceof Bunker) {
            return isCollidingWith((Bunker) otherCollidable);
        } else if (otherCollidable instanceof PowerUp) {
            return isCollidingWith((PowerUp) otherCollidable);
        } else {
            return super.isCollidingWith(otherCollidable);
        }
    }

    public boolean isCollidingWith(Enemy enemy) {
        if ((this.state == MissileState.ALIVE) && (enemy.state == Enemy.EnemyState.ALIVE)) {
            if (this.getCollisionShape().intersects(enemy.getCollisionShape())) {
                return true;
            }
        }
        return false;
    }

    public boolean isCollidingWith(Shooter shooter) {
        if ((this.state == MissileState.ALIVE) && (shooter.state == Shooter.ShooterState.ALIVE)) {
            if (this.getCollisionShape().intersects(shooter.getCollisionShape())) {
                return true;
            }
        }
        return false;
    }

    public boolean isCollidingWith(Bunker bunker) {
        return bunker.isCollidingWith(this); // reuse code in Bunker class
    }

    public boolean isCollidingWith(PowerUp powerUp) {
        return powerUp.isCollidingWith(this); // reuse code in PowerUp class
    }

    @Override
    public void handleCollisionWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Enemy) {
            handleCollisionWith((Enemy) otherCollidable);
        } else if (otherCollidable instanceof Shooter) {
            handleCollisionWith((Shooter) otherCollidable);
        } else if (otherCollidable instanceof Bunker) {
            handleCollisionWith((Bunker) otherCollidable);
        } else if (otherCollidable instanceof PowerUp) {
            handleCollisionWith((PowerUp) otherCollidable);
        }
    }

    public void handleCollisionWith(Enemy enemy) {
        // for game this is handled in EnemyGroup class, but for tutorial and testing it
        // gets handled here
        this.explode();
        enemy.takeDamage(Missile.DEFAULT_DAMAGE_POINTS);
    }

    public void handleCollisionWith(Shooter shooter) {
        this.explode();
        shooter.takeDamage(DEFAULT_DAMAGE_POINTS);
        if (shooter.gameScreen != null) {
            shooter.gameScreen.shake();
        }
    }

    public void handleCollisionWith(Bunker bunker) {
        bunker.handleCollisionWith(this); // reuse code in bunker class
    }

    public void handleCollisionWith(PowerUp powerUp) {
        powerUp.handleCollisionWith(this); // reuse code in PowerUp class
    }

    @Override
    public boolean mayBeDisposed() {
        return state == MissileState.DEAD;
    }

}