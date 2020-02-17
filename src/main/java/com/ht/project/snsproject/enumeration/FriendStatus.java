package com.ht.project.snsproject.enumeration;

public enum FriendStatus {
    REQUEST(1), FRIEND(2), BLOCK(3), NONE(4);

    public final int value;

    FriendStatus(int value) {
        this.value = value;
    }

    public static FriendStatus valueOf(int value){
        switch (value){
            case 1: return REQUEST;
            case 2: return FRIEND;
            case 3: return BLOCK;
            case 4: return NONE;
            default: throw new AssertionError("Unknown value: " + value);
        }
    }
}
