package com.hoo.community.service;

import com.hoo.community.dto.PaginationDTO;
import com.hoo.community.dto.QuestionDTO;
import com.hoo.community.exception.CustomizeErrorCode;
import com.hoo.community.exception.CustomizeException;
import com.hoo.community.mapper.QuestionExtMapper;
import com.hoo.community.mapper.QuestionMapper;
import com.hoo.community.mapper.UserMapper;
import com.hoo.community.model.Question;
import com.hoo.community.model.QuestionExample;
import com.hoo.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;


    public PaginationDTO list(Integer page, Integer size) {
        // 存放拼接结果
        PaginationDTO paginationDTO = new PaginationDTO();
        // 获取总页数
        Integer totalPage;

        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());

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
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample,new RowBounds(offset,size));
        List<QuestionDTO> questionDTOList =  new ArrayList<>();
        for (Question question : questions) {
            // 根据question表中的用户id去查询user表中的用户，并返回用户
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            // new 一个questionDTO，存放查询结果
            QuestionDTO questionDTO = new QuestionDTO();
            // 将question中的属性拷贝到questionDTO

            BeanUtils.copyProperties(question,questionDTO);

            // 将查询到到user放入questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return  paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        // 存放拼接结果
        PaginationDTO paginationDTO = new PaginationDTO();
        // 获取总页数
        Integer totalPage;

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);

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
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));

        List<QuestionDTO> questionDTOList =  new ArrayList<>();
        for (Question question : questions) {
            // 根据question表中的用户id去查询user表中的用户，并返回用户,该句式为P34之后更新的内容
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            // new 一个questionDTO，存放查询结果
            QuestionDTO questionDTO = new QuestionDTO();
            // 将question中的属性拷贝到questionDTO
            BeanUtils.copyProperties(question,questionDTO);
            // 将查询到到user放入questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return  paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        // 利用id信息获取该问题
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        // 返回前端的questionDTO
        QuestionDTO questionDTO = new QuestionDTO();
        // 将查询所得question放入questionDTO
        BeanUtils.copyProperties(question,questionDTO);
        // 利用question中的creator获取user信息
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        // 将user信息放入questionDTO中
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            // create question
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }else{
            // update question
            question.setGmtModified(question.getGmtCreate());
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion,example);
            if(updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            questionMapper.updateByExampleSelective(updateQuestion,example);
        }
    }

    public void incView(Long id) {
        // 首先获取当前问题的浏览数，然后将updateQuestion的浏览数+1
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        // 拼接好的正则表达式
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionExtMapper.selectRelated(question);
        // 将查询得到的question列表转换为QuestionDTO
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
