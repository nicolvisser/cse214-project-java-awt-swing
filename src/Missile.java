import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import geom.Vector2D;

public class Missile extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_RADIUS = vmin / 150;

    private static final int DEFAULT_SPEED = 10;

    private static ImageIcon imgIcon;

    static {
        imgIcon = new ImageIcon("resources/bullet.png");
    }

    public enum MissileState {
        ALIVE, EXPLODING, DEAD;
    }

    MissileState state = MissileState.ALIVE;

    public final DefaultCritter owner;

    private AnimatedImage explosion = new AnimatedImage("resources/blueExplosion", "png", 17,
            AnimatedImage.AnimationType.ONCE);
    private double explosionOrientation = Math.random() * 3 * Math.PI;

    public Missile(Vector2D position, Vector2D direction, DefaultCritter owner) {
        super(position.x, position.y, DEFAULT_RADIUS, direction.getBearing());
        velocity = direction.normalize().scale(DEFAULT_SPEED);
        this.owner = owner;
    }

    public void explode() {
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
                g2.drawImage(imgIcon.getImage(), x, y, w, h, null);

                g2.rotate(-orientation, position.x, position.y);

                break;

            case EXPLODING:

                w = (int) (width * 2);
                h = (int) (height * 2);
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

}