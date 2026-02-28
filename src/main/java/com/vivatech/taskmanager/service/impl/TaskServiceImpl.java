package com.vivatech.taskmanager.service.impl;

import com.vivatech.taskmanager.dto.TaskRequest;
import com.vivatech.taskmanager.dto.TaskResponse;
import com.vivatech.taskmanager.entity.Task;
import com.vivatech.taskmanager.entity.User;
import com.vivatech.taskmanager.enums.TaskStatus;
import com.vivatech.taskmanager.exception.AppException;
import com.vivatech.taskmanager.exception.ErrorCode;
import com.vivatech.taskmanager.repository.TaskRepository;
import com.vivatech.taskmanager.repository.UserRepository;
import com.vivatech.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


   //logic for create the task only user can create the task
    @Override
    public TaskResponse createTask(TaskRequest request) {

        // Get logged in user from SecurityContext
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        // Generate task number TASK-0001, TASK-0002...
        String taskNumber = generateTaskNumber();

        // Build and save task
        Task task = Task.builder()
                .taskNumber(taskNumber)
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(user)
                .build();

        taskRepository.save(task);

        return mapToResponse(task);
    }


    // Generate taskNumber like TASK-0001
    private String generateTaskNumber() {
        return taskRepository.findLastTask()
                .map(lastTask -> {
                    // Extract number from TASK-0001 → 1
                    String lastNumber = lastTask.getTaskNumber()
                            .replace("TASK-", "");
                    int nextNumber = Integer.parseInt(lastNumber) + 1;
                    return String.format("TASK-%04d", nextNumber);
                })
                .orElse("TASK-0001"); // First task
    }

    // Task entity → TaskResponse
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .taskNumber(task.getTaskNumber())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .createdBy(task.getCreatedBy().getUsername())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }


    //logic for get the task for user he gets only their task and for admin he gets all the task
    @Override
    public List<TaskResponse> getTasks() {

        // Get logged in user from SecurityContext
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        // Check role
        String role = user.getRole().name();

        if (role.equals("ADMIN")) {
            // ADMIN → all tasks
            return taskRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        } else {
            // USER → only own tasks
            return taskRepository.findByCreatedBy(user)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }
    }


    // logic for admin approve the task
    @Override
    public TaskResponse approveTask(Long id) {

        // Find task by id
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        // Check if task is already approved
        if (task.getStatus() == TaskStatus.APPROVED) {
            throw new AppException(ErrorCode.TASK_ALREADY_APPROVED);
        }

        // Check if task is already rejected
        // Rejected task cannot be approved
        if (task.getStatus() == TaskStatus.REJECTED) {
            throw new AppException(ErrorCode.TASK_ALREADY_REJECTED);
        }

        // Update status to APPROVED
        task.setStatus(TaskStatus.APPROVED);
        taskRepository.save(task);

        return mapToResponse(task);
    }


    //logic for rejecting the task by admin
    @Override
    public TaskResponse rejectTask(Long id) {

        // Find task by id
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        // Check if task is already rejected
        if (task.getStatus() == TaskStatus.REJECTED) {
            throw new AppException(ErrorCode.TASK_ALREADY_REJECTED);
        }

        // Check if task is already approved
        // Approved task cannot be rejected
        if (task.getStatus() == TaskStatus.APPROVED) {
            throw new AppException(ErrorCode.TASK_ALREADY_APPROVED);
        }

        // Update status to REJECTED
        task.setStatus(TaskStatus.REJECTED);
        taskRepository.save(task);

        return mapToResponse(task);
    }

}
