package project;

import project.enums.Action;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Snake {

    public static final Color DEFAULT_COLOR = Color.green;

    public static final char DEFAULT_SNAKE_HEAD_CHAR = 'X';

    public static final char DEFAULT_SNAKE_BODY_CHAR = '0';

    public static final int DEFAULT_SPEED_FACTOR = 1;

    private static final int INITIAL_SIZE = 3;


    private static final Action DEFAULT_DIRECTION = Action.RIGHT;

    static final int TURN_QUEUE_SIZE = 3;

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

    Queue<Action> turns;


    ArrayList<BodyUnit> bodyUnits;

    Action direction;


    public Snake(int xGridSize, int yGridSize, int speed, int unitPixelSize, char[][] grid) {
        this.unitPixelSize = unitPixelSize;
        this.grid = grid;
        this.xGridSize = xGridSize;
        this.yGridSize = yGridSize;
        this.speedFactor = speed;

        if (xGridSize % 2 == 0)
            this.xCoordinate = xGridSize * unitPixelSize / 2;
        else
            this.xCoordinate = (xGridSize + 1) * unitPixelSize / 2;
        if (yGridSize % 2 == 0)
            this.yCoordinate = yGridSize * unitPixelSize / 2;
        else
            this.yCoordinate = (yGridSize + 1) * unitPixelSize / 2;

        this.xGridCoordinate = this.xCoordinate / this.unitPixelSize;
        this.yGridCoordinate = this.yCoordinate / this.unitPixelSize;

        grid[xGridCoordinate][yGridCoordinate] = DEFAULT_SNAKE_HEAD_CHAR;

        this.direction = null;
        this.xSpeed = this.speedFactor;
        this.ySpeed = 0;
        this.turns = new ArrayBlockingQueue<>(TURN_QUEUE_SIZE, true);
        this.bodyUnits = new ArrayList<>();

    }

    public void turn(Action direction) {
        this.turnHead(direction);
        if (!this.bodyUnits.isEmpty()) {
            for (BodyUnit unit : this.bodyUnits) {
                unit.turns.add(new TurnPoint(direction, new int[]{(this.xCoordinate) , (this.yCoordinate)}));
            }
        }
    }

    private void turnHead(Action direction) {
        if (direction == Action.UP && (this.direction == Action.LEFT || this.direction == Action.RIGHT)) {
            this.direction = Action.UP;
            xSpeed = 0;
            ySpeed = -this.speedFactor;
        } else if (direction == Action.DOWN && (this.direction == Action.LEFT || this.direction == Action.RIGHT)) {
            this.direction = Action.DOWN;
            xSpeed = 0;
            ySpeed = this.speedFactor;
        } else if (direction == Action.RIGHT && (this.direction == Action.UP || this.direction == Action.DOWN)) {
            this.direction = Action.RIGHT;
            xSpeed = this.speedFactor;
            ySpeed = 0;
        } else if (direction == Action.LEFT && (this.direction == Action.UP || this.direction == Action.DOWN)) {
            this.direction = Action.LEFT;
            xSpeed = -this.speedFactor;
            ySpeed = 0;
        }
    }

    public void snakeInit(Action direction) {
        this.direction = direction;
        this.changeSpeedOnDirection(direction);
        for (int i = 0; i < INITIAL_SIZE - 1; i++) {
            this.grow();
        }
    }
    
    private void changeSpeedOnDirection(Action direction) {
        if (direction == Action.UP) {
            this.ySpeed = -this.speedFactor;
            this.xSpeed = 0;

        } else if (direction == Action.DOWN) {
            this.ySpeed = this.speedFactor;
            this.xSpeed = 0;

        } else if (direction == Action.RIGHT) {
            this.ySpeed = 0;
            this.xSpeed = this.speedFactor;
        } else {
            this.ySpeed = 0;
            this.xSpeed = -this.speedFactor;
        }
    }
    

    private void turnBody(BodyUnit unit) {
        Action nextUnitTurn = unit.turns.remove().turnDirection;
        if (nextUnitTurn == Action.UP && (unit.currentDirection == Action.LEFT || unit.currentDirection == Action.RIGHT)) {
            unit.currentDirection = Action.UP;
            unit.xSpeed = 0;
            unit.ySpeed = -this.speedFactor;
        } else if (nextUnitTurn == Action.DOWN && (unit.currentDirection == Action.LEFT || unit.currentDirection == Action.RIGHT)) {
            unit.currentDirection = Action.DOWN;
            unit.xSpeed = 0;
            unit.ySpeed = this.speedFactor;
        } else if (nextUnitTurn == Action.RIGHT && (unit.currentDirection == Action.UP || unit.currentDirection == Action.DOWN)) {
            unit.currentDirection = Action.RIGHT;
            unit.xSpeed = this.speedFactor;
            unit.ySpeed = 0;
        } else if (nextUnitTurn == Action.LEFT && (unit.currentDirection == Action.UP || unit.currentDirection == Action.DOWN)) {
            unit.currentDirection = Action.LEFT;
            unit.xSpeed = -this.speedFactor;
            unit.ySpeed = 0;
        }
    }

    public boolean lostGame() {

        //hit wall
        if ((this.xCoordinate < 0 || this.xCoordinate + this.unitPixelSize > (xGridSize * this.unitPixelSize)
        ) || this.yCoordinate < 0 || (this.yCoordinate + this.unitPixelSize)
                > (yGridSize * this.unitPixelSize)) {
            return true;
        }
        //hit itself

        for (BodyUnit unit : this.bodyUnits) {
            if (headOverLapsWithBodyUnit(unit)) {
                return true;
            }
        }

        return false;
    }

    private boolean headOverLapsWithBodyUnit(BodyUnit unit) {
        // Calculate the coordinates of the right and bottom edges of the rectangles

        int x3 = unit.xCoordinate;
        int x4= unit.xCoordinate + this.unitPixelSize;
        int y3 = unit.yCoordinate;
        int y4 = unit.yCoordinate + this.unitPixelSize;

        if (this.direction == Action.UP) {
            int x1 = this.xCoordinate;
            int x2 = this.xCoordinate + this.unitPixelSize;
            int y = this.yCoordinate;

            return (((x1 >= x3 && x1 < x4) || (x2 > x3 && x2 <= x4)) && (y > y3 && y < y4));
        }
        else if (this.direction == Action.RIGHT) {
            int y1 = this.yCoordinate;
            int y2 = this.yCoordinate + this.unitPixelSize;
            int x = this.xCoordinate + this.unitPixelSize;

            return (((y1 >= y3 && y1 < y4 ) || (y2 > y3 && y2 <= y4 )) && (x > x3 && x < x4));

        }
        else if (this.direction == Action.DOWN) {

            int x1 = this.xCoordinate;
            int x2 = this.xCoordinate + this.unitPixelSize;
            int y = this.yCoordinate + this.unitPixelSize;

            return (((x1 >= x3 && x1 < x4) || (x2 > x3 && x2 <= x4)) && (y > y3 && y < y4));
        }
        else {
            int y1 = this.yCoordinate;
            int y2 = this.yCoordinate + this.unitPixelSize;
            int x = this.xCoordinate;

            return (((y1 >= y3 && y1 < y4 ) || (y2 > y3 && y2 <= y4 )) && (x > x3 && x < x4));
        }

    }


    public void updateCoordinates() {

        this.xCoordinate += xSpeed;
        this.yCoordinate += ySpeed;
        int[] gridCoordinates = this.getGridCoordinates(this.direction, this.xCoordinate, this.yCoordinate);

        this.grid[this.xGridCoordinate][this.yGridCoordinate] = ' ';
        this.grid[gridCoordinates[0]][gridCoordinates[1]] = DEFAULT_SNAKE_HEAD_CHAR;

        this.xGridCoordinate = gridCoordinates[0];
        this.yGridCoordinate = gridCoordinates[1];

        if (!this.bodyUnits.isEmpty()) {
            int i = 0;
            int len = this.bodyUnits.size() - 1;

            for (BodyUnit unit : this.bodyUnits) {

                this.grid[unit.xGridCoordinate][unit.yGridCoordinate] = ' ';

                if (!unit.turns.isEmpty() && unit.xCoordinate == unit.turns.peek().coordinateOfTurn[0] &&
                        unit.yCoordinate == unit.turns.peek().coordinateOfTurn[1]) {
                    this.turnBody(unit);
                }
                unit.xCoordinate += unit.xSpeed;
                unit.yCoordinate += unit.ySpeed;
                int[] unitGridCoordinates = this.getGridCoordinates(unit.currentDirection, unit.xCoordinate, unit.yCoordinate);
                unit.xGridCoordinate = unitGridCoordinates[0];
                unit.yGridCoordinate = unitGridCoordinates[1];
                this.grid[unit.xGridCoordinate][unit.yGridCoordinate] = DEFAULT_SNAKE_BODY_CHAR;

                i += 1;
            }
        }
    }

    public int[] getGridCoordinates(Action direction, int xCoordinate, int yCoordinate) {
        if (direction == Action.UP) {
            int[] gridCoordinates = new int[]{(int) Math.floor((double) xCoordinate / unitPixelSize),
                    (int) Math.floor((double) yCoordinate / unitPixelSize)};
            if (gridCoordinates[1] == -1)
                gridCoordinates[1] = 0;
            return gridCoordinates;
        } else if (direction == Action.DOWN) {
            int[] gridCoordinates = new int[]{(int) Math.floor((double) xCoordinate / unitPixelSize),
                    (int) Math.floor(((double) (yCoordinate + unitPixelSize) / unitPixelSize))};
            if (gridCoordinates[1] == yGridSize)
                gridCoordinates[1] = yGridSize - 1;
            return gridCoordinates;
        } else if (direction == Action.RIGHT) {
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
        Action direction;
        int lastXCoordinate;
        int lastYCoordinate;
        int lastXSpeed;
        int lastYSpeed;
        int lastXGridCoordinate;
        int lastYGridCoordinate;
        Queue<TurnPoint> lastTurns = null;
        BodyUnit unit = null;

        if (this.bodyUnits.isEmpty()) {
            direction = this.direction;
            lastXCoordinate = this.xCoordinate;
            lastYCoordinate = this.yCoordinate;
            lastXSpeed = this.xSpeed;
            lastYSpeed = this.ySpeed;
            lastXGridCoordinate = this.xGridCoordinate;
            lastYGridCoordinate = this.yGridCoordinate;
        } else {
            BodyUnit lastUnit = this.bodyUnits.get(this.bodyUnits.size() - 1);
            direction = lastUnit.currentDirection;
            lastXCoordinate = lastUnit.xCoordinate;
            lastYCoordinate = lastUnit.yCoordinate;
            lastXSpeed = lastUnit.xSpeed;
            lastYSpeed = lastUnit.ySpeed;
            lastXGridCoordinate = lastUnit.xGridCoordinate;
            lastYGridCoordinate = lastUnit.yGridCoordinate;
            lastTurns = lastUnit.turns;
        }

        if (direction == Action.UP) {

            unit = new BodyUnit(lastXCoordinate, lastYCoordinate + this.unitPixelSize,
                    lastXSpeed, lastYSpeed, lastXGridCoordinate, lastYGridCoordinate + 1, direction);
        } else if (direction == Action.DOWN) {
            unit = new BodyUnit(lastXCoordinate, lastYCoordinate - this.unitPixelSize,
                    lastXSpeed, lastYSpeed, lastXGridCoordinate, lastYGridCoordinate - 1, direction);
        } else if (direction == Action.RIGHT) {
            unit = new BodyUnit(lastXCoordinate - this.unitPixelSize, lastYCoordinate,
                    lastXSpeed, lastYSpeed, lastXGridCoordinate - 1, lastYGridCoordinate, direction);
        } else {
            unit = new BodyUnit(lastXCoordinate + this.unitPixelSize, lastYCoordinate,
                    lastXSpeed, lastYSpeed, lastXGridCoordinate + 1, lastYGridCoordinate, direction);
        }
        if (lastTurns != null) {
            unit.turns = deepCopyQueue(lastTurns);
        }
        this.bodyUnits.add(unit);
        this.grid[unit.xGridCoordinate][unit.yGridCoordinate] = DEFAULT_SNAKE_BODY_CHAR;
    }

    static class BodyUnit {

        int xGridCoordinate;
        int yGridCoordinate;

        int xCoordinate;

        int yCoordinate;

        int xSpeed;

        int ySpeed;

        Queue<TurnPoint> turns;

        Action currentDirection;

        public BodyUnit() {
        }

        public BodyUnit(int xCoordinate, int yCoordinate, int xSpeed, int ySpeed, int xUnitGridCoordinate,
                        int yUnitGridCoordinate, Action currentDirection) {
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
        Action turnDirection;

        int[] coordinateOfTurn;

        public TurnPoint(Action turnDirection, int[] coordinateOfTurn) {
            this.turnDirection = turnDirection;
            this.coordinateOfTurn = coordinateOfTurn;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TurnPoint turnPoint)) return false;
            return turnDirection == turnPoint.turnDirection && Arrays.equals(coordinateOfTurn, turnPoint.coordinateOfTurn);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(turnDirection);
            result = 31 * result + Arrays.hashCode(coordinateOfTurn);
            return result;
        }

        @Override
        public TurnPoint clone() {
            try {
                return (TurnPoint) super.clone();
            } catch (CloneNotSupportedException e) {
                return new TurnPoint(this.turnDirection, this.coordinateOfTurn.clone());
            }
        }

    }

    public static Queue<TurnPoint> deepCopyQueue(Queue<TurnPoint> originalQueue) {
        Queue<TurnPoint> deepCopy = new LinkedList<>();
        for (TurnPoint turnPoint : originalQueue) {
            deepCopy.add(turnPoint.clone());
        }
        return deepCopy;
    }

}
