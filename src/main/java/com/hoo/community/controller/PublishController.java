package com.hoo.community.controller;

import com.hoo.community.cache.TagCache;
import com.hoo.community.dto.QuestionDTO;
import com.hoo.community.mapper.QuestionMapper;
import com.hoo.community.model.Question;
import com.hoo.community.model.User;
import com.hoo.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id") Long id,
                       Model model){
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        // 获取问题的id 返回给publish中，用于判断当前问题使用create 还是update
        model.addAttribute("id",question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";

    }


    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title",required = false)String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(value = "id",required = false) Long id,
            HttpServletRequest request,
            Model model){           // 如果服务端从接口传递数据到页面中去需要将数据写入model中去

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());

        if(title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }

        if(description == null || description == ""){
            model.addAttribute("error","补充不能为空");
            return "publish";
        }

        if(tag == null || tag == ""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        // invalid tags
        String invalid = TagCache.filterInvalid(tag);
        if(StringUtils.isNoneBlank(invalid)){
            model.addAttribute("error","输入非法标签" + invalid);
            return "publish";
        }
        // 从request中获得cookie，然后利用token获取用户信息
        // 如果用户信息存在，则绑定到session去
        // 如果用户不存在，则显示用户为登录到publish页面去
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            model.addAttribute("error","用户未登录");
            // 前后端分离项目可以局部刷新，而非前后端分离项目，只能按照旧方式，点击发布后请求到服务端
            // 成功则响应请求，否则返回旧页面
            return "publish";
        }
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        // 插入到数据库
        questionService.createOrUpdate(question);
        // 如果成功则返回首页
        return "redirect:/";

    }
}
