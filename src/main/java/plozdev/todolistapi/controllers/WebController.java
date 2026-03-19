package plozdev.todolistapi.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import plozdev.todolistapi.dto.auth.AuthResponse;
import plozdev.todolistapi.dto.auth.LoginRequest;
import plozdev.todolistapi.dto.auth.RegisterRequest;
import plozdev.todolistapi.dto.task.TaskResponse;
import plozdev.todolistapi.exception.InvalidAuthenticationException;
import plozdev.todolistapi.exception.UserAlreadyExistsException;
import plozdev.todolistapi.services.AuthService;
import plozdev.todolistapi.services.TaskService;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final TaskService taskService;
    private final AuthService authService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        if (session.getAttribute("token") == null)
            return "redirect:/login";

        try {
            Pageable pageable = PageRequest.of(0, 10);
            Page<TaskResponse> taskPage = taskService.getAllTasks(pageable);

            model.addAttribute("taskList", taskPage.getContent());
            //TODO: replace name by SecurityContext
            model.addAttribute("userName", "User");
            return "dashboard";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute LoginRequest loginRequest,
                              HttpSession session, Model model) {
        try {
            AuthResponse response = authService.login(loginRequest);
            session.setAttribute("token", response.getToken());
            return "redirect:/";
            
        } catch (InvalidAuthenticationException | BadCredentialsException | UsernameNotFoundException e) {
            model.addAttribute("error", "Invalid email or password!");
            model.addAttribute("loginData", loginRequest);
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Something went wrong! Please try again later.");
            model.addAttribute("loginData", loginRequest);
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute RegisterRequest registerRequest, Model model) {
        try {
            authService.register(registerRequest);
            
            // Stay on page to show success message then redirect
            model.addAttribute("msg", "Registration successful! Redirecting to login in 3 seconds...");
            return "register";
            
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerData", registerRequest);
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Could not register account. Please try again.");
            model.addAttribute("registerData", registerRequest);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
