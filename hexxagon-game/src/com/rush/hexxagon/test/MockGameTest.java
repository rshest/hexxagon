package com.rush.hexxagon.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class MockGameTest {

    private static final String GAME_TREE =
            "B.D.1=6, B.D.2=5, B.D.3=3, B.E.1=7, B.E.2=0, " +
            "C.F.1=1, C.F.2=4, C.F.3=2, C.G.1=8, C.G.2=9";

    MockGame mGame = new MockGame(GAME_TREE);

    @Test
    public void testParsing() throws Exception {
        final byte playerID = 0;

        ArrayList<GameMove> rootMoves = mGame.mRootBoard.getPossibleMoves(playerID);
        Assert.assertEquals(2, rootMoves.size());

        MockGame.MockBoard b = (MockGame.MockBoard) mGame.mRootBoard.clone();
        b.move(rootMoves.get(0));

        ArrayList<GameMove> moves1 = b.getPossibleMoves(b.getOtherPlayerID(playerID));
        Assert.assertEquals(2, moves1.size());

        b.move(moves1.get(0));
        ArrayList<GameMove> moves2 = b.getPossibleMoves(playerID);
        Assert.assertEquals(3, moves2.size());

        b.move(moves2.get(1));
        Assert.assertEquals(0, b.getPossibleMoves(b.getOtherPlayerID(playerID)).size());
        Assert.assertEquals(5, b.evaluate(b.getOtherPlayerID(playerID), true));
    }

    @Test
    public void testFind() throws Exception {
        Assert.assertEquals(9, (int)mGame.mRootBoard.position.find("C.G.2").value);
    }
}
