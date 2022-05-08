package com.hoo.community.exception;

/*
* ICustomizeErrorCode
* 定义错误码类型与接口
* */
public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND(2001, "你找到问题不在了，要不要换个试试？"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NO_LOGIN(2003, "当前操作需要登录，请登陆后重试"),
    SYS_ERROR(2004, "服务冒烟了，要不然你稍后再试试！！！"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在了，要不要换个试试？"),
    CONTENT_IS_EMPTY(2007, "输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "兄弟你这是在读别人的信息呢！"),
    NOTIFICATION_NOT_FOUND(2009, "消息飞了！");


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    private String message;
    private Integer code;

    //构造器
    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
