package pathcalculator;

import field.Field;
import move.Move;
import move.MoveType;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Plein on 01/12/2016.
 */
public class PathCalculator {

    private static List<List<MoveType>> solutions;
    private static Integer bestPathDistance;
    private static Integer callsNumber;
    private static Integer[][] visited;

    public static Move bestNextMove(Field field) {
        List<List<MoveType>> moves = new ArrayList<>();
        if (!field.getWeaponPositions().isEmpty()) {
            for (Point point : field.getWeaponPositions()) {
                moves.add(calculateShortestPath(field, field.getMyPosition(), point));
            }
        } else {
            for (Point point : field.getSnippetPositions()) {
                moves.add(calculateShortestPath(field, field.getMyPosition(), point));
            }
        }

        if (moves.isEmpty()) {
            return new Move();
        }
        return new Move(chooseBestMove(moves));
    }

    public static MoveType chooseBestMove(List<List<MoveType>> moves) {
        moves.sort(new Comparator<List<MoveType>>() {
            @Override
            public int compare(List<MoveType> o1, List<MoveType> o2) {
                return o1.size() - o2.size();
            }
        });
        return moves.get(0).get(0);
    }

    public static List<MoveType> calculateShortestPath(Field field, Point start, Point end) {
        init(field);
        calculateMinimumDistanceAux(field, start, end, new ArrayList<MoveType>());
        //System.out.println(callsNumber);
        return getBestSolution();
    }

    private static void init(Field field) {
        solutions = new ArrayList<>();
        bestPathDistance = -1;
        callsNumber = 0;
        visited = initMatrix(field.getWidth(), field.getHeight());
    }

    private static void calculateMinimumDistanceAux(Field field, Point actual, Point end, List<MoveType> moves) {
        callsNumber ++;
        // Path found
        if (actual.equals(end)) {
            //System.out.println("Path found distance " + moves.size() + " moves: " + moves);
            solutions.add(moves);
            if (moves.contains(MoveType.PASS)) {
                bestPathDistance = moves.size() * 2;
            } else {
                bestPathDistance = moves.size();
            }
            return;
        }

        // There is a better path
        if (visited[actual.x][actual.y] < moves.size()
            || (bestPathDistance != -1 && bestPathDistance < moves.size())) {
            return;
        }

        if (field.getField()[actual.x][actual.y].equals(Field.BUG)) {
            // Penalty
            moves.add(MoveType.PASS);
        }

        visited[actual.x][actual.y] = moves.size();

        List<MoveType> bestDirections = getMovesPriority(actual, end);

        for (MoveType move : bestDirections) {
            if (canGoInDirection(field, actual, move)) {
                List<MoveType> newMoves = new ArrayList<>(moves);
                newMoves.add(move);
                calculateMinimumDistanceAux(field, move(actual, move), end, newMoves);
            }
        }
    }

    private static List<MoveType> getMovesPriority(Point actual, Point end) {
        List<MoveType> bestDirections = bestDirections(actual, end);

        List<MoveType> directions = new ArrayList<>();
        directions.add(MoveType.UP);
        directions.add(MoveType.DOWN);
        directions.add(MoveType.RIGHT);
        directions.add(MoveType.LEFT);

        directions.removeAll(bestDirections);
        bestDirections.addAll(directions);

        return bestDirections;
    }

    public static boolean canGoInDirection(Field field, Point actual, MoveType moveType) {
        if (moveType.equals(MoveType.UP)) {
            return canGoUp(field.getField(), actual);
        } else if (moveType.equals(MoveType.DOWN)) {
            return canGoDown(field.getField(), actual);
        } else if (moveType.equals(MoveType.LEFT)) {
            return canGoLeft(field.getField(), actual);
        } else {
            return canGoRight(field.getField(), actual);
        }
    }

    private static boolean canGoRight(String[][] field, Point actual) {
        return actual.x < (field.length - 1) && !field[actual.x + 1][actual.y].equals(Field.WALL);
    }

    private static boolean canGoLeft(String[][] field, Point actual) {
        return actual.x > 0 && !field[actual.x - 1][actual.y].equals(Field.WALL);
    }

    private static boolean canGoDown(String[][] field, Point actual) {
        return actual.y < (field[0].length - 1) && !field[actual.x][actual.y + 1].equals(Field.WALL);
    }

    private static boolean canGoUp(String[][] field, Point actual) {
        return actual.y > 0 && !field[actual.x][actual.y - 1].equals(Field.WALL);
    }

    private static Integer[][] initMatrix(int width, int height) {
        Integer[][] visited = new Integer[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                visited[x][y] = 1000;
            }
        }
        return visited;
    }

    private static List<MoveType> getBestSolution() {
        solutions.sort(new Comparator<List<MoveType>>() {
            @Override
            public int compare(List<MoveType> o1, List<MoveType> o2) {
                if (o1.contains(MoveType.PASS) && !o2.contains(MoveType.PASS)) {
                    return 1;
                } else if (o2.contains(MoveType.PASS) && !o1.contains(MoveType.PASS)) {
                    return -1;
                }
                return o1.size() - o2.size();
            }
        });
        return solutions.get(0);
    }

    private static void printFieldWithSolution(String[][] field, List<MoveType> moves, Point pos) {
        // Copy field
        String[][] newField = new String[field.length][field[0].length];
        for (int i = 0; i < newField.length; i++) {
            for (int j = 0; j < newField[i].length; j++) {
                newField[i][j] = field[i][j];
            }
        }

        newField[pos.x][pos.y] = "0";
        for (MoveType move : moves) {
            pos = move(pos, move);
            newField[pos.x][pos.y] = "M";
        }

        for (int i = 0; i < newField.length; i++) {
            for (int j = 0; j < newField[i].length; j++) {
                System.out.print(newField[i][j]);
            }
            System.out.print("\n");
        }
    }

    /**
     * Find the direction to follow from start point to reach end poing.
     * @param start point
     * @param end point
     * @return List of moves types
     */
    public static List<MoveType> bestDirections(Point start, Point end) {
        List<MoveType> bestDirections = new ArrayList<>();
        int x = end.x - start.x;
        int y = end.y - start.y;
        if (Math.abs(y) > Math.abs(x)){
            if (y > 0) {
                bestDirections.add(MoveType.UP);
            } else if (y < 0){
                bestDirections.add(MoveType.DOWN);
            }
            if (x > 0) {
                bestDirections.add(MoveType.RIGHT);
            } else if (x < 0){
                bestDirections.add(MoveType.LEFT);
            }
        } else if(Math.abs(y) < Math.abs(x)) {
            if (x > 0) {
                bestDirections.add(MoveType.RIGHT);
            } else if (x < 0){
                bestDirections.add(MoveType.LEFT);
            }
            if (y > 0) {
                bestDirections.add(MoveType.UP);
            } else if (y < 0){
                bestDirections.add(MoveType.DOWN);
            }
        }
        return bestDirections;
    }

    public static Point move(Point actual, MoveType moveType) {
        if (MoveType.UP.equals(moveType)) {
            return new Point(actual.x, actual.y - 1);
        } else if (MoveType.DOWN.equals(moveType)) {
            return new Point(actual.x, actual.y + 1);
        } else if (MoveType.RIGHT.equals(moveType)) {
            return new Point(actual.x + 1, actual.y);
        } else {
            return new Point(actual.x - 1, actual.y);
        }
    }
}
