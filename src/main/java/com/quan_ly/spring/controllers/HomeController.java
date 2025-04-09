package com.quan_ly.spring.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
    @GetMapping({"/", "/user/home"})
    public ModelAndView index(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return new ModelAndView("public/home");
        }
        return new ModelAndView(new RedirectView("/auth/login"));
    }
}
