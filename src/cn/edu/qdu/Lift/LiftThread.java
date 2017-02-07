package cn.edu.qdu.Lift;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

/**
 * @author 志强
 *
 */
public class LiftThread extends JPanel implements Runnable {
    private final int UP = 1, DN = -1, ABORT = 0;
    private static int floorNum;
    private int direction;
    private int curPos;
    private boolean[] numState;
    private int tarPos;
    private Thread thread;

    private Color numColor = new Color(192, 160, 190), numColor1 = Color.green;
    private Color floorColor = new Color(100, 100, 100), floorColor1 = Color.blue;

    JButton[] liftButton;
    JButton[] numButton;
    JLabel dispState, dispFloor;

    public LiftThread() {
        floorNum = LiftFrame.getFloorNum();
        direction = ABORT;
        curPos = 0;
        tarPos = 0;

        // 对电梯内部的数字键进行状态初始化
        numState = new boolean[floorNum];
        for (int i = 0; i < numState.length; i++) {
            numState[i] = false;
        }

        thread = new Thread(this);

        // 面板布局
        setLayout(new GridLayout(floorNum + 1, 2));
        this.setBorder(new MatteBorder(2, 2, 2, 2, Color.lightGray));
        liftButton = new JButton[floorNum];
        numButton = new JButton[floorNum];

        dispFloor = new JLabel("Floor", SwingConstants.CENTER);
        dispState = new JLabel("Stop", SwingConstants.CENTER);
        dispState.setForeground(new Color(217, 123, 2));

        this.add(dispFloor);
        this.add(dispState);

        MouseListener numListener = new NumButtonAction();
        // 设置属性
        for (int i = liftButton.length - 1; i >= 0; i--) {
            numButton[i] = new JButton(String.valueOf(i + 1));
            numButton[i].addMouseListener(numListener);
            numButton[i].setBackground(numColor);
            liftButton[i] = new JButton();
            liftButton[i].setEnabled(false);
            liftButton[i].setBackground(floorColor);
            this.add(numButton[i]);
            this.add(liftButton[i]);
        }
        liftButton[curPos].setBackground(floorColor1);
    }

    // 电梯内部数字键的监听器类
    class NumButtonAction extends MouseAdapter implements MouseListener {

        public void mousePressed(MouseEvent e) {
            for (int i = 0; i < numButton.length; i++) {
                if (e.getSource() == numButton[i]) {
                    numState[i] = true;
                    numButton[i].setBackground(numColor1);
                    if (direction == ABORT) {
                        tarPos = i;
                    }

                    if (direction == UP) {
                        tarPos = getMaxPressedNum();
                    }
                    if (direction == DN) {
                        tarPos = getMinPressedNum();
                    }
                }
            }
        }
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (direction == UP || direction == DN) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                direction = ABORT;
            }

            if (tarPos > curPos) {
                direction = UP;
                dispState.setText("Upstream");
                moveUp();
                direction = ABORT;
                dispState.setText("Stop");
            } else if (tarPos < curPos) {
                direction = DN;
                dispState.setText("DownStram");
                moveDn();
                direction = ABORT;
                dispState.setText("Stop");
            }

        }
    }

    public void moveUp() {
        int oldPos = curPos;
        for (int i = curPos + 1; i <= tarPos; i++) {
            try {
                dispState.setText("UpStream");
                Thread.sleep(600);
                liftButton[i].setBackground(floorColor1);

                if (i > oldPos) {
                    liftButton[i - 1].setBackground(floorColor);
                }

                if (numState[i]) {
                    dispState.setText("Open");
                    Thread.sleep(1000);

                    dispState.setText("Close");
                    numButton[i].setBackground(numColor);
                    Thread.sleep(1000);
                }
                curPos = i;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        clearState();
    }

    public void moveDn() {
        int oldPos = curPos;
        for (int i = curPos - 1; i >= tarPos; i--) {
            try {
                Thread.sleep(600);
                liftButton[i].setBackground(Color.blue);

                if (i < oldPos) {
                    liftButton[i + 1].setBackground(floorColor);
                }

                if (numState[i]) {
                    dispState.setText("Open");
                    Thread.sleep(2000);
                    dispState.setText("Close");
                    numButton[i].setBackground(numColor);
                    Thread.sleep(800);
                }
                curPos = i;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        clearState();
    }

    public Thread getThread() {
        return thread;
    }

    private int getMinPressedNum() {
        int min = 0;
        for (int i = 0; i < numState.length; i++) {
            if (numState[i]) {
                min = i;
                break;
            }
        }
        return min;
    }

    private int getMaxPressedNum() {
        int max = 0;
        for (int i = numState.length - 1; i >= 0; i--) {
            if (numState[i]) {
                max = i;
                break;
            }
        }
        return max;
    }

    private void clearState() {
        for (int i = 0; i < numState.length; i++) {
            if (numState[i]) {
                numState[i] = false;
                numButton[i].setBackground(numColor);
            }
        }
    }

    public int getDirection() {
        return direction;
    }

    public int getTarPos() {
        return tarPos;
    }

    public void setDirection(int i) {
        direction = i;
    }

    public void setTarPos(int i) {
        if (direction == ABORT) {
            tarPos = i;
            numState[i] = true;
            if (curPos > tarPos) {
                direction = DN;
            }
            if (curPos < tarPos) {
                direction = UP;
            }
        }

        if (direction == UP && i > tarPos) {
            tarPos = i;
            numState[i] = true;
        }

        if (direction == DN && i < tarPos) {
            tarPos = i;
            numState[i] = true;
        }
    }

    public boolean isUp() {
        return direction == UP;
    }

    public boolean isDown() {
        return direction == DN;
    }

    public boolean isAbort() {
        return direction == ABORT;
    }

    public int getCurPos() {
        return curPos;
    }

    public void setDirectionUp() {
        direction = UP;
    }

    public void setDirectionDn() {
        direction = DN;
    }

    public void setDirectionAbort() {
        direction = ABORT;
    }
}
