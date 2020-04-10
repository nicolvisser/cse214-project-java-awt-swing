import java.awt.Graphics2D;

import javax.swing.ImageIcon;

public class Enemy extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    private static final ImageIcon IMAGE_ICON_SINGLE_ENEMY = new ImageIcon("resources/enemy.png");

    public enum EnemyState {
        ALIVE, EXPLODING, DEAD;
    }

    public EnemyState state = EnemyState.ALIVE;

    private AnimatedImage explosion = new AnimatedImage("resources/redExplosion", "png", 17,
            AnimatedImage.AnimationType.ONCE);
    private double explosionOrientation = Math.random() * 3 * Math.PI;

    public Enemy(double x, double y, double radius, double orientation) {
        super(x, y, radius, orientation);

        //// angularVelocity = 0.1 + Math.random() / 10;
    }

    public void explode() {
        StdAudio.play("resources/Explosion+1.wav");
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
        if (InvadersFrame.DEBUG)
            super.draw(g2);
        // <-------------------------------------------
    }

    @Override
    public void update() {
        super.updateRotation();

        if (explosion.isComplete) {
            state = EnemyState.DEAD;
        }
    }

    @Override
    public boolean isCollidingWith(DefaultCritter critter) {
        if (critter instanceof Missile) {
            return isCollidingWith((Missile) critter);
        } else {
            return super.isCollidingWith(critter);
        }
    }

    public boolean isCollidingWith(Missile missile) {
        return missile.isCollidingWith(this); // reuse code from missile class
    }

    @Override
    public void handleCollisionWith(DefaultCritter critter) {
        if (critter instanceof Missile) {
            handleCollisionWith((Missile) critter);
        } else {
            super.handleCollisionWith(critter);
        }
    }

    public void handleCollisionWith(Missile missile) {
        missile.handleCollisionWith(this); // reuse code from missile class
    }

    @Override
    public boolean mayBeRemoved() {
        return state == EnemyState.DEAD;
    }

}