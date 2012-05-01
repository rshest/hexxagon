package com.rush.hexxagon;

public class Move {
    public Move(int _from, int _to) {
        from = _from;
        to = _to;
    }

    public int from;
    public int to;
    public int value = 0;
}
