package field;/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

import move.MoveType;

import java.awt.*;
import java.util.ArrayList;

/**
 * field.Field
 *
 * Stores all information about the playing field and
 * contains methods to perform calculations about the field
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class Field {

    public static final String PATH = ".";
    public static final String WALL = "x";
    public static final String CHARACTER = "C";
    public static final String BUG = "E";

    private String myId;
    private String opponentId;
    private int width;
    private int height;

    private String[][] field;
    private Point myPosition;
    private Point opponentPosition;
    private ArrayList<Point> enemyPositions;
    private ArrayList<Point> snippetPositions;
    private ArrayList<Point> weaponPositions;

    public Field() {
        this.enemyPositions = new ArrayList<>();
        this.snippetPositions = new ArrayList<>();
        this.weaponPositions = new ArrayList<>();
    }

    /**
     * Initializes field
     * @throws Exception: exception
     */
    public void initField() throws Exception {
        try {
            this.field = new String[this.width][this.height];
        } catch (Exception e) {
            throw new Exception("Error: trying to initialize field while field "
                    + "settings have not been parsed yet.");
        }
        clearField();
    }

    /**
     * Clears the field
     */
    public void clearField() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                this.field[x][y] = "";
            }
        }

        this.myPosition = null;
        this.opponentPosition = null;
        this.enemyPositions.clear();
        this.snippetPositions.clear();
        this.weaponPositions.clear();
    }

    /**
     * Parses input string from the engine and stores it in
     * this.field. Also stores several interesting points.
     * @param input String input from the engine
     */
    public void parseFromString(String input) {
        clearField();

        String[] cells = input.split(",");

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                String cell = cells[x + (y * this.width)];
                this.field[x][y] = cell;

                for (char c : cell.toCharArray()) {  // Multiple things can be on same position
                    if (c == this.myId.charAt(0)) {
                        this.myPosition = new Point(x, y);
                    } else if (c == this.opponentId.charAt(0)) {
                        this.opponentPosition = new Point(x, y);
                    } else if (c == 'C') {
                        this.snippetPositions.add(new Point(x, y));
                    } else if (c == 'E') {
                        this.enemyPositions.add(new Point(x, y));
                    } else if (c == 'W') {
                        this.weaponPositions.add(new Point(x, y));
                    }
                }
            }
        }
        //printField();
    }

    /**
     * Returns a string representation of the field that
     * can be printed
     * @return String representation of the current field
     */
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                output.append(this.field[x][y]);
            }
            output.append("\n");
        }

        return output.toString();
    }

    /**
     * Return a list of valid moves for my bot, i.e. moves does not bring
     * player outside the field or inside a wall
     * @return A list of valid moves
     */
    public ArrayList<MoveType> getValidMoveTypes() {
        ArrayList<MoveType> validMoveTypes = new ArrayList<>();
        int myX = this.myPosition.x;
        int myY = this.myPosition.y;

        Point up = new Point(myX, myY - 1);
        Point down = new Point(myX, myY + 1);
        Point left = new Point(myX - 1, myY);
        Point right = new Point(myX + 1, myY);

        if (isPointValid(up)) validMoveTypes.add(MoveType.UP);
        if (isPointValid(down)) validMoveTypes.add(MoveType.DOWN);
        if (isPointValid(left)) validMoveTypes.add(MoveType.LEFT);
        if (isPointValid(right)) validMoveTypes.add(MoveType.RIGHT);

        return validMoveTypes;
    }

    /**
     * Returns whether a point on the field is valid to stand on.
     * @param p Point to test
     * @return True if point is valid to stand on, false otherwise
     */
    public boolean isPointValid(Point p) {
        int x = p.x;
        int y = p.y;

        return x >= 0 && x < this.width && y >= 0 && y < this.height &&
                !this.field[x][y].equals("x");
    }

    public void setMyId(int id) {
        this.myId = id + "";
    }

    public void setOpponentId(int id) {
        this.opponentId = id + "";
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Point getMyPosition() {
        return this.myPosition;
    }

    public Point getOpponentPosition() {
        return this.opponentPosition;
    }

    public ArrayList<Point> getEnemyPositions() {
        return this.enemyPositions;
    }

    public ArrayList<Point> getSnippetPositions() {
        return this.snippetPositions;
    }

    public ArrayList<Point> getWeaponPositions() {
        return this.weaponPositions;
    }

    public String[][] getField() {
        return field;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void printField() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(field[j][i]);
            }
            System.out.print("\n");
        }
    }
}