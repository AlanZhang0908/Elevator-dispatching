package cn.edu.qdu.Lift;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * @author 志强
 * 
 */
public class LiftMain {
    public static void main(String[] args) {
        JFrame frame = new LiftFrame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setTitle("Lift");
        frame.setLocation(50, 50);
        frame.setSize(1200, 1200);
        frame.setResizable(false);
        frame.setVisible(true);
        ;
    }
}
