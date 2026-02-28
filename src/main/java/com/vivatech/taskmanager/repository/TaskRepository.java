package com.vivatech.taskmanager.repository;

import com.vivatech.taskmanager.entity.Task;
import com.vivatech.taskmanager.entity.User;
import com.vivatech.taskmanager.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // USER own tasks
    List<Task> findByCreatedBy(User user);


    @Query("SELECT t FROM Task t ORDER BY t.id DESC LIMIT 1")
    Optional<Task> findLastTask();

    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countTasksByStatus();

    @Query("SELECT DATE(t.createdAt), COUNT(t) FROM Task t GROUP BY DATE(t.createdAt) ORDER BY DATE(t.createdAt) DESC")
    List<Object[]> countTasksByDate();
}