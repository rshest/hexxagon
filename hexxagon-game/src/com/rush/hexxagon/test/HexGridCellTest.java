package com.rush.hexxagon.test;

import com.rush.hexxagon.HexGridCell;
import org.junit.Assert;
import org.junit.Test;

public class HexGridCellTest {
    final int X1 = 13;
    final int X2 = 14;
    final int Y1 = 42;

    @Test
    public void testWalkDistance() throws Exception {
        for (int i = 0; i < HexGridCell.NUM_NEIGHBORS; i++) {
            Assert.assertEquals(1, HexGridCell.walkDistance(X1, Y1,
                    HexGridCell.getNeighborI(X1, Y1, i), HexGridCell.getNeighborJ(X1, Y1, i)));
            Assert.assertEquals(1, HexGridCell.walkDistance(X2, Y1,
                    HexGridCell.getNeighborI(X2, Y1, i), HexGridCell.getNeighborJ(X2, Y1, i)));
        }
        for (int i = 0; i < HexGridCell.NUM_NEIGHBORS2; i++) {
            Assert.assertEquals(2, HexGridCell.walkDistance(X1, Y1,
                    HexGridCell.getNeighbor2I(X1, Y1, i), HexGridCell.getNeighbor2J(X1, Y1, i)));
            Assert.assertEquals(2, HexGridCell.walkDistance(X2, Y1,
                    HexGridCell.getNeighbor2I(X2, Y1, i), HexGridCell.getNeighbor2J(X2, Y1, i)));
        }
    }
}
