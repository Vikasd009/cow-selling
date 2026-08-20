package com.vikas.cowselling.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public String test(){
        return "You are authenticated Successfully";
    }

    @GetMapping("/seller")
    public String sellerTest(){
        return "Welcome Seller!";
    }

    @GetMapping("/admin")
    public String adminTest(){
        return "Welcome Admin!";
    }
}
