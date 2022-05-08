package com.hoo.community.enums;

public enum CommentTypeEnum {
    QUESTION(1),
    COMMENT(2);
    private Integer type;


    // 获得Type方法
    public Integer getType() {
        return type;
    }

    CommentTypeEnum(Integer type) {
        this.type = type;
    }

    public static boolean isExist(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if (commentTypeEnum.getType() == type) {
                return true;
            }
        }
        return false;
    }
}
