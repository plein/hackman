package pathcalculator;

import configuration.Configuration;
import field.Field;
import move.Move;
import move.MoveType;
import move.Path;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Plein on 01/12/2016.
 */
public class PathCalculator {

    private static Integer bestPathDistance;

    public static Move bestNextMove(Field field, boolean iHaveWeapon, boolean opponentWithWeapon, boolean imWining) {
        List<Path> paths = new ArrayList<>();
        if (!iHaveWeapon && !field.getWeaponPositions().isEmpty()) {
            for (Point point : field.getWeaponPositions()) {
                Path path = calculateShortestPath(field, field.getMyPosition(), point, null, iHaveWeapon, opponentWithWeapon, field.getOpponentId(), true);
                if (path != null) {
                    Path opponentPath = calculateShortestPath(field, field.getOpponentPosition(), point, null, iHaveWeapon, opponentWithWeapon, field.getOpponentId(), false);
                    if (opponentPath == null || path.getDistance() < opponentPath.getDistance()) {
                        paths.add(path);
                    }
                }
            }
            for (Point point : field.getSnippetPositions()) {
                Integer distance = (paths.isEmpty() || !imWining) ? null : Configuration.MAX_DISTANCE_IF_SWORD.getValue();
                Path path = calculateShortestPath(field, field.getMyPosition(), point, distance, iHaveWeapon, opponentWithWeapon, field.getOpponentId(), true);
                if (path != null) {
                    paths.add(path);
                }
            }
        } else {
            for (Point point : field.getSnippetPositions()) {
                Path path = calculateShortestPath(field, field.getMyPosition(), point, null, iHaveWeapon, opponentWithWeapon, field.getOpponentId(), true);
                if (path != null) {
                    paths.add(path);
                }
            }
            for (Point point : field.getWeaponPositions()) {
                Path path = calculateShortestPath(field, field.getMyPosition(), point, null, iHaveWeapon, opponentWithWeapon, field.getOpponentId(), true);
                if (path != null) {
                    paths.add(path);
                }
            }
        }

        if (paths.isEmpty()) {
            return new Move(goToBestPosition(field, iHaveWeapon, opponentWithWeapon));
        }
        return new Move(chooseBestMove(paths, field, iHaveWeapon, opponentWithWeapon));
    }

    private static MoveType goToBestPosition(Field field, boolean iHaveWeapon, boolean opponentWithWeapon) {
        Path path1 = calculateShortestPath(field, field.getMyPosition(), Field.BEST_POSITION, null, iHaveWeapon, opponentWithWeapon, field.getOpponentId(), true);
        Path path2 = calculateShortestPath(field, field.getMyPosition(), Field.BEST_POSITION2, null, iHaveWeapon, opponentWithWeapon, field.getOpponentId(), true);
        if (path1 != null && path2 != null) {
            if (path1.getDistance() == 0) {
                return path2.getMoves().get(0);
            } else if (path2.getDistance() == 0) {
                return path1.getMoves().get(0);
            } else {
                return (path1.getDistance() < path2.getDistance())
                        ? path1.getMoves().get(0)
                        : path2.getMoves().get(0);
            }
        } else if (path1 == null && path2 != null) {
            return path2.getMoves().get(0);
        } else if (path2 == null && path1 != null) {
            return path1.getMoves().get(0);
        } else {
            return field.getMyValidMoveTypes().get(0);
        }
    }

    public static MoveType chooseBestMove(List<Path> paths, Field field, boolean iHaveWeapon, boolean opponentWithWeapon) {
        paths.sort(new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                return o1.getDistance() - o2.getDistance();
            }
        });

        List<Path> pathsImCloser = new ArrayList<>();
        List<Path> pathsEnemyIsCloser = new ArrayList<>();
        for (Path path : paths) {
            Path opponentPath = calculateShortestPath(field, field.getOpponentPosition(), path.getEnd(), path.getDistance(), null, null, null, false);
            if (opponentPath == null || opponentPath.getDistance() >= path.getDistance()) {
                pathsImCloser.add(path);
            } else {
                pathsEnemyIsCloser.add(path);
            }
        }

        if (paths.size() == 1 && pathsImCloser.isEmpty()) {
            return goToBestPosition(field, iHaveWeapon, opponentWithWeapon);
        }

        if (pathsImCloser.isEmpty()) {
            // go to worst enemy pos
            pathsEnemyIsCloser.sort(new Comparator<Path>() {
                @Override
                public int compare(Path o1, Path o2) {
                    return o2.getDistance() - o1.getDistance();
                }
            });
            return pathsEnemyIsCloser.get(0).getMoves().get(0);
        }

        // Priorize same direction
        if (pathsImCloser.size() <= 2) {
            return pathsImCloser.get(0).getMoves().get(0);
        }

        Path bestPath = null;
        Integer distance = null;
        for (Path path : pathsImCloser) {
            for (Path pathDest : pathsImCloser) {
                if (path != pathDest) {
                    Point dest = pathDest.getEnd();
                    Path newPath = calculateShortestPath(field, path.getEnd(), dest, null, null, null, null, false);
                    if (distance == null || distance > (path.getDistance() + newPath.getDistance())) {
                        distance = path.getDistance() + newPath.getDistance();
                        bestPath = path;
                    }
                }
            }
        }
        return bestPath.getMoves().get(0);
    }

    public static Path calculateShortestPath(Field field, Point start, Point end, Integer maxDistance, Boolean iHaveWeapon, Boolean opponentWithWeapon, String opponentId, boolean myMove) {
        bestPathDistance = -1;
        Integer[][] visited = initMatrix(field.getWidth(), field.getHeight());
        List<Path> solutions = new ArrayList<>();
        calculateMinimumDistanceAux(field, start, start, end, new ArrayList<MoveType>(), solutions, maxDistance, visited, iHaveWeapon, opponentWithWeapon, opponentId, myMove);
        //System.out.println(callsNumber);
        return getBestSolution(solutions);
    }

    private static void calculateMinimumDistanceAux(Field field, Point start, Point actual, Point end, List<MoveType> moves, List<Path> solutions, Integer maxDistance, Integer[][] visited, Boolean iHaveWeapon, Boolean opponentWithWeapon, String opponentId, boolean myMove) {
        //BUG or opponent
        if (!moves.isEmpty() && isThereABugOrOpponentWithWeapon(field, actual, opponentId, opponentWithWeapon)) {
            if (iHaveWeapon != null && iHaveWeapon) {
                iHaveWeapon = false;
            } else if (myMove){
                // Penalty
                if (moves.size() > 2) {
                    moves.add(MoveType.PASS);
                    moves.add(MoveType.PASS);
                    moves.add(MoveType.PASS);
                } else {
                    return;
                }
            }
        }

        // Path found
        if (actual.equals(end)) {
            //System.out.println("Path found distance " + moves.size() + " moves: " + moves);
            solutions.add(new Path(start, end, moves));
            bestPathDistance = moves.size();
            return ;
        }

        // There is a better path or too long
        if (visited[actual.x][actual.y] < moves.size()
                || (maxDistance != null && maxDistance < moves.size())
                || (bestPathDistance != -1 && bestPathDistance < moves.size())) {
            return;
        }

        visited[actual.x][actual.y] = moves.size();

        List<MoveType> bestDirections = getMovesPriority(actual, end);

        for (MoveType move : bestDirections) {
            if (canGoInDirection(field, actual, move)) {
                // First move
                if (myMove && moves.isEmpty()) {
                    if (isDangerous(field, move(actual, move), opponentWithWeapon, opponentId)) {
                        if (!iHaveWeapon) {
                            continue;
                        }
                    }
                }
                List<MoveType> newMoves = new ArrayList<>(moves);
                newMoves.add(move);
                calculateMinimumDistanceAux(field, start, move(actual, move), end, newMoves, solutions, maxDistance, visited, iHaveWeapon, opponentWithWeapon, opponentId, myMove);
            }
        }
    }

    private static boolean isDangerous(Field field, Point position, Boolean opponentWithWeapon, String opponentId) {
        List<MoveType> moveTypes = field.getValidMoveTypes(position);
        for (MoveType moveType : moveTypes) {
            if (isThereABugOrOpponentWithWeapon(field, move(position, moveType), opponentId, opponentWithWeapon))
                return true;
        }
        return false;
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

    private static Path getBestSolution(List<Path> solutions) {
        if (solutions.isEmpty()) return null;
        solutions.sort(new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                return o1.getDistance() - o2.getDistance();
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
                bestDirections.add(MoveType.DOWN);
            } else if (y < 0){
                bestDirections.add(MoveType.UP);
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
                bestDirections.add(MoveType.DOWN);
            } else if (y < 0){
                bestDirections.add(MoveType.UP);
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

    private static boolean isThereABugOrOpponentWithWeapon(Field field, Point actual, String opponentId, Boolean opponentWithWeapon) {
        return field.getField()[actual.x][actual.y].contains(Field.BUG)
                || (opponentId != null && field.getField()[actual.x][actual.y].contains(opponentId)
                    && opponentWithWeapon != null && opponentWithWeapon);
    }
}
