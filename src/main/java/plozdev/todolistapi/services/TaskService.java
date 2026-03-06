package plozdev.todolistapi.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plozdev.todolistapi.dto.task.TaskRequest;
import plozdev.todolistapi.dto.task.TaskResponse;
import plozdev.todolistapi.entities.Task;

public interface TaskService {
    TaskResponse createTask(TaskRequest request);
    TaskResponse updateTask(Integer id, TaskRequest request);
    void deleteTask(Integer id);
    TaskResponse getTask(Integer id);
    Page<TaskResponse>getAllTasks(Pageable pageable);
}
