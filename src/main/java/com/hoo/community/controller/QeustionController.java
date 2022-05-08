package com.hoo.community.controller;

import com.hoo.community.dto.CommentDTO;
import com.hoo.community.dto.QuestionDTO;
import com.hoo.community.enums.CommentTypeEnum;
import com.hoo.community.service.CommentService;
import com.hoo.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QeustionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model){
        // 获得问题
        QuestionDTO questionDTO = questionService.getById(id);

        // 获取相关问题
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);

        // 获取问题下得回复列表
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        // 累加阅读数
        questionService.incView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        model.addAttribute("relatedQuestions",relatedQuestions);
        return "question";
    }
}
