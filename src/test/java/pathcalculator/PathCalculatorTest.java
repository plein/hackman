package pathcalculator;

import field.Field;
import move.MoveType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Plein on 01/12/2016.
 */
public class PathCalculatorTest {
    public static Field field;

    public static final String[][] FIELD = new String[][]{
            {".",".",".",".",".",".",".",".",".",".",".",".",".",".",".",".",".",".",".","."},
            {".","x","x","x","x","x",".","x","x","x","x","x","x",".","x","x","x","x","x","."},
            {".","x",".",".",".",".",".","x","x","x","x","x","x",".",".",".",".",".","x","."},
            {".","x",".","x","x","x",".",".",".","x","x",".",".",".","x","x","x",".","x","."},
            {".",".",".",".",".","x","x","x",".","x","x",".","x","x","x",".",".",".",".","."},
            {".","x","x","x",".","x",".",".",".",".",".",".",".",".","x",".","x","x","x","."},
            {".",".",".","x",".","x",".","x","x","x","x","x","x",".","x",".","x",".",".","."},
            {"x","x",".","x",".",".",".","x","x","x","x","x","x",".",".",".","x",".","x","x"},
            {".",".",".","x","x","x",".","x","x","x","x","x","x",".","x","x","x",".",".","."},
            {".","x",".",".",".",".",".",".",".",".",".",".",".",".",".",".",".",".","x","."},
            {".","x","x","x",".","x","x","x","x","x","x","x","x","x","x",".","x","x","x","."},
            {".","x","x","x",".",".",".",".",".",".",".",".",".",".",".",".","x","x","x","."},
            {".","x","x","x",".","x","x","x",".","x","x",".","x","x","x",".","x","x","x","."},
            {".",".",".",".",".",".",".",".",".","x","x",".",".",".",".",".",".",".",".","."}};

    @BeforeClass
    public static void init() throws Exception {
        field = new Field();
        field.setWidth(20);
        field.setHeight(14);
        field.initField();
        field.setMyId(0);
        field.setOpponentId(1);
        field.parseFromString("C,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,x,x,x,x,.,x,x,x,x,x,x,.,x,x,x,x,x,.,.,x,.,.,.,.,.,x,x,x,x,x,x,.,.,.,.,.,x,.,.,x,.,x,x,x,.,.,.,x,x,.,.,.,x,x,x,.,x,.,.,.,.,.,.,x,x,x,.,x,x,.,x,x,x,.,.,.,.,.,.,x,x,x,.,x,.,.,.,.,.,.,.,.,x,.,x,x,x,.,.,.,.,x,.,x,.,x,x,x,x,x,x,.,x,.,x,.,.,.,x,x,0,x,.,.,.,x,x,x,x,x,x,.,.,.,x,1,x,x,.,.,.,x,x,x,.,x,x,x,x,x,x,.,x,x,x,.,.,.,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,.,x,x,x,.,x,x,x,x,x,x,x,x,x,x,.,x,x,x,.,.,x,x,x,.,.,.,.,.,.,.,.,.,.,.,.,x,x,x,.,.,x,x,x,.,x,x,x,.,x,x,.,x,x,x,.,x,x,x,.,.,.,.,.,.,.,.,.,.,x,x,.,.,.,.,.,.,.,.,C");
    }

    @Test
    public void testMinimunDistance() throws Exception {
        int distance = PathCalculator.calculateShortestPath(field, new Point(5, 0), new Point(17, 0)).size();
        Assert.assertSame(12, distance);
    }

    @Test
    public void testMinimunDistance2() throws Exception {
        int distance = PathCalculator.calculateShortestPath(field, new Point(0, 5), new Point(17, 3)).size();
        Assert.assertSame(25, distance);
    }

    @Test
    public void testMinimunDistance3() throws Exception {
        int distance = PathCalculator.calculateShortestPath(field, new Point(2, 8), new Point(17, 3)).size();
        Assert.assertSame(22, distance);
    }

    @Test
    public void testChooseBestMove() throws Exception {
        List<List<MoveType>> moves = new ArrayList<>();
        java.util.List<MoveType> moves1 = new ArrayList<>();
        moves1.add(MoveType.UP);
        moves.add(moves1);

        java.util.List<MoveType> moves2 = new ArrayList<>();
        moves2.add(MoveType.DOWN);
        moves2.add(MoveType.RIGHT);
        moves.add(moves2);

        MoveType bestMove = PathCalculator.chooseBestMove(moves);
        Assert.assertSame(MoveType.UP, bestMove);
    }

    @Test
    public void testCanGoDirection() throws Exception {
        Assert.assertFalse(PathCalculator.canGoInDirection(field,  new Point(7, 2), MoveType.RIGHT));
    }
}
