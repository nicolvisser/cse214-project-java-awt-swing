import java.awt.Graphics2D;
import java.util.ArrayList;

import geom.Rectangle;
import geom.Shape;
import geom.Vector2D;
import geom.Ray;

public class EnemyGroup implements Collidable, Disposable {

    private static final int vw = GlobalSettings.vw;
    private static final int vh = GlobalSettings.vh;
    private static final int vmin = GlobalSettings.vmin;
    //// private static final int vmax = GlobalSettings.vmax;

    private static final int MOVEMENT_BOUNDARY_XMIN = vw * 5 / 100;
    private static final int MOVEMENT_BOUNDARY_XMAX = vw * 95 / 100;

    public static final double DEFAULT_MOVEMENT_SPEED = 0.002 * vmin;
    private static final int DEFAULT_MOVE_DOWN_TIME = 10;

    private static final double POWERUP_SPAWN_PROBABILITY = 0.1;

    private enum MoveState {
        LEFT, RIGHT, DOWN_BEFORE_LEFT, DOWN_BEFORE_RIGHT;
    }

    private MoveState moveState;
    int moveDownTimer = 0;

    public ArrayList<Enemy> enemies = new ArrayList<>();
    private int lastNumberOfEnemies = 0;

    public ArrayList<Missile> missiles = new ArrayList<>();
    private DefaultCritter target;
    private static final long DEFAULT_SHOOT_INTERVAL = 2000;
    private long counterAttackTimer = 0;

    private PowerUpManager powerUpManagerRef;

    Vector2D position, velocity;
    double width, height;

    public EnemyGroup(double x, double y, double width, double height, int numEnemiesInRow, int numEnemiesInCol,
            DefaultCritter target) {

        position = new Vector2D(x, y);
        this.width = width;
        this.height = height;
        this.target = target;

        double xmin = x - width / 2;
        double xmax = x + width / 2;
        double ymin = y - height / 2;
        double ymax = y + height / 2;

        double r = Enemy.DEFAULT_COLLISION_RADIUS;

        double xSpacing = (width - 2 * r * numEnemiesInRow) / (numEnemiesInRow - 1);
        double ySpacing = (height - 2 * r * numEnemiesInCol) / (numEnemiesInCol - 1);

        for (double eX = xmin + r; eX < xmax; eX += xSpacing + 2 * r) {
            for (double eY = ymin + r; eY < ymax; eY += ySpacing + 2 * r) {
                enemies.add(new Enemy(eX, eY, r, Math.PI));
                lastNumberOfEnemies++;
            }
        }

        if (x < vw / 2) {
            moveState = MoveState.RIGHT;
            velocity = new Vector2D(DEFAULT_MOVEMENT_SPEED, 0);
        } else {
            moveState = MoveState.LEFT;
            velocity = new Vector2D(-DEFAULT_MOVEMENT_SPEED, 0);
        }
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

        xmin -= Enemy.DEFAULT_COLLISION_RADIUS;
        xmax += Enemy.DEFAULT_COLLISION_RADIUS;
        ymin -= Enemy.DEFAULT_COLLISION_RADIUS;
        ymax += Enemy.DEFAULT_COLLISION_RADIUS;

        width = xmax - xmin;
        height = ymax - ymin;
        position.x = xmin + width / 2;
        position.y = ymin + height / 2;
    }

    public void shootMissile() {
        StdAudio.play("resources/heartbeat.wav", GlobalSettings.volume);
        int i = (int) (Math.random() * enemies.size());
        Enemy randomEnemy = enemies.get(i);
        Vector2D pos = new Vector2D(randomEnemy.position.x, randomEnemy.position.y);
        Vector2D dir = target.positionRelativeTo(randomEnemy).normalize();
        Missile missile = new Missile(pos, dir, this);
        missiles.add(missile);
    }

    public void draw(Graphics2D g2) {

        for (Enemy enemy : enemies) {
            enemy.draw(g2);
        }

        for (Missile missile : missiles) {
            missile.draw(g2);
        }

        // Show Collision Boundary if Debugging: --->>
        if (GlobalSettings.DEBUG)
            getCollisionShape().draw(g2);
        // <-------------------------------------------
    }

    public void update(int dt) {

        // if meanwhile an enemy has died, recalculate collision boundary of group
        if (lastNumberOfEnemies != enemies.size() && enemies.size() > 0) {
            recalculateCollisionShape();
            lastNumberOfEnemies = enemies.size();
        }

        if (enemies.size() > 0) {
            counterAttackTimer += dt;
            if (counterAttackTimer > DEFAULT_SHOOT_INTERVAL) {
                shootMissile();
                counterAttackTimer = 0;
            }
        }

        // increase velocity over time
        velocity = velocity.scale(1.0005);

        // calculate how much group center will translate and store
        double dx = velocity.x;
        double dy = velocity.y;

        // update center point position
        position.x += dx;
        position.y += dy;

        // update each child with same translation
        for (Enemy enemy : enemies) {
            enemy.position.x += dx;
            enemy.position.y += dy;
            enemy.update(dt);
            enemy.lookAt(target);
        }

        for (Missile missile : missiles) {
            missile.update(dt);
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
                velocity = new Vector2D(-velocity.magnitude(), 0);
                break;

            case RIGHT:
                moveState = newState;
                velocity = new Vector2D(velocity.magnitude(), 0);
                break;

            case DOWN_BEFORE_LEFT:
            case DOWN_BEFORE_RIGHT:
                moveState = newState;
                velocity = new Vector2D(0, velocity.magnitude());
                moveDownTimer = DEFAULT_MOVE_DOWN_TIME;

                break;

            default:
                break;
        }
    }

    // creates link for one way communication between this EnemyGroup object and the
    // powerUpManagerObject
    public void createReferenceFor(PowerUpManager powerUpManager) {
        powerUpManagerRef = powerUpManager;
    }

    @Override
    public Shape getCollisionShape() {
        return new Rectangle(position.x, position.y, width, height);
    }

    @Override
    public boolean isCollidingWith(Collidable otherCollidable) {
        // first check if other collidable is actually colliding with bouding rectangle,
        // then check children and stop at first true
        if (this.getCollisionShape().intersects(otherCollidable.getCollisionShape())) {
            for (Enemy enemy : enemies) {
                if (enemy.isCollidingWith(otherCollidable)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void handleCollisionWith(Collidable otherCollidable) {
        for (Enemy enemy : enemies) {
            if (enemy.isCollidingWith(otherCollidable)) {
                if (otherCollidable instanceof Missile) {
                    Missile missile = (Missile) otherCollidable;
                    missile.explode();
                    enemy.takeDamage(Missile.DEFAULT_DAMAGE_POINTS);

                    if (missile.owner instanceof Shooter) {
                        Shooter shooter = (Shooter) missile.owner;
                        shooter.score.addPoints(Missile.DEFAULT_DAMAGE_POINTS, enemy.position);
                    }

                    if (Math.random() <= POWERUP_SPAWN_PROBABILITY) {
                        powerUpManagerRef.spawnRandomTypeAt(enemy.position);
                    }

                } else {
                    // if not colliding with a missile pass down to child class to handle
                    enemy.handleCollisionWith(otherCollidable);
                }
            }
        }
    }

    @Override
    public boolean mayBeDisposed() {
        return enemies.size() <= 0;
    }

    public boolean hasReachedBottom() {

        Ray bottomOfScreenRay = new Ray(new Vector2D(0, vh), new Vector2D(1, 0));
        return this.getCollisionShape().intersects(bottomOfScreenRay);
    }
}