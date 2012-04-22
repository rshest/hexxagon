package com.rush.hexxagon;

import java.util.ArrayList;
import java.util.Random;

public class AIPlayer extends Player {

    public AIPlayer(byte color) {
        super(color);
    }

    public void startMove(Game game) {
        GameBoard board = game.getBoard();
        ArrayList<Move> moves = board.getPossibleMoves(getColor());

        System.out.format("Num moves: %d; ", moves.size());

        byte color = getColor();
        if (moves.size() > 0) {
            Random rnd = new Random();
            int bestValue = Integer.MIN_VALUE;
            int nMax = 0;
            Move bestMove = null;
            for (Move move : moves) {
                GameBoard b = new GameBoard(board);
                b.move(move.from, move.to);
                int value = b.getValue(color);
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                    nMax = 1;
                } else if (value == bestValue) {
                    // perform "reservoir sampling" to choose the random move
                    // from the best ones
                    nMax++;
                    if (rnd.nextInt(nMax) == 0) {
                        bestMove = move;
                    }
                }
            }
            // Move move = moves.get(rnd.nextInt(moves.size()));
            System.out
                    .format("Best move: %d->%d; ", bestMove.from, bestMove.to);
            board.move(bestMove.from, bestMove.to);
            System.out.format("Best value: %d\n", bestValue);
        }

        game.nextMove();
    }

}
