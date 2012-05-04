package com.rush.hexxagon.test;

import com.rush.hexxagon.HexGridCell;
import org.junit.Assert;
import org.junit.Test;

public class HexGridCellTest {
    @Test
    public void testWalkDistance() throws Exception {
        final int X1 = 13;
        final int X2 = 14;
        final int Y1 = 42;

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

    static private int max(int[] arr) {
        int res = Integer.MIN_VALUE;
        for (int v: arr) res = Math.max(res, v);
        return res;
    }

    static private int min(int[] arr) {
        int res = Integer.MAX_VALUE;
        for (int v: arr) res = Math.min(res, v);
        return res;
    }

    static HexGridCell.Extents getAreaExtentsHardWay(HexGridCell hc, int cl, int ct, int cr, int cb) {
        int[] cx = new int[HexGridCell.NUM_CORNERS];
        int[] cy = new int[HexGridCell.NUM_CORNERS];

        hc.setCellIndex(cl, ct);
        hc.computeCorners(cx, cy);
        int x = min(cx);
        int y = min(cy);

        hc.setCellIndex(cr, cb);
        hc.computeCorners(cx, cy);
        int w = max(cx) - x;
        int h = max(cy) - y;

        return new HexGridCell.Extents(x, y, w, h);
    }

    @Test
    public void testGetAreaExtents() throws Exception {
        HexGridCell hc = new HexGridCell(10);

        final int CL = 7;
        final int CT = 5;
        final int CR = 19;
        final int CB = 20;

        HexGridCell.Extents ext = hc.getAreaExtents(CL, CT, CR, CB);
        HexGridCell.Extents ext1 = getAreaExtentsHardWay(hc, CL, CT, CR, CB);

        Assert.assertEquals(ext1.x, ext.x);
        Assert.assertEquals(ext1.w, ext.w);

        Assert.assertEquals(ext1.y, ext.y);
        Assert.assertEquals(ext1.h, ext.h);

        ext = hc.getAreaExtents(CL + 1, CT + 2, CR + 6, CB + 1);
        ext1 = getAreaExtentsHardWay(hc, CL + 1, CT + 2, CR + 6, CB + 1);

        Assert.assertEquals(ext1.x, ext.x);
        Assert.assertEquals(ext1.w, ext.w);

        Assert.assertEquals(ext1.y, ext.y);
        Assert.assertEquals(ext1.h, ext.h);
    }
}
