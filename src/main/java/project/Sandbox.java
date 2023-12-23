package project;

import project.enums.Action;
import project.enums.GameState;
import project.enums.ModelState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Random;

public class Sandbox extends JPanel implements KeyListener {

    private static final char DEFAULT_APPLE_CHAR = 'A';

    int xGridSize;

    int yGridSize;

    int unitPixelSize;

    int score;

    int highScore;

    char[][] grid;

    Snake snake;

    int snakeSpeed;

    int xApple;

    int yApple;

    GameState gameState;

    Random appleCoordinateGen = new Random();

    JLabel highScoreComponent;

    JLabel currentScoreComponent;

    JPanel panel;


    public Sandbox(int xGridSize, int yGridSize, int unitPixelSize, ModelState modelState) {

        this.addKeyListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();

        this.gameState = GameState.START;

        this.xGridSize = xGridSize;
        this.yGridSize = yGridSize;
        this.unitPixelSize = unitPixelSize;
        this.grid = new char[xGridSize][yGridSize];
        Arrays.stream(grid).forEach(a -> Arrays.fill(a, ' '));

        if (modelState == ModelState.LEARNING) {
            this.snakeSpeed = unitPixelSize;
        } else
            this.snakeSpeed = Snake.DEFAULT_SPEED_FACTOR;

        this.snake = new Snake(this.xGridSize, this.yGridSize, this.snakeSpeed, unitPixelSize, this.grid);


        this.spawnNewApple();

        this.setPreferredSize(new Dimension(xGridSize * unitPixelSize, yGridSize * unitPixelSize));
        this.setBackground(Color.black);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        this.highScoreComponent = new JLabel("High Score: " + this.highScore);
        this.highScoreComponent.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.highScoreComponent.setForeground(Color.WHITE);

        this.currentScoreComponent = new JLabel("Current Score: " + this.score);
        this.currentScoreComponent.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.currentScoreComponent.setForeground(Color.WHITE);
        panel.add(currentScoreComponent);
        panel.add(highScoreComponent);
        panel.setBackground(new Color(Color.TRANSLUCENT));
        this.add(panel, BorderLayout.LINE_START);

    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        this.drawGrid(g2D, Color.GRAY);
        this.drawSnake(g2D);
        this.drawApple(g2D);

        if (this.gameState == GameState.ALIVE) {

            if (this.appleEaten()) {
                this.score += 1;
                this.spawnNewApple();
                this.snake.grow();
                this.currentScoreComponent.setText("Current Score: " + this.score);
                if (this.score > highScore) {
                    this.highScore = this.score;
                    this.highScoreComponent.setText("High Score: " + this.highScore);
                }
            }
            if (!this.snake.turns.isEmpty()) {

                if ((this.snake.yCoordinate % unitPixelSize) == 0 && this.snake.xCoordinate % unitPixelSize == 0) {
                    this.snake.turn(this.snake.turns.poll());
                }
            }
            this.snake.updateCoordinates();


            if (this.snake.lostGame()) {
                this.restartGame();
            }
        }

        //this.isTouchingBorder();
        printGrid();
        this.repaint();
    }

    private void restartGame() {
        this.grid = new char[xGridSize][yGridSize];
        Arrays.stream(grid).forEach(a -> Arrays.fill(a, ' '));
        this.snake = new Snake(this.xGridSize, this.yGridSize, this.snakeSpeed, this.unitPixelSize, this.grid);
        this.spawnNewApple();
        this.score = 0;
        this.currentScoreComponent.setText("Current Score: 0");
        this.gameState = GameState.START;
    }

    private void printGrid() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < grid[0].length; i++) {
            s.append(',');
            for (char[] chars : grid) {
                s.append((chars[i])).append(',');
            }
            s.append('\n');
        }
        System.out.println(s);

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
        while (true) {
            int newXApple = appleCoordinateGen.nextInt(0, xGridSize);
            int newYApple = appleCoordinateGen.nextInt(0, yGridSize);
            if (this.grid[newXApple][newYApple] == ' ') {
                this.grid[xApple][yApple] = ' ';
                this.grid[newXApple][newYApple] = DEFAULT_APPLE_CHAR;
                this.xApple = newXApple;
                this.yApple = newYApple;
                return;
            }
        }
    }

    private boolean appleEaten() {
        return this.snake.xGridCoordinate == this.xApple && this.snake.yGridCoordinate == this.yApple;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        project.enums.Action direction = getDirectionOnKey(e);

        if (this.gameState == GameState.START) {
            this.snake.snakeInit(direction);
            this.gameState = GameState.ALIVE;
            return;
        }

        if (this.snake.turns.size() == Snake.TURN_QUEUE_SIZE) {
            return;
        }

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

    private project.enums.Action getDirectionOnKey(KeyEvent e) {
        if (e.getKeyCode() >= 37 && e.getKeyCode() <= 40) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                return project.enums.Action.UP;
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                return project.enums.Action.RIGHT;
            }
            else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                return project.enums.Action.DOWN;
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                return Action.LEFT;
            }
        }
        return this.snake.direction;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
