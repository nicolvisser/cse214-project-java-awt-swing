import java.awt.Graphics2D;
import geom.*;

public class Missile extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_RADIUS = vmin / 100;

    private static final int DEFAULT_SPEED = 10;

    public enum MissileState {
        ALIVE, DEAD;
    }

    MissileState state = MissileState.ALIVE;

    public final DefaultCritter owner;

    public Missile(Vector2D position, Vector2D direction, DefaultCritter owner) {
        super(position.x, position.y, DEFAULT_RADIUS, direction.getBearing());
        velocity = direction.normalize().scale(DEFAULT_SPEED);
        this.owner = owner;
    }

    @Override
    public void draw(Graphics2D g) {
        switch (state) {
            case ALIVE:
                if (getCollisionShape().intersects(getCanvasRect())) {
                    super.draw(g);
                } else {
                    state = MissileState.DEAD;
                }
                break;

            default:
                break;
        }
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

}