package com.quan_ly.spring.controllers;

import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/login" })
    public ModelAndView login( ){
        ModelAndView mv = new ModelAndView("public/login");
        return mv;
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            Model model,
                            HttpSession session) {
        Optional<User> userOptional = userService.loginUser(email, password);

        if (userOptional.isPresent()) {
            session.setAttribute("user", userOptional.get());
            return "redirect:/user/home"; // Điều hướng đến trang chủ
        } else {
            model.addAttribute("error", "Email or password incorrect");
            return "public/login"; // Quay lại trang login với thông báo lỗi
        }
    }
}
