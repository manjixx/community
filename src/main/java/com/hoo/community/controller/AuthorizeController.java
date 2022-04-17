package com.hoo.community.controller;

import com.hoo.community.dto.AccessTokenDTO;
import com.hoo.community.dto.GithubUser;
import com.hoo.community.mapper.UserMapper;
import com.hoo.community.model.User;
import com.hoo.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String  redirectUri;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        // 利用获取得到的code值，获取token值
        String  accessToken = githubProvider.getAccessToken(accessTokenDTO);
        // 利用得到的token获取用户信息
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if(githubUser != null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            // 此处将token存入数据库的过程已经完成写session的过程，因此仅需要将token写入cookie 即可
            response.addCookie(new Cookie("token",token));
            // request.getSession().setAttribute("githubUser",githubUser);  登录成功 自动写入cookie 和 session

        }else{
            // 登录失败，重新登录
        }
        return "redirect:/";    // 重定向，跳转回主页
    }
}
