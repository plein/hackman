package pathcalculator;

import field.Field;
import move.MoveType;
import move.Path;
import org.junit.Assert;
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
    public void testMinimumDistance() throws Exception {
        Path path = PathCalculator.calculateShortestPath(field, new Point(5, 0), new Point(17, 0), null, null, null, field.getOpponentId(), true);
        int distance = path.getDistance();
        Assert.assertSame(12, distance);
    }

    @Test
    public void testMinimumDistance2() throws Exception {
        Path path = PathCalculator.calculateShortestPath(field, new Point(0, 5), new Point(17, 3), null, null, null, field.getOpponentId(), true);
        int distance = path.getDistance();
        Assert.assertSame(25, distance);
    }

    @Test
    public void testMinimumDistance3() throws Exception {
        Path path = PathCalculator.calculateShortestPath(field, new Point(2, 8), new Point(17, 3), null, null, null, field.getOpponentId(), true);
        int distance = path.getDistance();
        Assert.assertSame(22, distance);
    }

    @Test
    public void testChooseBestMove() throws Exception {
        Point start = new Point(0, 5);
        Point end = new Point(0, 1);
        List<Path> paths = new ArrayList<>();
        java.util.List<MoveType> moves1 = new ArrayList<>();
        moves1.add(MoveType.UP);
        paths.add(new Path(start, end, moves1));

        java.util.List<MoveType> moves2 = new ArrayList<>();
        moves2.add(MoveType.DOWN);
        moves2.add(MoveType.RIGHT);
        paths.add(new Path(start, end, moves2));

        MoveType bestMove = PathCalculator.chooseBestMove(paths, field, false, false);
        Assert.assertSame(MoveType.UP, bestMove);
    }

    @Test
    public void testCanGoDirection() throws Exception {
        Assert.assertFalse(PathCalculator.canGoInDirection(field,  new Point(7, 2), MoveType.RIGHT));
    }
}
