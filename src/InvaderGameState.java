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

    private int[] keyCodesP1, keyCodesP2;

    public final ScoreKeeper score;

    private Shooter[] shooters;
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

    public InvaderGameState(int[] keyCodesP1, int[] keyCodesP2) {
        vw = GlobalSettings.vw;
        vh = GlobalSettings.vh;

        this.keyCodesP1 = keyCodesP1;
        this.keyCodesP2 = keyCodesP2;

        score = new ScoreKeeper(vw, vh);

        Shakeable shakeFunc = () -> {
            shakeTimer = 0;
        };

        shooters = new Shooter[2];
        shooters[0] = new Shooter(score, shakeFunc);
        shooters[1] = new Shooter(score, shakeFunc);
        shooters[1].position.x += vw / 20;

        enemyGroup = null;

        bunkers.add(new Bunker(0.25 * vw, 0.7 * vh, 0.2 * vw, 0.05 * vh, 4, 16));
        bunkers.add(new Bunker(0.50 * vw, 0.7 * vh, 0.2 * vw, 0.05 * vh, 4, 16));
        bunkers.add(new Bunker(0.75 * vw, 0.7 * vh, 0.2 * vw, 0.05 * vh, 4, 16));
        for (Shooter shooter : shooters)
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
                numEnemiesInColumn + level, shooters);

        enemyGroup.shootInterval = EnemyGroup.DEFAULT_SHOOT_INTERVAL * 90 / 100;

        enemyGroup.createReferenceFor(powerUpManager); // so that powerups can spawn on enemy kill
        for (Shooter shooter : shooters)
            shooter.enemyGroupObstacle = enemyGroup; // pass shooter a reference to the new enemyGroup for laser

    }

    public void draw(Graphics2D g2) {

        g2.translate(xOffset, yOffset); // used to give screen a shake animation
        {
            for (Shooter shooter : shooters)
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

            for (Shooter shooter : shooters)
                shooter.drawAimLine(g2);

            // HUD

            score.draw(g2);

            g2.setColor(new Color(0.6f, 0.2f, 0.2f, 0.9f));
            drawStatusBar(g2, vw * 3 / 100, vh * 75 / 100, vw / 100, vh * 20 / 100, shooters[0].getHealthPercentage());

            g2.setColor(new Color(0.2f, 0.5f, 0.7f, 0.9f));
            drawStatusBar(g2, vw * 5 / 100, vh * 75 / 100, vw / 100, vh * 20 / 100, shooters[0].getEnergyPercentage());

            // TODO draw status bars for both shooters

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

        for (Shooter shooter : shooters)
            shooter.update(dt);

        if (enemyGroup != null)
            enemyGroup.update(dt);

        for (Bunker bunker : bunkers)
            bunker.update(dt);

        powerUpManager.update(dt);

        for (Shooter shooter : shooters)
            Disposable.handleDisposing(shooter.missiles);
        Disposable.handleDisposing(bunkers);
        Disposable.handleDisposing(powerUpManager.powerUps);
        if (enemyGroup != null) {
            Disposable.handleDisposing(enemyGroup.enemies);
            Disposable.handleDisposing(enemyGroup.missiles);
        }

        for (Shooter shooter : shooters) {
            Collidable.checkAndHandleCollisions(bunkers, shooter.missiles);
            Collidable.checkAndHandleCollisions(shooter, powerUpManager.powerUps);
            Collidable.checkAndHandleCollisions(shooter.missiles, powerUpManager.powerUps);
        }

        if (enemyGroup != null) {
            for (Shooter shooter : shooters) {
                Collidable.checkAndHandleCollisions(enemyGroup, shooter.missiles);
                Collidable.checkAndHandleCollisions(shooter, enemyGroup.missiles);
            }
            Collidable.checkAndHandleCollisions(bunkers, enemyGroup.missiles);
        }

        if (shooters.length == 1 && shooters[0].state == Shooter.ShooterState.DEAD) {
            gameOverFlag = true;
        } else if (shooters.length == 2 && shooters[0].state == Shooter.ShooterState.DEAD
                && shooters[1].state == Shooter.ShooterState.DEAD) {
            gameOverFlag = true;
        }

        if (enemyGroup != null) {
            boolean condition1 = enemyGroup.hasReachedBottom();
            boolean condition2 = enemyGroup.isCollidingWith(shooters[0]);
            boolean condition3 = shooters.length == 2 && enemyGroup.isCollidingWith(shooters[1]);
            if (condition1 || condition2 || condition3) {
                gameOverFlag = true;
            }
        }

    }

    public void setGameKeys(int[] keyCodes) {
        this.keyCodesP1 = keyCodes;
    }

    private class GameKeyListener extends KeyAdapter {

        GameKeyListener() {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                pauseFlag = true;
            } else if (e.getKeyCode() == keyCodesP1[0]) { // P1 Move Left
                shooters[0].isLeftThrusterActive = true;
            } else if (e.getKeyCode() == keyCodesP1[1]) { // P1 Move Right
                shooters[0].isRightThrusterActive = true;
            } else if (e.getKeyCode() == keyCodesP1[2]) { // P1 Rotate Left
                shooters[0].isRotatingLeft = true;
            } else if (e.getKeyCode() == keyCodesP1[3]) { // P1 Rotate Right
                shooters[0].isRotatingRight = true;
            } else if (e.getKeyCode() == keyCodesP1[4]) { // P1 Shoot
                shooters[0].onShootButtonPress();
            } else if (e.getKeyCode() == keyCodesP1[5]) { // P1 Block
                shooters[0].activateShield();
            } else if (e.getKeyCode() == keyCodesP2[0]) { // P2 Move Left
                shooters[1].isLeftThrusterActive = true;
            } else if (e.getKeyCode() == keyCodesP2[1]) { // P2 Move Right
                shooters[1].isRightThrusterActive = true;
            } else if (e.getKeyCode() == keyCodesP2[2]) { // P2 Rotate Left
                shooters[1].isRotatingLeft = true;
            } else if (e.getKeyCode() == keyCodesP2[3]) { // P2 Rotate Right
                shooters[1].isRotatingRight = true;
            } else if (e.getKeyCode() == keyCodesP2[4]) { // P2 Shoot
                shooters[1].onShootButtonPress();
            } else if (e.getKeyCode() == keyCodesP2[5]) { // P2 Block
                shooters[1].activateShield();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == keyCodesP1[0]) { // P1 Move Left
                shooters[0].isLeftThrusterActive = false;
            } else if (e.getKeyCode() == keyCodesP1[1]) { // P1 Move Right
                shooters[0].isRightThrusterActive = false;
            } else if (e.getKeyCode() == keyCodesP1[2]) { // P1 Rotate Left
                shooters[0].isRotatingLeft = false;
            } else if (e.getKeyCode() == keyCodesP1[3]) { // P1 Rotate Right
                shooters[0].isRotatingRight = false;
            } else if (e.getKeyCode() == keyCodesP1[4]) { // P1 Shoot
                shooters[0].onShootButtonRelease();
            } else if (e.getKeyCode() == keyCodesP1[5]) { // P1 Block
                shooters[0].deactivateShield();
            } else if (e.getKeyCode() == keyCodesP2[0]) { // P2 Move Left
                shooters[1].isLeftThrusterActive = false;
            } else if (e.getKeyCode() == keyCodesP2[1]) { // P2 Move Right
                shooters[1].isRightThrusterActive = false;
            } else if (e.getKeyCode() == keyCodesP2[2]) { // P2 Rotate Left
                shooters[1].isRotatingLeft = false;
            } else if (e.getKeyCode() == keyCodesP2[3]) { // P2 Rotate Right
                shooters[1].isRotatingRight = false;
            } else if (e.getKeyCode() == keyCodesP2[4]) { // P2 Shoot
                shooters[1].onShootButtonRelease();
            } else if (e.getKeyCode() == keyCodesP2[5]) { // P2 Block
                shooters[1].deactivateShield();
            }
        }

    }

    public Vector2D getVelocityForBackground() {
        return shooters[0].velocity; // TODO change for 2 players
    }

    public void shake() {
        shakeTimer = 0;
    }
}