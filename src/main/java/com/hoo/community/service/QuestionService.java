package com.hoo.community.service;

import com.hoo.community.dto.QuestionDTO;
import com.hoo.community.mapper.QuestionMapper;
import com.hoo.community.mapper.UserMapper;
import com.hoo.community.model.Question;
import com.hoo.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;

    public List<QuestionDTO> list() {
        // 存放从question表中查询的结果
        List<Question> questions = questionMapper.list();
        // 存放拼接结果
        List<QuestionDTO> questionDTOList =  new ArrayList<>();

        for (Question question : questions) {
            // 根据question表中的用户id去查询user表中的用户，并返回用户
            User user = userMapper.findById(question.getCreator());
            // new 一个questionDTO，存放查询结果
            QuestionDTO questionDTO = new QuestionDTO();
            // 将question中的属性拷贝到questionDTO
            BeanUtils.copyProperties(question,questionDTO);
            // 将查询到到user放入questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        return  questionDTOList;
    }

    }
