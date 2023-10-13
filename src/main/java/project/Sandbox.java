package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Sandbox extends JPanel implements KeyListener {

    int xGridSize;

    int yGridSize;

    int unitPixelSize;

    int score;

    int highScore;

    char[][] grid;

    Snake snake;

    int xApple;

    int yApple;

    GameState gameState;

    int t = 5;

    public Sandbox(int xGridSize, int yGridSize, int unitPixelSize) {

        this.addKeyListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();

        this.xGridSize = xGridSize;
        this.yGridSize = yGridSize;
        this.unitPixelSize = unitPixelSize;
        this.grid = new char[xGridSize][yGridSize];
        Arrays.stream(grid).forEach(a -> Arrays.fill(a, ' '));

        this.snake = new Snake(this.xGridSize, this.yGridSize, unitPixelSize, this.grid);

        this.spawnNewApple();

        this.setPreferredSize(new Dimension(xGridSize * unitPixelSize, yGridSize * unitPixelSize));
        this.setBackground(Color.black);
        this.gameState = GameState.ALIVE;

    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        this.drawGrid(g2D, Color.GRAY);
        this.drawSnake(g2D);
        this.drawApple(g2D);

        if (!this.snake.turns.isEmpty()) {

            if ((this.snake.yCoordinate % unitPixelSize) == 0 && this.snake.xCoordinate % unitPixelSize == 0) {
                this.snake.turn(this.snake.turns.poll());
            }
        }
        this.snake.updateCoordinates();

        if (this.appleEaten()) {
            this.score+=1;
            this.spawnNewApple();
            this.snake.grow();
        }

        if (this.snake.hitWall()) {
            this.restartGame();
        }

        //this.isTouchingBorder();
        this.repaint();
    }

    private void restartGame() {
        this.grid = new char[xGridSize][yGridSize];
        this.snake = new Snake(this.xGridSize, this.yGridSize, this.unitPixelSize, this.grid);
        this.highScore = Math.max(this.score, this.highScore);
        this.spawnNewApple();
        this.score = 0;
    }



    private void drawGrid(Graphics2D g2D, Color gridLineColor) {
        int gridSize = this.xGridSize * this.yGridSize;
        Color cacheColor = (Color) g2D.getPaint();
        g2D.setPaint(gridLineColor);
        for (int row = 1; row < gridSize; row++) {
            int y = row * this.unitPixelSize;
            g2D.drawLine(0, y, getWidth(), y);
        }

        // Draw vertical grid lines
        for (int col = 1; col < gridSize; col++) {
            int x = col * this.unitPixelSize;
            g2D.drawLine(x, 0, x, getHeight());
        }

        g2D.setPaint(cacheColor);
    }

    private void drawSnake(Graphics2D g2D) {
        g2D.setPaint(Color.green);
        g2D.fillRect(this.snake.xCoordinate, this.snake.yCoordinate, this.unitPixelSize, this.unitPixelSize);
        if (!this.snake.bodyUnits.isEmpty()) {
            for (Snake.BodyUnit unit : this.snake.bodyUnits) {
                g2D.fillRect(unit.xCoordinate, unit.yCoordinate, this.unitPixelSize, this.unitPixelSize);
            }
        }

    }

    private void drawApple(Graphics2D g2D) {
        g2D.setPaint(Color.RED);
        g2D.fillRect(this.xApple * this.unitPixelSize, this.yApple * this.unitPixelSize,
                this.unitPixelSize, this.unitPixelSize);
    }

    private void spawnNewApple() {
        Random r = new Random();
        this.grid[xApple][yApple] = ' ';
        this.xApple = r.nextInt(0, xGridSize);
        this.yApple = r.nextInt(0, yGridSize);
        this.grid[xApple][yApple] = 'X';

    }

    private boolean appleEaten() {
        return this.snake.xGridCoordinate == this.xApple && this.snake.yGridCoordinate == this.yApple;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        Direction direction = getDirectionOnKey(e);

        if (!this.snake.turns.isEmpty()) {

            this.snake.turns.add(direction);
            return;
        }
        if (direction == this.snake.direction) {
            return;
        }
        this.snake.turns.add(direction);
        //this.snake.turn(direction);

    }

    private Direction getDirectionOnKey(KeyEvent e) {
        if (e.getKeyCode() >= 37 && e.getKeyCode() <= 40) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                return Direction.UP;
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                return Direction.RIGHT;
            }
            else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                return Direction.DOWN;
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                return Direction.LEFT;
            }
        }
        return this.snake.direction;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
