import java.awt.Graphics2D;
import java.util.ArrayList;

import geom.Rectangle;
import geom.Shape;
import geom.Vector2D;

public class EnemyGroup extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    private static final int DEFUALT_ENEMY_RADIUS = vmin * 2 / 100;

    private static final int MOVEMENT_BOUNDARY_XMIN = vw * 5 / 100;
    private static final int MOVEMENT_BOUNDARY_XMAX = vw * 95 / 100;

    private static final double DEFAULT_MOVEMENT_SPEED = 2;
    private static final int DEFAULT_MOVE_DOWN_TIME = 10;

    private enum MoveState {
        LEFT, RIGHT, DOWN_BEFORE_LEFT, DOWN_BEFORE_RIGHT;
    }

    private MoveState moveState;
    int moveDownTimer = 0;

    public ArrayList<Enemy> enemies = new ArrayList<>();
    private int lastNumberOfEnemies = 0;

    public EnemyGroup(double x, double y, double width, double height, int numEnemiesInRow, int numEnemiesInCol) {
        super(x, y, width, height, Math.PI);

        double xmin = x - width / 2;
        double xmax = x + width / 2;
        double ymin = y - height / 2;
        double ymax = y + height / 2;

        double r = DEFUALT_ENEMY_RADIUS;

        double xSpacing = (width - 2 * r * numEnemiesInRow) / (numEnemiesInRow - 1);
        double ySpacing = (height - 2 * r * numEnemiesInCol) / (numEnemiesInCol - 1);

        for (double eX = xmin + r; eX < xmax; eX += xSpacing + 2 * r) {
            for (double eY = ymin + r; eY < ymax; eY += ySpacing + 2 * r) {
                enemies.add(new Enemy(eX, eY, r, Math.PI));
                lastNumberOfEnemies++;
            }
        }

        if (position.x < vw / 2) {
            moveState = MoveState.RIGHT;
            velocity = new Vector2D(DEFAULT_MOVEMENT_SPEED, 0);
        } else {
            moveState = MoveState.LEFT;
            velocity = new Vector2D(-DEFAULT_MOVEMENT_SPEED, 0);
        }
    }

    @Override
    public Shape getCollisionShape() {
        Rectangle r = (Rectangle) super.getCollisionShape();
        return r;
    }

    public void recalculateCollisionShape() {

        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;

        for (Enemy enemy : enemies) {
            xmin = Math.min(xmin, enemy.position.x);
            xmax = Math.max(xmax, enemy.position.x);
            ymin = Math.min(ymin, enemy.position.y);
            ymax = Math.max(ymax, enemy.position.y);
        }

        xmin -= DEFUALT_ENEMY_RADIUS;
        xmax += DEFUALT_ENEMY_RADIUS;
        ymin -= DEFUALT_ENEMY_RADIUS;
        ymax += DEFUALT_ENEMY_RADIUS;

        this.width = xmax - xmin;
        this.height = ymax - ymin;
        this.position.x = xmin + this.width / 2;
        this.position.y = ymin + this.height / 2;
    }

    @Override
    public void draw(Graphics2D g2) {

        // Show Collision Boundary for Debugging: --->>
        if (InvadersFrame.DEBUG)
            super.draw(g2);
        // <-------------------------------------------

        for (Enemy enemy : enemies) {
            enemy.draw(g2);
        }
    }

    @Override
    public void update() {

        // if meanwhile an enemy has died, recalculate collision boundary of group
        if (lastNumberOfEnemies != enemies.size() && enemies.size() > 0) {
            recalculateCollisionShape();
            lastNumberOfEnemies = enemies.size();
        }

        // calculate how much group center will translate and store
        double dx = velocity.x + 0.5 * acceleration.x;
        double dy = velocity.y + 0.5 * acceleration.y;

        // update group position and velocity
        super.update();

        // update each child with same translation
        for (Enemy enemy : enemies) {
            enemy.position.x += dx;
            enemy.position.y += dy;
            enemy.update();
        }

        switch (moveState) {
            case LEFT:
                if (position.x - width / 2 < MOVEMENT_BOUNDARY_XMIN)
                    setState(MoveState.DOWN_BEFORE_RIGHT);
                break;

            case DOWN_BEFORE_RIGHT:
                if (moveDownTimer > 0)
                    moveDownTimer--;
                else
                    setState(MoveState.RIGHT);
                break;

            case RIGHT:
                if (position.x + width / 2 > MOVEMENT_BOUNDARY_XMAX)
                    setState(MoveState.DOWN_BEFORE_LEFT);
                break;

            case DOWN_BEFORE_LEFT:
                if (moveDownTimer > 0)
                    moveDownTimer--;
                else
                    setState(MoveState.LEFT);
                break;

            default:
                break;
        }

    }

    public void setState(MoveState newState) {
        switch (newState) {
            case LEFT:
                moveState = newState;
                velocity = new Vector2D(-DEFAULT_MOVEMENT_SPEED, 0);
                break;

            case RIGHT:
                moveState = newState;
                velocity = new Vector2D(DEFAULT_MOVEMENT_SPEED, 0);
                break;

            case DOWN_BEFORE_LEFT:
            case DOWN_BEFORE_RIGHT:
                moveState = newState;
                velocity = new Vector2D(0, DEFAULT_MOVEMENT_SPEED);
                moveDownTimer = DEFAULT_MOVE_DOWN_TIME;

                break;

            default:
                break;
        }
    }
}