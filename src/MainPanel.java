import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.event.*;

public class MainPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public ArrayList<Drawable> drawableChildren = new ArrayList<>();
    public ArrayList<Updatable> updatableChildren = new ArrayList<>();

    Shooter shooter;
    EnemyGroup enemyGroup;

    public MainPanel() {
        setLayout(null);
        setIgnoreRepaint(true);
        setKeyBindings();

        shooter = new Shooter();
        add(shooter);

        enemyGroup = new EnemyGroup(500, 500, 400, 200, 6, 3);
        add(enemyGroup);

    }

    @Override
    public Component add(Component comp) {
        if (comp instanceof Drawable) {
            drawableChildren.add((Drawable) comp);
        }
        if (comp instanceof Updatable) {
            updatableChildren.add((Updatable) comp);
        }
        return super.add(comp);
    }

    public void draw(Graphics g) {

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getSize().width, getSize().height);

        for (Drawable drawableChild : drawableChildren) {
            drawableChild.draw(g);
        }
    }

    public void update() {
        for (Updatable updatableChild : updatableChildren) {
            updatableChild.update();
        }

        Iterator<Missile> shooterMissileIterator = shooter.missiles.iterator();
        while (shooterMissileIterator.hasNext()) {
            Missile shooterMissile = shooterMissileIterator.next();
            if (shooterMissile.getCollisionShape().intersects(enemyGroup.getCollisionShape())) {
                Iterator<DefaultCritter> enemyIterator = enemyGroup.enemies.iterator();
                while (enemyIterator.hasNext()) {
                    DefaultCritter enemy = enemyIterator.next();
                    if (shooterMissile.getCollisionShape().intersects(enemy.getCollisionShape())) {
                        shooterMissileIterator.remove();
                        enemyIterator.remove();
                        break;
                    }
                }
            }
        }
    }

    private void setKeyBindings() {
        // Special thanks to https://www.youtube.com/watch?v=LNizNHaRV84&t=1484s
        // https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html
        // https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/InputMap.html
        // https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/KeyStroke.html
        // https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/ActionMap.html

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        KeyStroke quitKeyPress = KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false);
        inputMap.put(quitKeyPress, "quitKeyPress");
        actionMap.put("quitKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
    }

}