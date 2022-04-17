package com.hoo.community.controller;

import com.hoo.community.dto.QuestionDTO;
import com.hoo.community.mapper.UserMapper;
import com.hoo.community.model.Question;
import com.hoo.community.model.User;
import com.hoo.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
                        Model model) {
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
        // 利用Service层的questionService获取组装好的questionList列表
        List<QuestionDTO> questionList = questionService.list();
        // 利用Model将组装好的数据返回到前端
        model.addAttribute("questions",questionList);
        return "index";
    }
}
