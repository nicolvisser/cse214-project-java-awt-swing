import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import geom.Vector2D;

public class Missile extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_RADIUS = vmin / 150;
    private static final int DEFAULT_SPEED = 10;
    public static final int DEFAULT_DAMAGE_POINTS = 50;

    private static ImageIcon imgIcnBulletBlue = new ImageIcon("resources/bullet.png");
    private static ImageIcon imgIcnBulletRed = new ImageIcon("resources/bullet_red.png");

    public enum MissileState {
        ALIVE, EXPLODING, DEAD;
    }

    MissileState state = MissileState.ALIVE;

    public final DefaultCritter owner;

    private AnimatedImage explosion;
    private double explosionOrientation = Math.random() * 3 * Math.PI;

    public Missile(Vector2D position, Vector2D direction, DefaultCritter owner) {
        super(position.x, position.y, DEFAULT_RADIUS, direction.getBearing());
        velocity = direction.normalize().scale(DEFAULT_SPEED);
        this.owner = owner;

        if (owner instanceof Shooter) {
            explosion = new AnimatedImage("resources/blueExplosion", "png", 17, AnimatedImage.AnimationType.ONCE);
        } else {
            explosion = new AnimatedImage("resources/redExplosion", "png", 17, AnimatedImage.AnimationType.ONCE);
        }

    }

    public void explode() {
        StdAudio.play("resources/Explosion+1.wav");
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
        if (InvadersFrame.DEBUG)
            super.draw(g2);
        // <-------------------------------------------
    }

    @Override
    public void update() {
        switch (state) {
            case ALIVE:
            case EXPLODING:
                super.update();

                if (state == MissileState.EXPLODING) {
                    velocity = velocity.scale(0.9);
                }

                if (!getCollisionShape().intersects(getCanvasRect())) {
                    state = MissileState.DEAD;
                }

                if (explosion.isComplete) {
                    state = MissileState.DEAD;
                }

                break;

            default:
                break;
        }
    }

    @Override
    public boolean isCollidingWith(DefaultCritter critter) {
        if (critter instanceof Enemy) {
            return isCollidingWith((Enemy) critter);
        } else {
            return super.isCollidingWith(critter);
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

    @Override
    public void handleCollisionWith(DefaultCritter critter) {
        if (critter instanceof Enemy) {
            handleCollisionWith((Enemy) critter);
        } else if (critter instanceof Shooter) {
            handleCollisionWith((Shooter) critter);
        } else {
            super.handleCollisionWith(critter);
        }
    }

    public void handleCollisionWith(Enemy enemy) {
        this.explode();
        enemy.takeDamage(DEFAULT_DAMAGE_POINTS);
    }

    public void handleCollisionWith(Shooter shooter) {
        this.explode();
        shooter.takeDamage(DEFAULT_DAMAGE_POINTS);
    }

    @Override
    public boolean mayBeRemoved() {
        return state == MissileState.DEAD;
    }

}