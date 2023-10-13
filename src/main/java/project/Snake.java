package project;

import java.awt.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Snake {

    public static final Color DEFAULT_COLOR = Color.green;
    private static final int INITIAL_SIZE = 3;

    private static final int DEFAULT_SPEED_FACTOR = 1;

    private static final Direction DEFAULT_DIRECTION = Direction.RIGHT;

    char[][] grid;

    int xGridSize;

    int yGridSize;

    int xCoordinate;

    int yCoordinate;

    int xGridCoordinate;

    int yGridCoordinate;

    int speedFactor;

    int xSpeed;

    int ySpeed;

    int unitPixelSize;

    Queue<Direction> turns;

    int length;

    ArrayList<BodyUnit> bodyUnits;

    Direction direction;


    public Snake(int xGridSize, int yGridSize, int unitPixelSize, char[][] grid) {
        this.unitPixelSize = unitPixelSize;
        this.speedFactor = DEFAULT_SPEED_FACTOR;
        this.grid = grid;
        this.xGridSize = xGridSize;
        this.yGridSize = yGridSize;

        if (xGridSize % 2 == 0)
            this.xCoordinate = xGridSize * unitPixelSize / 2;
        else
            this.xCoordinate = (xGridSize + 1) * unitPixelSize / 2;
        if (yGridSize % 2 == 0)
            this.yCoordinate = yGridSize * unitPixelSize / 2;
        else
            this.yCoordinate = (yGridSize + 1) * unitPixelSize / 2;


        grid[xGridCoordinate][yGridCoordinate] = '0';

        this.direction = DEFAULT_DIRECTION;
        this.xSpeed = this.speedFactor;
        this.ySpeed = 0;
        this.turns = new ArrayBlockingQueue<>(2, true);
        this.bodyUnits = new ArrayList<>();

    }

    public void turn(Direction direction) {
        this.turnHead(direction);
        if (!this.bodyUnits.isEmpty()) {
            for (BodyUnit unit : this.bodyUnits) {
                unit.turns.add(new TurnPoint(direction, new int[]{this.xGridCoordinate, this.yGridCoordinate}));
            }
        }
    }

    private void turnHead(Direction direction) {
        if (direction == Direction.UP && (this.direction == Direction.LEFT || this.direction == Direction.RIGHT)) {
            this.direction = Direction.UP;
            xSpeed = 0;
            ySpeed = -this.speedFactor;
        } else if (direction == Direction.DOWN && (this.direction == Direction.LEFT || this.direction == Direction.RIGHT)) {
            this.direction = Direction.DOWN;
            xSpeed = 0;
            ySpeed = this.speedFactor;
        } else if (direction == Direction.RIGHT && (this.direction == Direction.UP || this.direction == Direction.DOWN)) {
            this.direction = Direction.RIGHT;
            xSpeed = this.speedFactor;
            ySpeed = 0;
        } else if (direction == Direction.LEFT && (this.direction == Direction.UP || this.direction == Direction.DOWN)) {
            this.direction = Direction.LEFT;
            xSpeed = -this.speedFactor;
            ySpeed = 0;
        }
    }

    private void turnBody(BodyUnit unit) {
        Direction nextUnitTurn = unit.turns.remove().turnDirection;
        if (nextUnitTurn == Direction.UP && (unit.currentDirection == Direction.LEFT || unit.currentDirection == Direction.RIGHT)) {
            unit.currentDirection = Direction.UP;
            unit.xSpeed = 0;
            unit.ySpeed = -this.speedFactor;
        } else if (nextUnitTurn == Direction.DOWN && (unit.currentDirection == Direction.LEFT || unit.currentDirection == Direction.RIGHT)) {
            unit.currentDirection = Direction.DOWN;
            unit.xSpeed = 0;
            unit.ySpeed = this.speedFactor;
        } else if (nextUnitTurn == Direction.RIGHT && (unit.currentDirection == Direction.UP || unit.currentDirection == Direction.DOWN)) {
            unit.currentDirection = Direction.RIGHT;
            unit.xSpeed = this.speedFactor;
            unit.ySpeed = 0;
        } else if (nextUnitTurn == Direction.LEFT && (unit.currentDirection == Direction.UP || unit.currentDirection == Direction.DOWN)) {
            unit.currentDirection = Direction.LEFT;
            unit.xSpeed = -this.speedFactor;
            unit.ySpeed = 0;
        }
    }

    public boolean hitWall() {
        if ((this.xCoordinate < 0 || this.xCoordinate + this.unitPixelSize > (xGridSize * this.unitPixelSize)
        ) || this.yCoordinate < 0 || (this.yCoordinate + this.unitPixelSize)
                > (yGridSize * this.unitPixelSize)) {
            System.out.println('a');
            return true;
        }
        return false;
    }

    public void updateCoordinates() {

        this.xCoordinate += xSpeed * speedFactor;
        this.yCoordinate += ySpeed * speedFactor;
        int[] gridCoordinates = this.getGridCoordinates(this.direction, this.xCoordinate, this.yCoordinate);
        this.xGridCoordinate = gridCoordinates[0];
        this.yGridCoordinate = gridCoordinates[1];
        this.grid[this.xGridCoordinate][this.yGridCoordinate] = '0';

        if (this.bodyUnits.isEmpty())
            this.grid[xGridCoordinate][yGridCoordinate] = ' ';
        else {
            int i = 0;
            int len = this.bodyUnits.size() - 1;

            for (BodyUnit unit : this.bodyUnits) {
                if (i == len)
                    this.grid[unit.xGridCoordinate][unit.yGridCoordinate] = ' ';
                unit.xCoordinate += unit.xSpeed * this.speedFactor;
                unit.yCoordinate += unit.ySpeed * this.speedFactor;
                int[] unitGridCoordinates = this.getGridCoordinates(unit.currentDirection, unit.xCoordinate, unit.yCoordinate);
                unit.xGridCoordinate = unitGridCoordinates[0];
                unit.yGridCoordinate = unitGridCoordinates[1];
                this.grid[unit.xGridCoordinate][unit.yGridCoordinate] = '0';

                if (!unit.turns.isEmpty() && unit.xGridCoordinate == unit.turns.peek().coordinateOfTurn[0] &&
                        unit.yGridCoordinate == unit.turns.peek().coordinateOfTurn[1]) {
                    this.turnBody(unit);
                }

                i += 1;
            }
            BodyUnit lastUnit = this.bodyUnits.get(this.bodyUnits.size() - 1);
            this.grid[lastUnit.xGridCoordinate][lastUnit.yGridCoordinate] = ' ';
        }
    }

    public int[] getGridCoordinates(Direction direction, int xCoordinate, int yCoordinate) {
        if (direction == Direction.UP) {
            int[] gridCoordinates = new int[]{(int) Math.floor((double) xCoordinate / unitPixelSize),
                    (int) Math.floor((double) yCoordinate / unitPixelSize)};
            if (gridCoordinates[1] == -1)
                gridCoordinates[1] = 0;
            return gridCoordinates;
        } else if (direction == Direction.DOWN) {
            int[] gridCoordinates = new int[]{(int) Math.floor((double) xCoordinate / unitPixelSize),
                    (int) Math.floor(((double) (yCoordinate + unitPixelSize) / unitPixelSize))};
            if (gridCoordinates[1] == yGridSize)
                gridCoordinates[1] = yGridSize - 1;
            return gridCoordinates;
        } else if (direction == Direction.RIGHT) {
            int[] gridCoordinates = new int[]{(int) Math.floor((double) (xCoordinate + unitPixelSize) / unitPixelSize),
                    (int) Math.floor((double) yCoordinate / unitPixelSize)};
            if (gridCoordinates[0] == xGridSize)
                gridCoordinates[0] = xGridSize - 1;
            return gridCoordinates;

        } else {
            int[] gridCoordinates = new int[]{(int) Math.floor((double) xCoordinate / unitPixelSize),
                    (int) Math.floor((double) yCoordinate / unitPixelSize)};
            if (gridCoordinates[0] == -1)
                gridCoordinates[0] = 0;
            return gridCoordinates;
        }
    }

    public void grow() {
        //grow behind last unit
        Direction lastDirection;
        int lastXCoordinate;
        int lastYCoordinate;
        int lastXSpeed;
        int lastYSpeed;
        int lastXGridCoordinate;
        int lastYGridCoordinate;

        if (this.bodyUnits.isEmpty()) {
            lastDirection = this.direction;
            lastXCoordinate = this.xCoordinate;
            lastYCoordinate = this.yCoordinate;
            lastXSpeed = this.xSpeed;
            lastYSpeed = this.ySpeed;
            lastXGridCoordinate = this.xGridCoordinate;
            lastYGridCoordinate = this.yGridCoordinate;
        } else {
            BodyUnit lastUnit = this.bodyUnits.get(this.bodyUnits.size() - 1);
            lastDirection = lastUnit.currentDirection;
            lastXCoordinate = lastUnit.xCoordinate;
            lastYCoordinate = lastUnit.yCoordinate;
            lastXSpeed = lastUnit.xSpeed;
            lastYSpeed = lastUnit.ySpeed;
            lastXGridCoordinate = lastUnit.xGridCoordinate;
            lastYGridCoordinate = lastUnit.yGridCoordinate;
        }

        if (lastDirection == Direction.UP) {
            BodyUnit unit = new BodyUnit(lastXCoordinate, lastYCoordinate + this.unitPixelSize,
                    lastXSpeed, lastYSpeed, lastXGridCoordinate, lastYGridCoordinate + 1, lastDirection);
            this.bodyUnits.add(unit);
            this.grid[unit.xGridCoordinate][unit.yGridCoordinate] = '0';
        } else if (lastDirection == Direction.DOWN) {
            BodyUnit unit = new BodyUnit(lastXCoordinate, lastYCoordinate - this.unitPixelSize,
                    lastXSpeed, lastYSpeed, lastXGridCoordinate, lastYGridCoordinate - 1, lastDirection);
            this.bodyUnits.add(unit);
            this.grid[unit.xGridCoordinate][unit.yGridCoordinate] = '0';
        } else if (lastDirection == Direction.RIGHT) {
            BodyUnit unit = new BodyUnit(lastXCoordinate - this.unitPixelSize, lastYCoordinate,
                    lastXSpeed, lastYSpeed, lastXGridCoordinate - 1, lastYGridCoordinate, lastDirection);
            this.bodyUnits.add(unit);
            this.grid[unit.xGridCoordinate][unit.yGridCoordinate] = '0';
        } else if (lastDirection == Direction.LEFT) {
            BodyUnit unit = new BodyUnit(lastXCoordinate + this.unitPixelSize, lastYCoordinate,
                    lastXSpeed, lastYSpeed, lastXGridCoordinate + 1, lastYGridCoordinate, lastDirection);
            this.bodyUnits.add(unit);
            this.grid[unit.xGridCoordinate][unit.yGridCoordinate] = '0';
        }
    }

    static class BodyUnit {

        int xGridCoordinate;
        int yGridCoordinate;

        int xCoordinate;

        int yCoordinate;

        int xSpeed;

        int ySpeed;

        Queue<TurnPoint> turns;

        Direction currentDirection;

        public BodyUnit() {
        }

        public BodyUnit(int xCoordinate, int yCoordinate, int xSpeed, int ySpeed, int xUnitGridCoordinate,
                        int yUnitGridCoordinate, Direction currentDirection) {
            this.xCoordinate = xCoordinate;
            this.yCoordinate = yCoordinate;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
            this.turns = new ArrayBlockingQueue<>(20);
            this.xGridCoordinate = xUnitGridCoordinate;
            this.yGridCoordinate = yUnitGridCoordinate;
            this.currentDirection = currentDirection;
        }
    }

    static class TurnPoint {
        Direction turnDirection;

        int[] coordinateOfTurn;

        public TurnPoint(Direction turnDirection, int[] coordinateOfTurn) {
            this.turnDirection = turnDirection;
            this.coordinateOfTurn = coordinateOfTurn;
        }
    }

}
