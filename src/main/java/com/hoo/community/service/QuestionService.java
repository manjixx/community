package com.hoo.community.service;

import com.hoo.community.dto.PaginationDTO;
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


    public PaginationDTO list(Integer page, Integer size) {
        // 存放拼接结果
        PaginationDTO paginationDTO = new PaginationDTO();
        // 获取总页数
        Integer totalPage;

        Integer totalCount = questionMapper.count();

        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }

        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage,page);
        // 偏移量 offset = size * (page - 1)
        Integer offset = size * (page - 1);
        // 存放从question表中查询的结果
        List<Question> questions = questionMapper.list(offset,size);
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
        paginationDTO.setQuestions(questionDTOList);

        return  paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        // 存放拼接结果
        PaginationDTO paginationDTO = new PaginationDTO();
        // 获取总页数
        Integer totalPage;

        Integer totalCount = questionMapper.countByUserId(userId);

        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }

        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage,page);

        // 偏移量 offset = size * (page - 1)
        Integer offset = size * (page - 1);
        // 存放从question表中查询的结果
        List<Question> questions = questionMapper.listByUserID(userId,offset,size);
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
        paginationDTO.setQuestions(questionDTOList);

        return  paginationDTO;
    }
}
