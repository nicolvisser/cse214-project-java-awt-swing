import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

public class Enemy extends DefaultCritter {

    private static final ImageIcon IMAGE_ICON_SINGLE_ENEMY = new ImageIcon("resources/images/enemy.png");
    public static final int DEFAULT_COLLISION_RADIUS = GameSettings.vmin * 2 / 100;

    public enum EnemyState {
        ALIVE, EXPLODING, DEAD;
    }

    public static final int DEFAULT_HEALTH_POINTS = 50;
    private int healthPoints = DEFAULT_HEALTH_POINTS;

    public EnemyState state = EnemyState.ALIVE;

    private AnimatedImage explosion = new AnimatedImage("resources/images/redExplosion", "png", 17,
            AnimatedImage.AnimationType.ONCE);
    private double explosionOrientation = Math.random() * 3 * Math.PI;

    public Enemy(double x, double y, double radius, double orientation) {
        super(x, y, radius, orientation);
    }

    public void takeDamage(int damagePoints) {
        healthPoints -= damagePoints;
        if (isHealthDepleted()) {
            explode();
        }
    }

    public void explode() {
        GameAudio.playSoundExplosion();
        state = EnemyState.EXPLODING;
    }

    @Override
    public void draw(Graphics2D g2) {

        int w, h, x, y;
        switch (state) {
            case ALIVE:

                w = (int) (width * 1.4);
                h = (int) (height * 1.4);
                x = (int) (position.x - w / 2);
                y = (int) (position.y - h / 2);

                final AffineTransform at = new AffineTransform();
                at.rotate(orientation, position.x, position.y);

                g2.rotate(orientation, position.x, position.y);
                g2.drawImage(IMAGE_ICON_SINGLE_ENEMY.getImage(), x, y, w, h, null);
                g2.rotate(-orientation, position.x, position.y);

                break;

            case EXPLODING:

                w = (int) (width * 3);
                h = (int) (height * 3);
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
        if (GameSettings.DEBUG)
            super.draw(g2);
        // <-------------------------------------------
    }

    @Override
    public void update(int dt) {
        super.updateRotation();

        if (explosion.isComplete) {
            state = EnemyState.DEAD;
        }
    }

    @Override
    public boolean isCollidingWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Missile) {
            return isCollidingWith((Missile) otherCollidable);
        } else if (otherCollidable instanceof Shooter) {
            return isCollidingWith((Shooter) otherCollidable);
        } else {
            return super.isCollidingWith(otherCollidable);
        }
    }

    public boolean isCollidingWith(Missile missile) {
        return missile.isCollidingWith(this); // reuse code from missile class
    }

    public boolean isCollidingWith(Shooter shooter) {
        return this.state == EnemyState.ALIVE && shooter.state == Shooter.ShooterState.ALIVE
                && getCollisionShape().intersects(shooter.getCollisionShape());
    }

    @Override
    public void handleCollisionWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Missile) {
            handleCollisionWith((Missile) otherCollidable);
        } else {
            super.handleCollisionWith(otherCollidable);
        }
    }

    public void handleCollisionWith(Missile missile) {
        missile.handleCollisionWith(this); // reuse code from missile class
    }

    @Override
    public boolean mayBeDisposed() {
        return state == EnemyState.DEAD;
    }

    public boolean isHealthDepleted() {
        return healthPoints <= 0;
    }

}