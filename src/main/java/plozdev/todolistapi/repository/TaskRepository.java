package plozdev.todolistapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import plozdev.todolistapi.entities.Task;

import java.util.Optional;

public interface TaskRepository extends JpaRepository <Task, Integer>, JpaSpecificationExecutor<Task>  {
    Page<Task> findByUserId(Integer userId, Pageable pageable);
    Page<Task> findByUserIdAndIsCompleted(Integer userId, boolean isCompleted, Pageable pageable);
    Optional<Task> findTaskById(Integer id);
}
