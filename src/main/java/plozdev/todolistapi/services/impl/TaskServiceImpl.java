package plozdev.todolistapi.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import plozdev.todolistapi.dto.task.TaskRequest;
import plozdev.todolistapi.dto.task.TaskResponse;
import plozdev.todolistapi.entities.Task;
import plozdev.todolistapi.entities.User;
import plozdev.todolistapi.mapper.TaskMapper;
import plozdev.todolistapi.repository.TaskRepository;
import plozdev.todolistapi.repository.UserRepository;
import plozdev.todolistapi.services.TaskService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse createTask(TaskRequest request) {
        User currentUser = getCurrentUser();

        Task newTask = taskMapper.toEntity(request);
        newTask.setUser(currentUser);
        taskRepository.save(newTask);

        return taskMapper.toResponse(newTask);

    }

    @Override
    public TaskResponse updateTask(Integer id, TaskRequest request) {
        Task currentTask = taskRepository.findTaskById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task doesn't exist!"));

        User user = getCurrentUser();
        checkIsOwner(currentTask, user, "You are not authorized to edit this task!");

        taskMapper.updateTaskFromRequest(request, currentTask);

        taskRepository.save(currentTask);

        return taskMapper.toResponse(currentTask);
    }

    @Override
    public void deleteTask(Integer id) {
        Task currentTask = taskRepository.findTaskById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task doesn't exist!"));

        User user = getCurrentUser();
        checkIsOwner(currentTask, user,  "You are not authorized to delete this task!");

        taskRepository.delete(currentTask);
    }

    @Override
    public TaskResponse getTask(Integer id) {
        Task currentTask = taskRepository.findTaskById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task doesn't exist!"));

        User user = getCurrentUser();
        checkIsOwner(currentTask, user,"You are not authorized to view this task!");

        return taskMapper.toResponse(currentTask);
    }


    @Override
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<Task> taskPage = taskRepository.findByUserId(currentUser.getId(), pageable);

        return taskPage.map(taskMapper::toResponse);
    }

    private void checkIsOwner(Task currentTask, User currentUser, String errorMsg) {
        String taskOwnerEmail = currentTask.getUser().getEmail();
        if (!taskOwnerEmail.equals(currentUser.getEmail()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMsg);
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("Email doesn't exist!"));
    }

}
