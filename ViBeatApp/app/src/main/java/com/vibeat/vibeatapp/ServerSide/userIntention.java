package com.vibeat.vibeatapp.ServerSide;

public enum userIntention {
    PLAY_BUTTON(0),
    NEXT_BUTTON(1),
    ON_COMPLETION(2);

    private int type;

    userIntention(int type) {
        this.type = type;
    }

    public int getInt() {
        return type;
    }
}

