package com.hoo.community.controller;

import com.hoo.community.dto.PaginationDTO;
import com.hoo.community.dto.QuestionDTO;
import com.hoo.community.mapper.UserMapper;
import com.hoo.community.model.Question;
import com.hoo.community.model.User;
import com.hoo.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    UserMapper userMapper;
    @Autowired
    QuestionService questionService;

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if(user != null){
                        request.getSession().setAttribute("user",user);
                    }
                    break;
                }
            }
        }
        // 利用Service层的questionService获取组装好的pagination列表
        PaginationDTO pagination = questionService.list(page,size);
        // 利用Model将组装好的数据返回到前端
        model.addAttribute("pagination",pagination);
        return "index";
    }
}
