import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import geom.Vector2D;

public class InvaderGameState extends JComponent {

    private static final long serialVersionUID = 1L;

    private int pWidth, pHeight;

    public boolean pauseFlag = false;
    public boolean gameOverFlag = false;

    private Shooter shooter;
    private EnemyGroup enemyGroup;
    private ArrayList<Bunker> bunkers = new ArrayList<>();

    public InvaderGameState(int w, int h, int[] keyCodes) {
        // before adding critters override canvas size --- NOT SURE IF THIS IS IDEAL
        DefaultCritter.setCanvasSize(w, h);

        this.pWidth = w;
        this.pHeight = h;

        shooter = new Shooter();
        add(shooter); // add shooter JComponent to link key bindings

        enemyGroup = new EnemyGroup(0.2 * w, 0.15 * h, 0.4 * w, 0.3 * h, 6, 4, shooter);
        add(enemyGroup);
        add(enemyGroup);

        bunkers.add(new Bunker(0.25 * w, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
        bunkers.add(new Bunker(0.50 * w, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
        bunkers.add(new Bunker(0.75 * w, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));

        setKeyBindings(keyCodes);
    }

    public void draw(Graphics2D g2) {
        shooter.draw(g2);
        enemyGroup.draw(g2);

        for (Bunker bunker : bunkers)
            bunker.draw(g2);

        shooter.drawAimLine(g2, enemyGroup, bunkers);

        // HUD

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

        removeDeadCritters(shooter.missiles);
        removeDeadCritters(enemyGroup.enemies);
        removeDeadCritters(enemyGroup.missiles);
        removeDeadCritters(bunkers);

        checkAndHandleCollisions(shooter.missiles, enemyGroup.enemies);
        checkAndHandleCollisions(shooter, enemyGroup.missiles);
        checkAndHandleCollisions(bunkers, shooter.missiles);
        checkAndHandleCollisions(bunkers, enemyGroup.missiles);

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

    private void setKeyBindings(int[] keyCodes) {
        // Special thanks to https://www.youtube.com/watch?v=LNizNHaRV84&t=1484s
        // https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        KeyStroke pauseScreenKeyPress = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        inputMap.put(pauseScreenKeyPress, "pauseScreenKeyPress");
        actionMap.put("pauseScreenKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                pauseFlag = true;
            }
        });

        KeyStroke moveLeftKeyPress = KeyStroke.getKeyStroke(keyCodes[0], 0, false);
        inputMap.put(moveLeftKeyPress, "moveLeftKeyPress");
        actionMap.put("moveLeftKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                shooter.isLeftThrusterActive = true;
            }

        });

        KeyStroke moveLeftKeyRelease = KeyStroke.getKeyStroke(keyCodes[0], 0, true);
        inputMap.put(moveLeftKeyRelease, "moveLeftKeyRelease");
        actionMap.put("moveLeftKeyRelease", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                shooter.isLeftThrusterActive = false;
            }

        });

        KeyStroke moveRightKeyPress = KeyStroke.getKeyStroke(keyCodes[1], 0, false);
        inputMap.put(moveRightKeyPress, "moveRightKeyPress");
        actionMap.put("moveRightKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                shooter.isRightThrusterActive = true;
            }
        });

        KeyStroke moveRightKeyRelease = KeyStroke.getKeyStroke(keyCodes[1], 0, true);
        inputMap.put(moveRightKeyRelease, "moveRightKeyRelease");
        actionMap.put("moveRightKeyRelease", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                shooter.isRightThrusterActive = false;
            }
        });

        KeyStroke rotateLeftKeyPress = KeyStroke.getKeyStroke(keyCodes[2], 0, false);
        inputMap.put(rotateLeftKeyPress, "rotateLeftKeyPress");
        actionMap.put("rotateLeftKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                shooter.isRotatingLeft = true;
            }

        });

        KeyStroke rotateLeftKeyRelease = KeyStroke.getKeyStroke(keyCodes[2], 0, true);
        inputMap.put(rotateLeftKeyRelease, "rotateLeftKeyRelease");
        actionMap.put("rotateLeftKeyRelease", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                shooter.isRotatingLeft = false;
            }

        });

        KeyStroke rotateRightKeyPress = KeyStroke.getKeyStroke(keyCodes[3], 0, false);
        inputMap.put(rotateRightKeyPress, "rotateRightKeyPress");
        actionMap.put("rotateRightKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                shooter.isRotatingRight = true;
            }
        });

        KeyStroke rotateRightKeyRelease = KeyStroke.getKeyStroke(keyCodes[3], 0, true);
        inputMap.put(rotateRightKeyRelease, "rotateRightKeyRelease");
        actionMap.put("rotateRightKeyRelease", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                shooter.isRotatingRight = false;
            }
        });

        KeyStroke shootKeyRelease = KeyStroke.getKeyStroke(keyCodes[4], 0, true);
        inputMap.put(shootKeyRelease, "shootKeyRelease");
        actionMap.put("shootKeyRelease", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                shooter.shootMissile();
            }
        });
    }

    public Vector2D getVelocityForBackground() {
        return shooter.velocity;
    }
}