package br.com.erickhanon.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String description;
    @Column(length = 100)
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String priority;
    private String status;

    private UUID userId;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
