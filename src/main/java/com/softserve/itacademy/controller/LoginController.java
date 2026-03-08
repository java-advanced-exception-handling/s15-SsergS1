package com.softserve.itacademy.controller;

import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("user_id") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam("username") String email,
                            @RequestParam("password") String password,
                            HttpSession session
    ) {
        var userOpt = userService.findByUsername(email);
        if (userOpt.isEmpty()) return "redirect:/login?error=true";
        var user = userOpt.get();
        if (user.getPassword().equals("{noop}" + password)) {
            session.setAttribute("username", user.getFirstName());
            session.setAttribute("user_id", user.getId());
            return "redirect:/";
        } else {
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
