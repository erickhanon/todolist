package br.com.erickhanon.todolist.task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.erickhanon.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/create")
  public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    taskModel.setUserId((java.util.UUID) request.getAttribute("userId"));

    var currentDate = java.time.LocalDateTime.now();
    if (currentDate.isAfter(taskModel.getStartDate()) || currentDate.isAfter(taskModel.getEndDate())) {
      return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
          .body("Data de início / Data de término não pode ser menor que a data atual");
    } else if (taskModel.getStartDate().isAfter(taskModel.getEndDate())) {
      return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
          .body("Data de início não pode ser maior que a data de término");
    }
    var task = this.taskRepository.save(taskModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(task);
  }

  @GetMapping("/user")
  public ResponseEntity<List<TaskModel>> getAllTasksByUser(HttpServletRequest request) {
    UUID userId = (UUID) request.getAttribute("userId");
    List<TaskModel> tasks = taskRepository.findByUserId(userId);
    return ResponseEntity.ok(tasks);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskModel> update(@PathVariable UUID id, @RequestBody TaskModel taskModel, HttpServletRequest request) {
    UUID userId = (UUID) request.getAttribute("userId");
    Optional<TaskModel> taskOptional = taskRepository.findById(id);
    if (taskOptional.isPresent()) {
      TaskModel task = taskOptional.get();
      Utils.CopyNonNullProperties(taskModel, task);
      if (task.getUserId().equals(userId)) {
        taskModel.setUserId(userId);
        var currentDate = java.time.LocalDateTime.now();
        if (currentDate.isAfter(task.getStartDate()) || currentDate.isAfter(task.getEndDate()) || task.getStartDate().isAfter(task.getEndDate())) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(task);
        }
        var taskUpdated = taskRepository.save(task);
        return ResponseEntity.ok(taskUpdated);
      }
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(task);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(taskModel);
  }

}
