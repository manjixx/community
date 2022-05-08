package com.hoo.community.enums;

public enum NotificationStatusEnum {
    READ(1),UNREAD(0);
    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
