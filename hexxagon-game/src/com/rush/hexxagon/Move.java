package com.rush.hexxagon;

public class Move {
    public Move(short _from, short _to) {
        from = _from;
        to = _to;
    }

    public short from;
    public short to;
    public int value = Integer.MIN_VALUE;
}
