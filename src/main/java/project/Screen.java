package project;

import project.enums.GameState;
import project.enums.ModelState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Screen extends JFrame {

    Sandbox sandbox;


    Screen(int xGridSize, int yGridSize, int unitPixelSize) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.sandbox = new Sandbox(xGridSize, yGridSize, unitPixelSize, ModelState.LEARNING);
        this.add(sandbox);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        Screen screen = new Screen(16, 16, 20);

    }

}
