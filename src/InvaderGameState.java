import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import javax.swing.JComponent;

import geom.Vector2D;

public class InvaderGameState extends JComponent {

    private static final long serialVersionUID = 1L;

    private int pWidth, pHeight;

    public boolean pauseFlag = false;
    public boolean gameOverFlag = false;

    private int[] keyCodes;

    public final ScoreKeeper score;

    private Shooter shooter;
    private EnemyGroup enemyGroup;
    private ArrayList<Bunker> bunkers = new ArrayList<>();
    private PowerUpManager powerUpManager;

    public InvaderGameState(int w, int h, int[] keyCodes) {
        // before adding critters override canvas size --- NOT SURE IF THIS IS IDEAL
        DefaultCritter.setCanvasSize(w, h);

        this.pWidth = w;
        this.pHeight = h;

        this.keyCodes = keyCodes;

        score = new ScoreKeeper(w, h);

        shooter = new Shooter(score);

        enemyGroup = new EnemyGroup(0.2 * w, 0.15 * h, 0.4 * w, 0.3 * h, 6, 4, shooter);

        bunkers.add(new Bunker(0.25 * w, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
        bunkers.add(new Bunker(0.50 * w, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
        bunkers.add(new Bunker(0.75 * w, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));

        powerUpManager = new PowerUpManager(w, h);

        addKeyListener(new GameKeyListener());

        setFocusTraversalKeysEnabled(false);
        // <--- this is to allow TAB key to be picked up by keyListener, see
        // https://stackoverflow.com/questions/8275204/how-can-i-listen-to-a-tab-key-pressed-typed-in-java

    }

    public void draw(Graphics2D g2) {
        shooter.draw(g2);
        enemyGroup.draw(g2);

        for (Bunker bunker : bunkers)
            bunker.draw(g2);

        powerUpManager.draw(g2);

        shooter.drawAimLine(g2, enemyGroup, bunkers);

        // HUD

        score.draw(g2);

        g2.setColor(new Color(0.6f, 0.2f, 0.2f, 0.9f));
        drawStatusBar(g2, pWidth * 3 / 100, pHeight * 75 / 100, pWidth / 100, pHeight * 20 / 100,
                shooter.getHealthPercentage());

        g2.setColor(new Color(0.2f, 0.5f, 0.7f, 0.9f));
        drawStatusBar(g2, pWidth * 5 / 100, pHeight * 75 / 100, pWidth / 100, pHeight * 20 / 100, 100);

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

    public void update() {
        shooter.update();
        enemyGroup.update();

        for (Bunker bunker : bunkers)
            bunker.update();

        powerUpManager.update();

        removeDeadCritters(shooter.missiles);
        removeDeadCritters(enemyGroup.enemies);
        removeDeadCritters(enemyGroup.missiles);
        removeDeadCritters(bunkers);
        removeDeadCritters(powerUpManager.powerUps);

        checkAndHandleCollisions(shooter.missiles, enemyGroup.enemies);
        checkAndHandleCollisions(shooter, enemyGroup.missiles);
        checkAndHandleCollisions(bunkers, shooter.missiles);
        checkAndHandleCollisions(bunkers, enemyGroup.missiles);
        checkAndHandleCollisions(shooter, powerUpManager.powerUps);
        checkAndHandleCollisions(shooter.missiles, powerUpManager.powerUps);

        if (enemyGroup.enemies.size() <= 0 || shooter.state == Shooter.ShooterState.DEAD) {
            gameOverFlag = true;
        }

    }

    public void removeDeadCritters(ArrayList<? extends DefaultCritter> group) {
        Iterator<? extends DefaultCritter> critterIterator = group.iterator();
        while (critterIterator.hasNext()) {
            DefaultCritter critter = critterIterator.next();
            if (critter.mayBeRemoved()) {
                critterIterator.remove();
            }
        }
    }

    public void checkAndHandleCollisions(ArrayList<? extends DefaultCritter> group1,
            ArrayList<? extends DefaultCritter> group2) {
        try {
            for (DefaultCritter critter1 : group1) {
                for (DefaultCritter critter2 : group2) {
                    if (critter1.isCollidingWith(critter2)) {
                        critter1.handleCollisionWith(critter2);
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("===== EXCEPTION IDENTIFIED: ================================");
            e.printStackTrace();
            System.out.println("------------------------------------------------------------");
            System.out.println("Handled by skipping draw of some critters for a single frame");
            System.out.println("============================================================");
        }
    }

    public void checkAndHandleCollisions(DefaultCritter critter1, ArrayList<? extends DefaultCritter> group2) {
        try {
            for (DefaultCritter critter2 : group2) {
                if (critter1.isCollidingWith(critter2)) {
                    critter1.handleCollisionWith(critter2);
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("===== EXCEPTION IDENTIFIED: ================================");
            e.printStackTrace();
            System.out.println("------------------------------------------------------------");
            System.out.println("Handled by skipping draw of some critters for a single frame");
            System.out.println("============================================================");
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
                shooter.shootMissile();
            } else if (e.getKeyCode() == keyCodes[5]) { // Block

            }
        }

    }

    public Vector2D getVelocityForBackground() {
        return shooter.velocity;
    }
}