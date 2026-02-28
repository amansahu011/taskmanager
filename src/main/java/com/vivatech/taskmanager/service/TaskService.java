package com.vivatech.taskmanager.service;

import com.vivatech.taskmanager.dto.TaskRequest;
import com.vivatech.taskmanager.dto.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);
    List<TaskResponse> getTasks();
    TaskResponse approveTask(Long id);
    TaskResponse rejectTask(Long id);

}