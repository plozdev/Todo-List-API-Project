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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import plozdev.todolistapi.dto.auth.AuthResponse;
import plozdev.todolistapi.dto.auth.LoginRequest;
import plozdev.todolistapi.dto.auth.RegisterRequest;
import plozdev.todolistapi.dto.task.TaskRequest;
import plozdev.todolistapi.dto.task.TaskResponse;
import plozdev.todolistapi.entities.User;
import plozdev.todolistapi.exception.InvalidAuthenticationException;
import plozdev.todolistapi.exception.UserAlreadyExistsException;
import plozdev.todolistapi.services.AuthService;
import plozdev.todolistapi.services.TaskService;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final TaskService taskService;
    private final AuthService authService;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "priority") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model, HttpSession session) {
        if (session.getAttribute("token") == null)
            return "redirect:/login";

        try {
            org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                    org.springframework.data.domain.Sort.by(sortField).ascending() : 
                    org.springframework.data.domain.Sort.by(sortField).descending();
            Pageable pageable = PageRequest.of(page, 10, sort);
            Page<TaskResponse> taskPage = (keyword == null) ?
                    taskService.getAllTasks(pageable) :
                    taskService.searchTasks(keyword,pageable);

            if (keyword != null) {
                model.addAttribute("keyword", keyword);
            }

            model.addAttribute("taskList", taskPage.getContent());

            List<TaskResponse> tasksForStats = taskPage.getContent();
            long completedCount = tasksForStats.stream().filter(t -> Boolean.TRUE.equals(t.getIsCompleted())).count();
            long totalCount = tasksForStats.size();
            long pendingCount = totalCount - completedCount;
            
            model.addAttribute("completedCount", completedCount);
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("totalCount", totalCount);

            // Pagination info
            model.addAttribute("currentPage", taskPage.getNumber() + 1);
            model.addAttribute("totalPages", Math.max(1, taskPage.getTotalPages()));
            model.addAttribute("hasPrev", taskPage.hasPrevious());
            model.addAttribute("hasNext", taskPage.hasNext());

            // Sorting info
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

            Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                model.addAttribute("userName", user.getName());
                model.addAttribute("userEmail", user.getEmail());
            } else {
                model.addAttribute("userName", principal.toString());
                model.addAttribute("userEmail", "");
            }
            
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

    @PostMapping("/tasks/add")
    public String addTask(@ModelAttribute TaskRequest request,
                          HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("token") == null)
            return "redirect:/login";

        try {
            taskService.createTask(request);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error adding task! Please ensure the Due Date is in the future.");
        }

        return "redirect:/";
    }

    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable Integer id, HttpSession session) {
        taskService.deleteTask(id);
        return "redirect:/";
    }

    @GetMapping("/tasks/toggle/{id}")
    public String toggleTask(@PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("token") == null)
            return "redirect:/login";
        
        try {
            TaskResponse task = taskService.getTask(id);
            if (task != null) {
                TaskRequest request = new TaskRequest(
                        task.getTitle(),
                        task.getDescription(),
                        !task.getIsCompleted(), // Toggle here
                        task.getPriority(),
                        task.getDueDate()
                );
                taskService.updateTask(id, request);
            }
        } catch (Exception ignored) { }
        
        return "redirect:/";
    }
}
