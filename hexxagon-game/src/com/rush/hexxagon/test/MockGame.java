package com.rush.hexxagon.test;

import java.util.ArrayList;
import java.util.Hashtable;

class MockGame {
    public static class MockMove implements GameMove {
        protected Position position;
    }

    public static class MockBoard implements GameBoard {
        MockGame mGame;
        protected Position position = new Position();

        MockBoard(MockGame game) {
            mGame = game;
        }

        @Override
        public ArrayList<GameMove> getPossibleMoves(byte playerID) {
            position.visited = true;
            ArrayList<GameMove> moves = new ArrayList<GameMove>();
            for (Position p: position.children) {
                MockMove move = new MockMove();
                move.position = p;
                moves.add(move);
            }
            return moves;
        }

        @Override
        public int evaluate(byte playerID, boolean canMove) {
            return position.value;
        }

        @Override
        public boolean move(GameMove move) {
            position = ((MockMove)move).position;
            return true;
        }

        @Override
        public byte getOtherPlayerID(byte playerID) {
            return (byte) ((playerID + 1)%2);
        }

        @Override
        public void copy(GameBoard board) {
            position = ((MockBoard)board).position;
        }

        @Override
        public GameBoard clone() {
            MockBoard b = new MockBoard(mGame);
            b.position = position;
            return b;
        }
    }

    public static class Position {
        public String               id;
        public Position             parent;
        public ArrayList<Position>  children = new ArrayList<Position>();
        public Integer              value;
        public boolean              visited = false;

        public Position find(String path) {
            if (id.equals(path)) {
                return this;
            } else {
                int dotIdx = path.indexOf('.');
                String head = (dotIdx < 0) ? path : path.substring(0, dotIdx);
                String newPath = path.substring(dotIdx + 1);
                for (Position p: children) {
                    if (p.id.equals(head)) {
                        Position curP = p.find(newPath);
                        if (curP != null) return curP;
                    }
                }
                return null;
            }
        }

        public int countVisited(boolean bVisited) {
            int res = (visited == bVisited) ? 1 : 0;
            for (Position p: children) res += p.countVisited(bVisited);
            return res;
        }

        public void resetVisited(boolean val) {
            visited = val;
            for (Position p: children) p.resetVisited(val);
        }
    }

    protected MockBoard mRootBoard = new MockBoard(this);

    /**
     *  Example string encoding the game tree:
     *  "B.D.1=6, B.D.2=5, B.D.3=3, B.E.1=7, B.E.2=0, C.F.1=1, C.F.2=4, C.F.3=2, C.G.1=8, C.G.2=9"
     *  Corrensponds to the tree:
     *          .
     *         B C
     *    D E      F G
     *  123  12  123  12 <- leaves
     *  ---  --  ---  --
     *  653  70  142  89 <- leave values
     */
    MockGame(String gameTree) {
        //  parse leaves
        String[] lines = gameTree.split(",");
        Hashtable<String, Position> posTable = new Hashtable<String, Position>();
        for (String l: lines) {
            String[] lr = l.trim().split("=");
            assert lr.length == 2;
            String path = lr[0].trim();

            Position pos = new Position();
            pos.value = Integer.parseInt(lr[1]);
            insertPosition(pos, path, posTable);
        }
        mRootBoard.position = posTable.get("");
    }

    private void insertPosition(Position pos, String path, Hashtable<String, Position> posTable) {
        if (path.length() == 0) {
            //  the root node
            pos.id = "";
            posTable.put("", pos);
        } else {
            // search for the parent
            int dotIdx = path.lastIndexOf('.');
            String parentPath = dotIdx > 0 ? path.substring(0, dotIdx) : "";
            pos.id = dotIdx > 0 ? path.substring(dotIdx + 1) : path;
            Position parentPos = posTable.get(parentPath);
            if (parentPos == null) {
                // no such parent yet, insert it
                parentPos = new Position();
                insertPosition(parentPos, parentPath, posTable);
                posTable.put(parentPath, parentPos);
            }
            pos.parent = parentPos;
            parentPos.children.add(pos);
        }
    }



}
