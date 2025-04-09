package com.quan_ly.spring.controllers;

import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.enums.Role;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MessageSource messageSource;
    @Autowired
    UserService userService;

    @GetMapping({"/account"})
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "public/user";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "user/form";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        if (userService.emailExists(user.getEmail())) {
            // Email đã tồn tại
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE, messageSource.getMessage("email_exited", null, Locale.getDefault()));
            return "redirect:/user/account";
        }

        userService.createUser(user);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE, messageSource.getMessage("create_success", null, Locale.getDefault()));
        return "redirect:/user/account";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return userService.getUserById(id).map(user -> {
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            return "user/form";
        }).orElse("redirect:/users/accounts");
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") User updatedUser,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        Optional<User> optionalExistingUser = userService.getUserById(id);

        if (optionalExistingUser.isEmpty()) {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE,messageSource.getMessage("user_not_found", null, Locale.getDefault()));
            return "redirect:/user/account";
        }

        User existingUser = optionalExistingUser.get();

        // Kiểm tra email đã tồn tại và không phải của user hiện tại
        Optional<User> userWithSameEmail = userService.getUserByEmail(updatedUser.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getUserId().equals(id)) {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE, messageSource.getMessage("email_exited", null, Locale.getDefault()));
            return "redirect:/user/account";
        }

        // Nếu không nhập mật khẩu mới, giữ nguyên passwordHash
        if (updatedUser.getPasswordHash() == null || updatedUser.getPasswordHash().isBlank()) {
            updatedUser.setPasswordHash(existingUser.getPasswordHash());
        }

        userService.updateUser(id, updatedUser);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE, messageSource.getMessage("edit_success", null, Locale.getDefault()));
        return "redirect:/user/account";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE, messageSource.getMessage("delete_success", null, Locale.getDefault()));
        return "redirect:/user/account";
    }
}
