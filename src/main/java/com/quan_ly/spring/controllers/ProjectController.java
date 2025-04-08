package com.quan_ly.spring.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProjectController {

    @GetMapping({"/project"})
    public ModelAndView project(HttpSession session) {
        return new ModelAndView("public/project");
    }
}
