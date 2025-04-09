package com.quan_ly.spring.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class DocumentController {
    @GetMapping({"/document"})
    public ModelAndView project(HttpSession session) {
        return new ModelAndView("public/document");
    }
}
