import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import geom.Vector2D;

public class InvaderGameState extends JComponent {

    private static final long serialVersionUID = 1L;

    public boolean pauseFlag = false;
    public boolean gameOverFlag = false;

    private int[] keyCodes;

    public final ScoreKeeper score;

    private Shooter shooter;
    private EnemyGroup enemyGroup;
    private ArrayList<Bunker> bunkers = new ArrayList<>();
    private PowerUpManager powerUpManager;

    int vw, vh;

    // VARIABLES CONCERNED WITH LEVELS ----->

    int level = 0;

    TextAnimation textAnimOnLevelStart;

    final int interLevelWaitTime = 500;
    long remainingWaitTimeBeforeLevelStarts = interLevelWaitTime;

    // vars associated with view shake animation
    double shakeTimer = Double.POSITIVE_INFINITY;
    double xOffset = 0, yOffset = 0;

    // vars associated with each level
    int enemyGroupBoxWidth = vw / 10;
    int enemyGroupBoxHEight = vw / 10;
    int numEnemiesInRow = 3;
    int numEnemiesInColumn = 3;
    int enemyShootInterval = 2000;
    int enemyHitpoints = Missile.DEFAULT_DAMAGE_POINTS; // 50
    double enemyMovementSpeedMultiplier = EnemyGroup.DEFAULT_MOVEMENT_SPEED; // 0.002 * vmin

    // <---------------------------------------

    public InvaderGameState(int[] keyCodes) {
        vw = GlobalSettings.vw;
        vh = GlobalSettings.vh;

        this.keyCodes = keyCodes;

        score = new ScoreKeeper(vw, vh);

        Shakeable shakeFunc = () -> {
            shakeTimer = 0;
        };

        shooter = new Shooter(score, shakeFunc);

        enemyGroup = null;

        bunkers.add(new Bunker(0.25 * vw, 0.7 * vh, 0.2 * vw, 0.05 * vh, 4, 16));
        bunkers.add(new Bunker(0.50 * vw, 0.7 * vh, 0.2 * vw, 0.05 * vh, 4, 16));
        bunkers.add(new Bunker(0.75 * vw, 0.7 * vh, 0.2 * vw, 0.05 * vh, 4, 16));
        shooter.bunkersObstacle = bunkers; // pass shooter a reference to bunkers list

        powerUpManager = new PowerUpManager();

        addKeyListener(new GameKeyListener());

        setFocusTraversalKeysEnabled(false);
        // <--- this is to allow TAB key to be picked up by keyListener, see
        // https://stackoverflow.com/questions/8275204/how-can-i-listen-to-a-tab-key-pressed-typed-in-java

    }

    public void startNewLevel() {
        level++;
        textAnimOnLevelStart = new TextAnimation("Level " + level, vw / 2, vh / 3, 2000);

        enemyGroup = new EnemyGroup(0.2 * vw, 0.15 * vh, 0.4 * vw, 0.3 * vh, numEnemiesInRow + level,
                numEnemiesInColumn + level, shooter);

        enemyGroup.shootInterval = EnemyGroup.DEFAULT_SHOOT_INTERVAL * 90 / 100;

        enemyGroup.createReferenceFor(powerUpManager); // so that powerups can spawn on enemy kill
        shooter.enemyGroupObstacle = enemyGroup; // pass shooter a reference to the new enemyGroup for laser

    }

    public void draw(Graphics2D g2) {

        g2.translate(xOffset, yOffset); // used to give screen a shake animation
        {
            shooter.draw(g2);

            if (enemyGroup != null)
                enemyGroup.draw(g2);

            if (textAnimOnLevelStart != null && !textAnimOnLevelStart.finished) {
                Utils.scaleFont(g2, 3f);
                g2.setColor(Color.WHITE);
                textAnimOnLevelStart.draw(g2);
                Utils.scaleFont(g2, 1 / 3f);
            }

            for (Bunker bunker : bunkers)
                bunker.draw(g2);

            powerUpManager.draw(g2);

            shooter.drawAimLine(g2);

            // HUD

            score.draw(g2);

            g2.setColor(new Color(0.6f, 0.2f, 0.2f, 0.9f));
            drawStatusBar(g2, vw * 3 / 100, vh * 75 / 100, vw / 100, vh * 20 / 100, shooter.getHealthPercentage());

            g2.setColor(new Color(0.2f, 0.5f, 0.7f, 0.9f));
            drawStatusBar(g2, vw * 5 / 100, vh * 75 / 100, vw / 100, vh * 20 / 100, 100);

        }
        g2.translate(-xOffset, -yOffset);
    }

    public void resetFlags() {
        pauseFlag = false;
        gameOverFlag = false;
    }

    public void drawStatusBar(Graphics2D g2, int x, int y, int w, int h, int perc) {
        final int SPACING = 2;

        double innerHeight = (h - 2 * SPACING) * perc / 100.0;
        double innerPositionY = y + SPACING + (h - 2 * SPACING) * (1 - perc / 100.0);

        RoundRectangle2D outer = new RoundRectangle2D.Double(x, y, w, h, w, w);
        RoundRectangle2D inner = new RoundRectangle2D.Double(x + SPACING, innerPositionY, w - 2 * SPACING, innerHeight,
                w - 2 * SPACING, w - 2 * SPACING);

        g2.draw(outer);
        g2.fill(inner);

    }

    public void update(int dt) {
        // shake screen by setting draw offset to an exponentially damped sinusoidal
        // curve for a few seconds
        if (shakeTimer < 3) {
            shakeTimer += dt / 1000.0;
            xOffset = 50 * Math.sin(20 * Math.PI * shakeTimer) * Math.exp(-10 * shakeTimer);
        }

        if (enemyGroup == null) {

            if (remainingWaitTimeBeforeLevelStarts <= 0) {
                startNewLevel();
            } else {
                remainingWaitTimeBeforeLevelStarts -= dt;
            }
        } else {
            if (enemyGroup.enemies.size() <= 0) {
                enemyGroup = null;
            }
        }

        shooter.update(dt);

        if (enemyGroup != null)
            enemyGroup.update(dt);

        for (Bunker bunker : bunkers)
            bunker.update(dt);

        powerUpManager.update(dt);

        Disposable.handleDisposing(shooter.missiles);
        Disposable.handleDisposing(bunkers);
        Disposable.handleDisposing(powerUpManager.powerUps);
        if (enemyGroup != null) {
            Disposable.handleDisposing(enemyGroup.enemies);
            Disposable.handleDisposing(enemyGroup.missiles);
        }

        Collidable.checkAndHandleCollisions(bunkers, shooter.missiles);
        Collidable.checkAndHandleCollisions(shooter, powerUpManager.powerUps);
        Collidable.checkAndHandleCollisions(shooter.missiles, powerUpManager.powerUps);
        if (enemyGroup != null) {
            Collidable.checkAndHandleCollisions(enemyGroup, shooter.missiles);
            Collidable.checkAndHandleCollisions(shooter, enemyGroup.missiles);
            Collidable.checkAndHandleCollisions(bunkers, enemyGroup.missiles);
        }

        if (shooter.state == Shooter.ShooterState.DEAD) {
            gameOverFlag = true;
        }
        if (enemyGroup != null) {
            if (enemyGroup.isCollidingWith(shooter) || enemyGroup.hasReachedBottom()) {
                gameOverFlag = true;
            }
        }

    }

    public void setGameKeys(int[] keyCodes) {
        this.keyCodes = keyCodes;
    }

    private class GameKeyListener extends KeyAdapter {

        GameKeyListener() {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                pauseFlag = true;
            } else if (e.getKeyCode() == keyCodes[0]) { // Move Left
                shooter.isLeftThrusterActive = true;
            } else if (e.getKeyCode() == keyCodes[1]) { // Move Right
                shooter.isRightThrusterActive = true;
            } else if (e.getKeyCode() == keyCodes[2]) { // Rotate Left
                shooter.isRotatingLeft = true;
            } else if (e.getKeyCode() == keyCodes[3]) { // Rotate Right
                shooter.isRotatingRight = true;
            } else if (e.getKeyCode() == keyCodes[4]) { // Shoot
                shooter.onShootButtonPress();
            } else if (e.getKeyCode() == keyCodes[5]) { // Block

            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == keyCodes[0]) { // Move Left
                shooter.isLeftThrusterActive = false;
            } else if (e.getKeyCode() == keyCodes[1]) { // Move Right
                shooter.isRightThrusterActive = false;
            } else if (e.getKeyCode() == keyCodes[2]) { // Rotate Left
                shooter.isRotatingLeft = false;
            } else if (e.getKeyCode() == keyCodes[3]) { // Rotate Right
                shooter.isRotatingRight = false;
            } else if (e.getKeyCode() == keyCodes[4]) { // Shoot
                shooter.onShootButtonRelease();
            } else if (e.getKeyCode() == keyCodes[5]) { // Block

            }
        }

    }

    public Vector2D getVelocityForBackground() {
        return shooter.velocity;
    }

    public void shake() {
        shakeTimer = 0;
    }
}