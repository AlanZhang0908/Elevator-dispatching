package cn.edu.qdu.Lift;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * @author 志强
 *
 */
public class LiftFrame extends JFrame implements Runnable {
    private static int floorNum = 15;
    private static int liftNum = 3;
    private LiftThread[] liftThread;

    Container cp;
    JPanel floorPanel = new JPanel();

    JButton[] floorButton;
    BasicArrowButton[] upButton;
    BasicArrowButton[] downButton;

    JButton disUp, disDown, disFloor;

    Color pressedUpDownColor = Color.ORANGE;
    Color unPressedUpDownColor = new Color(170, 170, 200);

    int[] upState;
    int[] downState;

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem chooses[] = { new JMenuItem("退出(X)") };

    public LiftFrame() {
        cp = this.getContentPane();
        cp.setLayout(new GridLayout(1, liftNum + 1));

        floorPanel.setLayout(new GridLayout(floorNum + 1, 3));
        floorPanel.setBorder(new MatteBorder(2, 4, 2, 2, Color.blue));
        floorButton = new JButton[floorNum];
        upButton = new BasicArrowButton[floorNum];
        downButton = new BasicArrowButton[floorNum];

        disFloor = new JButton("FLOOR");
        disFloor.setEnabled(false);
        disUp = new JButton("UP");
        disUp.setEnabled(false);
        disDown = new JButton("DOWN");
        disDown.setEnabled(false);
        floorPanel.add(disFloor);
        floorPanel.add(disUp);
        floorPanel.add(disDown);

        MouseListener upListener = new upButtonAction();

        // 设置属性
        for (int i = floorButton.length - 1; i >= 0; i--) {
            floorButton[i] = new JButton(String.valueOf(i + 1));
            floorButton[i].setForeground(Color.green);
            floorButton[i].setForeground(Color.green);
            floorButton[i].setFont(new Font("Serif", Font.BOLD, 13));
            floorButton[i].setEnabled(false);
            upButton[i] = new BasicArrowButton(BasicArrowButton.NORTH);
            upButton[i].addMouseListener(upListener);
            upButton[i].setBackground(unPressedUpDownColor);
            downButton[i] = new BasicArrowButton(BasicArrowButton.SOUTH);
            downButton[i].addMouseListener(upListener);
            downButton[i].setBackground(unPressedUpDownColor);
            floorPanel.add(floorButton[i]);
            floorPanel.add(upButton[i]);
            floorPanel.add(downButton[i]);
        }

        cp.add(floorPanel);
        // 设置菜单
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        menu.setFont(new Font("Serif", Font.BOLD, 14));
        menu.setForeground(Color.blue);
        menu.setMnemonic(KeyEvent.VK_M);

        for (int i = 0; i < chooses.length; i++) {
            menu.add(chooses[i]);
            if (i < chooses.length - 1) {
                menu.addSeparator();
            }
            chooses[i].setForeground(Color.blue);
            chooses[i].setFont(new Font("Serif", Font.BOLD, 14));
        }

        chooses[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });

        menuBar.add(menu);
        setJMenuBar(menuBar);

        liftThread = new LiftThread[liftNum];
        // 创建电梯线程
        for (int i = 0; i < liftNum; i++) {
            LiftThread lift = new LiftThread();
            cp.add(lift);
            lift.getThread().start();
            liftThread[i] = lift;
        }
        upState = new int[floorNum];
        downState = new int[floorNum];

        // 初始化方向键
        for (int i = 0; i < upState.length; i++) {
            upState[i] = 0;
            downState[i] = 0;
        }
        Thread manageThread = new Thread(this);
        manageThread.start();
    }

    // listener for up key
    class upButtonAction extends MouseAdapter implements MouseListener {
        public void mousePressed(MouseEvent e) {
            for (int i = 0; i < upButton.length; i++) {
                if (e.getSource() == upButton[i]) {
                    upButton[i].setBackground(pressedUpDownColor);
                    upState[i] = 1;
                }
                if (e.getSource() == downButton[i]) {
                    downButton[i].setBackground(pressedUpDownColor);
                    downState[i] = 1;
                }
            }
        }
    }

    public static int getFloorNum() {
        return floorNum;
    }

    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // for up key
            for (int i = 0; i < upState.length; i++) {
                if (upState[i] == 1) {
                    upLookForLift(i);
                }
                if (upState[i] >= 5) {
                    if (i == liftThread[upState[i] - 5].getCurPos()) {
                        upState[i] = 0;
                        upButton[i].setBackground(unPressedUpDownColor);
                    }
                }
            }
            // for down key
            for (int i = 0; i < downState.length; i++) {
                if (downState[i] == 1) {
                    downLookForList(i);
                }
                if (downState[i] >= 5) {
                    if (i == liftThread[downState[i] - 5].getCurPos()) {
                        downState[i] = 0;
                        downButton[i].setBackground(unPressedUpDownColor);
                    }
                }
            }
        }
    }

    // 寻找响应向上键最近的电梯
    private boolean upLookForLift(int floor) {
        int whichList = 0;
        int distance = floorNum;

        for (int j = 0; j < liftThread.length; j++) {
            if (liftThread[j].isAbort() || (liftThread[j].isUp() && floor >= liftThread[j].getCurPos())) {
                int temp = Math.abs(floor - liftThread[j].getCurPos());
                if (temp < distance) {
                    whichList = j;
                    distance = Math.abs(floor - liftThread[j].getCurPos());
                }
            }
        }

        if (distance != floorNum) {
            upState[floor] = 5 + whichList;
            liftThread[whichList].setTarPos(floor);
            return true;
        } else {
            return false;
        }

    }

    // 寻找响应向下键最近的电梯
    private boolean downLookForList(int floor) {
        int whichList = 0;
        int distance = floorNum;

        for (int j = 0; j < liftThread.length; j++) {
            if (liftThread[j].isAbort() || (liftThread[j].isDown() && floor <= liftThread[j].getCurPos())) {
                int temp = Math.abs(floor - liftThread[j].getCurPos());
                if (temp < distance) {
                    whichList = j;
                    distance = Math.abs(floor - liftThread[j].getCurPos());
                }
            }
        }

        if (distance != floorNum) {
            downState[floor] = 5 + whichList;
            liftThread[whichList].setTarPos(floor);
            return true;
        } else {
            return false;
        }

    }

}
