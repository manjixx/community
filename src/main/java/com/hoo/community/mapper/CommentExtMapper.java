package com.hoo.community.mapper;

import com.hoo.community.model.Comment;


public interface CommentExtMapper {
    int incCommentCount(Comment record);
}