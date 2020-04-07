import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Shooter extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_WIDTH = vmin * 5 / 100;
    private static final int DEFAULT_HEIGHT = vmin * 5 / 100;

    private static final int DEFAULT_POSITION_X = vw * 50 / 100 - DEFAULT_WIDTH / 2;
    private static final int DEFAULT_POSITION_Y = vh * 90 / 100 - DEFAULT_HEIGHT / 2;

    private static final int MOVEMENT_BOUNDARY_XMIN = vw * 5 / 100;
    private static final int MOVEMENT_BOUNDARY_XMAX = vw * 95 / 100 - DEFAULT_WIDTH;

    private double DEFAULT_THRUSTER_ACCELERATION = 0.5;

    private boolean isLeftThrusterActive = false;
    private boolean isRightThrusterActive = false;

    public Shooter() {
        this(DEFAULT_POSITION_X, DEFAULT_POSITION_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Shooter(double x, double y, double width, double height) {
        super(BoundingShape.RECTANGLE, x, y, width, height);

        setKeyBindings();

    }

    @Override
    public void update() {

        // set acceleration from thruster statuses
        if (isLeftThrusterActive && !isRightThrusterActive) {
            acceleration.x = -DEFAULT_THRUSTER_ACCELERATION;
        } else if (!isLeftThrusterActive && isRightThrusterActive) {
            acceleration.x = DEFAULT_THRUSTER_ACCELERATION;
        } else {
            acceleration.x = 0;
        }

        // if almost no 'thrust' applied or thrust applied in opposite direction than
        // movement, then slow down shooter for fast stopping or turning
        if (velocity.x * acceleration.x < 0.001) {
            velocity.x = 0.8 * velocity.x;
        }

        // update movement via super class
        super.update();

        // keep player within movement boundary
        if (position.x > MOVEMENT_BOUNDARY_XMAX) {
            position.x = MOVEMENT_BOUNDARY_XMAX;
            velocity.x = 0;
            acceleration.x = 0;
        } else if (position.x < MOVEMENT_BOUNDARY_XMIN) {
            position.x = MOVEMENT_BOUNDARY_XMIN;
            velocity.x = 0;
            acceleration.x = 0;
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

        KeyStroke moveLeftKeyPress = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false);
        inputMap.put(moveLeftKeyPress, "moveLeftKeyPress");
        actionMap.put("moveLeftKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                isLeftThrusterActive = true;
            }

        });

        KeyStroke moveLeftKeyRelease = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true);
        inputMap.put(moveLeftKeyRelease, "moveLeftKeyRelease");
        actionMap.put("moveLeftKeyRelease", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                isLeftThrusterActive = false;
            }

        });

        KeyStroke moveRightKeyPress = KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false);
        inputMap.put(moveRightKeyPress, "moveRightKeyPress");
        actionMap.put("moveRightKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                isRightThrusterActive = true;
            }
        });

        KeyStroke moveRightKeyRelease = KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true);
        inputMap.put(moveRightKeyRelease, "moveRightKeyRelease");
        actionMap.put("moveRightKeyRelease", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                isRightThrusterActive = false;
            }
        });
    }

}