package plozdev.todolistapi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("token") == null)
            return "redirect:/login";

        return "index";
        
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
}
