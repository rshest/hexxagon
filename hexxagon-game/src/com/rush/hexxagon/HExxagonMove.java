package com.rush.hexxagon;

public class HexxagonMove {
    public HexxagonMove(short _from, short _to) {
        from = _from;
        to = _to;
    }

    public short from;
    public short to;
    public int value = -HexxagonBoard.BIG_VALUE;
}
