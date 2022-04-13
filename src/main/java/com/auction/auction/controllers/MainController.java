package com.auction.auction.controllers;

import com.auction.auction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping("/auction/home")
    public String HomePage(Model model) {
        model.addAttribute("name", "Ивак");
        return "homePage";
    }
    @GetMapping("/auction/test")
    public String test(Model model) {

        return "test";
    }

}