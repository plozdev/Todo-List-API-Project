package plozdev.todolistapi.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import plozdev.todolistapi.entities.TaskPriority;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "The title must not be empty")
    private String title;

    @Size(max = 255)
    private String description;

    @NotNull
    private Boolean isCompleted;

    @NotNull
    private TaskPriority priority;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dueDate;

}
