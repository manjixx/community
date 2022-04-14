package com.hoo.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunityApplication {

    public static void main(String[] args) {
        try{
            SpringApplication.run(CommunityApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
