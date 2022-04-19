package com.hoo.community.mapper;

import com.hoo.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question(title,description,gmt_create,gmt_modified,creator,tag) " +
            "values(#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void create(Question question);
    @Select("select * from question limit #{offset} ,#{size}")
    List<Question> list(Integer offset, Integer size);

    @Select("Select count(1) from question")
    Integer count();

    @Select("select * from question where creator = #{userId} limit #{offset} ,#{size}")
    List<Question> listByUserID(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("size") Integer size);

    @Select("Select count(1) from question where creator = #{userId}")
    Integer countByUserId(@Param("userId") Integer userId);
}
