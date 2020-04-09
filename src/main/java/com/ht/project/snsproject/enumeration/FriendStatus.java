package com.ht.project.snsproject.enumeration;

public enum FriendStatus {
    REQUEST(1), RECEIVE(2), FRIEND(3), BLOCK(4), NONE(5), ME(6);

    public final int value;

    FriendStatus(int value) {
        this.value = value;
    }

    public static FriendStatus valueOf(int value){
        switch (value){
            case 1: return REQUEST;
            case 2: return RECEIVE;
            case 3: return FRIEND;
            case 4: return BLOCK;
            case 5: return NONE;
            case 6: return ME;
            default: throw new AssertionError("Unknown value: " + value);
        }
    }
}
