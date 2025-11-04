package src;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;


public class GUI implements ActionListener {

    private JLabel label;
    private JLabel label2;
    private JLabel label3;
    private JFrame frame;
    private JButton button;
    private JPanel panel;
    private JPanel slotsPanel;
    private static JTextField[] slots;
    private static Robot robot;

    private static boolean on = false;
    private static int slot = 49;
    private static ArrayList<Integer> weights = new ArrayList<>();

    private static GUI instance;

    public GUI() {
        instance = this; // store instance for static access

        frame = new JFrame();
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));

        button = new JButton("Click me");
        button.addActionListener(this);

        label = new JLabel("Off");
        label2 = new JLabel("Weight:");
        label3 = new JLabel("Press F6 to toggle");

        slots = new JTextField[9];
        for (int i = 0; i < 9; i++) {
            slots[i] = new JTextField(1);
            slots[i].setText("0");
        }

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(label3);
        panel.add(button);
        panel.add(label);

        panel.add(Box.createVerticalStrut(20));
        panel.add(label2);

        slotsPanel = new JPanel();
        slotsPanel.setLayout(new GridLayout(1, 9, 10, 0));
        for (JTextField slot : slots) {
            slotsPanel.add(slot);
        }

        panel.add(slotsPanel);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Block Randomizer");
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        new GUI(); 

        LogManager.getLogManager().reset();
        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

        try {
            GlobalScreen.registerNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                if (e.getKeyCode() == NativeKeyEvent.VC_F6) {
                    instance.button.doClick();
                }
            }
            public void nativeKeyReleased(NativeKeyEvent e) {}
            public void nativeKeyTyped(NativeKeyEvent e) {}
        });

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        Random random = new Random();

        while (true) {
            TimeUnit.MILLISECONDS.sleep(40);

            if (!on || weights.isEmpty()) continue;

            int randomIndex = random.nextInt(weights.size());
            slot = weights.get(randomIndex);

            robot.keyPress(slot);
            robot.keyRelease(slot);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        on = !on;

        if (on) {
            label.setText("Running!");
            weights = getWeights();
        } else {
            label.setText("Off");
        }
    }

    public ArrayList<Integer> getWeights() {
        ArrayList<Integer> newWeights = new ArrayList<>();

        int slotValue = 49;
        for (JTextField slotField : slots) {
            int weight = Integer.parseInt(slotField.getText());

            for (int i = 0; i < weight; i++) {
                newWeights.add(slotValue);
            }

            slotValue++;
        }

        return newWeights;
    }
}