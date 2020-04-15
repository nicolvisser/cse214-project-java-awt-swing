import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class KeyLis extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode() + " " + e.getKeyChar());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(500,500));
        frame.pack();
        frame.addKeyListener(new KeyLis());
        frame.setFocusTraversalKeysEnabled(false);
        frame.setVisible(true);
        
    }

}