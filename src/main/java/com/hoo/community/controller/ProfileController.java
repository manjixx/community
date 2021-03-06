package com.hoo.community.controller;

import com.hoo.community.dto.PaginationDTO;
import com.hoo.community.enums.NotificationStatusEnum;
import com.hoo.community.mapper.UserMapper;
import com.hoo.community.model.User;
import com.hoo.community.service.NotificationService;
import com.hoo.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;
    // 动态切换路径来实现内容和样式的切换
    @RequestMapping("/profile/{action}")
    public String profile(
            HttpServletRequest request,
            @PathVariable(name = "action") String action,
            @RequestParam(name = "page",defaultValue = "1") Integer page,
            @RequestParam(name = "size",defaultValue = "5") Integer size,
                          Model model){
        User user = (User) request.getSession().getAttribute("user");
        // 如果用户为空则返回登录页面
        if(user == null){
            return "redirect:/";
        }
        if("questions".equals(action)){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的问题");
            PaginationDTO paginationDTO = questionService.list(user.getId(),page,size);
            model.addAttribute("pagination",paginationDTO);
        }else if("replies".equals(action)){
            // 查询当前用户的通知列表
            PaginationDTO paginationDTO = notificationService.list(user.getId(),page,size);
            model.addAttribute("section","replies");
            model.addAttribute("pagination",paginationDTO);

            model.addAttribute("sectionName","最新回复");
        }
        return "profile";
    }
}
