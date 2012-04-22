package com.rush.hexxagon;

public interface Platform {
    public interface Callback{
        void call();
    }

    public void repaint();
    public void popup(String message, Callback onClosedCallback);
}
