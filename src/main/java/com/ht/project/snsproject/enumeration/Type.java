package com.ht.project.snsproject.enumeration;

public enum Type {

    FRIEND_REQ(1), FRIEND_RES(2), COMMENT(3), LIKE(4);

    private final int value;

    Type(int value){
        this.value = value;
    }

    public static Type valueOf(int value){
        switch (value){
            case 1: return FRIEND_REQ;
            case 2: return FRIEND_RES;
            case 3: return COMMENT;
            case 4: return LIKE;
            default: throw new AssertionError("Unknown value: " + value);
        }
    }
}
