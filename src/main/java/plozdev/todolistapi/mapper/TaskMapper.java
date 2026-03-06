package plozdev.todolistapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import plozdev.todolistapi.dto.task.TaskRequest;
import plozdev.todolistapi.dto.task.TaskResponse;
import plozdev.todolistapi.entities.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(TaskRequest request);

    @Mapping(source = "user.name", target = "name")
    TaskResponse toResponse(Task task);

    void updateTaskFromRequest(TaskRequest request, @MappingTarget Task task);
}
