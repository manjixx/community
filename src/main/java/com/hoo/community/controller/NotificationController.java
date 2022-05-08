package com.hoo.community.controller;

import com.hoo.community.dto.NotificationDTO;
import com.hoo.community.dto.PaginationDTO;
import com.hoo.community.enums.NotificationTypeEnum;
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

@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    // 动态切换路径来实现内容和样式的切换
    @RequestMapping("/notification/{id}")
    public String profile(
            HttpServletRequest request,
            @PathVariable(name = "id") Long id){
        User user = (User) request.getSession().getAttribute("user");
        // 如果用户为空则返回登录页面
        if(user == null){
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id,user);
        if(NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
        || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()){
            return "redirect:/question/" + notificationDTO.getOuterid();
        }else{
            return "redirect:/";
        }
    }
}
