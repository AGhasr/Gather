package org.example.eventregistration.controller;

import jakarta.validation.Valid;
import org.example.eventregistration.model.User;
import org.example.eventregistration.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user, BindingResult result) {
        // 1. Standard Validation (Empty fields, etc.)
        if (result.hasErrors()) {
            return "register";
        }

        // 2. Custom Validation: Check if Username exists
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }

        // 3. Custom Validation: Check if Email exists (NEW)
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email is already registered");
            return "register";
        }

        // 4. Save with Email
        // Note: passing "USER" as default role
        userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail(), "USER");

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}