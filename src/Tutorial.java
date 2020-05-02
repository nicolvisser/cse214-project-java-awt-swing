import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import geom.*;
import geom.Rectangle;

public class Tutorial extends JComponent {

    private static final long serialVersionUID = 1L;

    private final int vw = GlobalSettings.vw;
    private final int vh = GlobalSettings.vh;

    private int[] keyCodes;
    private String[] keyDescriptions;

    public boolean exitFlag;

    private Shooter shooter = new Shooter(null);
    private Enemy enemy = new Enemy(vw * 3 / 4, vh / 4, vh / 40, Math.PI);

    int tutorialStage = 0;

    boolean stage1Complete, stage2Complete, stage3Complete;

    Rectangle moveTarget = new Rectangle(vw / 4, shooter.position.y, shooter.width * 2, shooter.height * 2);

    public Tutorial(int[] keyCodes, String[] keyDescriptions) {
        this.keyCodes = keyCodes;
        this.keyDescriptions = keyDescriptions;

        addKeyListener(new TutorialKeyListener());
        setFocusTraversalKeysEnabled(false);
    }

    public void update(int dt) {
        shooter.update(dt);
        enemy.update(dt);

        if (!stage1Complete)
            stage1Complete = moveTarget.contains(shooter.getCollisionShape());

        stage2Complete = Double.isFinite(getLengthOfAImLine());
        stage3Complete = enemy.state == Enemy.EnemyState.DEAD;

        for (Missile missile : shooter.missiles) {
            if (missile.getCollisionShape().intersects(enemy.getCollisionShape())) {
                enemy.explode();
                missile.explode();
            }
        }

    }

    private void drawKey(Graphics2D g2, int x, int y, String keyStr) {

        int textWidth = g2.getFontMetrics().stringWidth(keyStr);
        double rectWidth = Math.max(vh * 4 / 100, textWidth * 1.5);

        Rectangle rect = new Rectangle(x, y, rectWidth, vh * 4 / 100);

        g2.setColor(Color.BLACK);
        rect.fill(g2);

        g2.setColor(Color.WHITE);
        rect.draw(g2);

        g2.setColor(Color.WHITE);
        Utils.drawCenteredText(g2, x, y, keyStr);

    }

    public void draw(Graphics2D g2) {

        g2.setColor(Color.gray);
        Utils.drawCenteredText(g2, vw / 2, vh * 25 / 100, "You can change the controls under Settings in Main Menu", 1);

        if (!stage3Complete) {
            if (!stage1Complete) {
                g2.setColor(Color.RED);
                Utils.drawCenteredText(g2, vw / 2, vh * 20 / 100,
                        "Move the Shooter into the box by using the movement keys", 2);
                drawKey(g2, vw * 45 / 100, vh * 30 / 100, keyDescriptions[0]);
                drawKey(g2, vw * 55 / 100, vh * 30 / 100, keyDescriptions[1]);
                moveTarget.draw(g2);
            } else {
                enemy.draw(g2);

                if (!stage2Complete) {
                    g2.setColor(Color.ORANGE);
                    Utils.drawCenteredText(g2, vw / 2, vh * 20 / 100, "Aim at the enemy using the aim keys", 2);
                    drawKey(g2, vw * 45 / 100, vh * 30 / 100, keyDescriptions[2]);
                    drawKey(g2, vw * 55 / 100, vh * 30 / 100, keyDescriptions[3]);
                } else {
                    g2.setColor(Color.YELLOW);
                    Utils.drawCenteredText(g2, vw / 2, vh * 20 / 100, "Shoot at the enemy with the shoot key", 2);
                    drawKey(g2, vw * 50 / 100, vh * 30 / 100, keyDescriptions[4]);
                }
            }
        } else {
            g2.setColor(Color.GREEN);
            Utils.drawCenteredText(g2, vw / 2, vh * 20 / 100,
                    "You now know the basics. Press escape and start a new game.", 2);
        }

        drawAimLine(g2);
        shooter.draw(g2);

    }

    private class TutorialKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                exitFlag = true;
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

    private double getLengthOfAImLine() {
        Ray aimRay = new Ray(shooter.position, shooter.lookVector());
        return aimRay.lengthUntilIntersection(enemy.getCollisionShape());
    }

    private LineSegment getAimLine() {
        Vector2D start = shooter.position;
        double lengthOfAimLine = vw + vh; // will always extend outside frame
        double lengthUntilCollision = getLengthOfAImLine();
        if (Double.isFinite(lengthUntilCollision)) {
            lengthOfAimLine = lengthUntilCollision;
        }
        return new LineSegment(start, shooter.lookVector(), lengthOfAimLine);
    }

    private void drawAimLine(Graphics2D g2) {
        g2.setColor(new Color(0, 0.75f, 1, 0.35f));
        getAimLine().draw(g2);
    }

}