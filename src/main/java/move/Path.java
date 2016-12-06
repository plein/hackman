package move;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Plein on 06/12/2016.
 */
public class Path {
    private final Point start;
    private final Point end;
    java.util.List<MoveType> moves;

    public Path(Point start, Point end, List<MoveType> moves) {
        this.start = start;
        this.end = end;
        this.moves = new ArrayList<>(moves);
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public List<MoveType> getMoves() {
        return moves;
    }

    public int getDistance() {
        return moves.size();
    }
}
