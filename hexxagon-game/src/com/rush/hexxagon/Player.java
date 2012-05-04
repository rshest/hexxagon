package com.rush.hexxagon;

public class Player {
    private byte mColor;

    public Player(byte color) {
        mColor = color;
    }

    public byte getColor() {
        return mColor;
    }

    public void startMove(HexxagonGame game) {
    }

    public void onClickCell(int cellIdx, HexxagonGame game) {
    }
}
