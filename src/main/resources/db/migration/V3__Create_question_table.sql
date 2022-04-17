create table question
(
    id long auto_increment,
    title varchar(50),
    description text,
    gmt_create BIGINT,
    gmt_modified BIGINT,
    creator INT,
    comment_count int default 0,
    view_count int default 0,
    like_count int default 0,
    tag VARCHAR(256),
    constraint QUESTION_PK
        primary key (id)
);

