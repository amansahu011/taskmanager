package com.vivatech.taskmanager.service.impl;

import com.vivatech.taskmanager.repository.TaskRepository;
import com.vivatech.taskmanager.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TaskRepository taskRepository;


    @Override
    public Map<String, Object> getTasksByStatus() {

        // Get task count grouped by status
        List<Object[]> results = taskRepository.countTasksByStatus();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        // Map status to count
        Map<String, Long> statusCount = new HashMap<>();
        for (Object[] result : results) {
            String status = result[0].toString();
            Long count = (Long) result[1];
            statusCount.put(status, count);
        }

        // Add 0 for missing statuses
        for (String status : List.of("CREATED", "APPROVED", "REJECTED")) {
            statusCount.putIfAbsent(status, 0L);
        }

        response.put("data", statusCount);
        response.put("total", statusCount.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum());

        return response;
    }


    @Override
    public Map<String, Object> getDailyTaskCount() {

        // Get task count grouped by date
        List<Object[]> results = taskRepository.countTasksByDate();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        // Map date to count
        List<Map<String, Object>> dailyCount = results.stream()
                .map(result -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("date", result[0].toString());
                    entry.put("count", result[1]);
                    return entry;
                })
                .toList();

        response.put("data", dailyCount);
        response.put("total_days", dailyCount.size());
        response.put("total_tasks", dailyCount.stream()
                .mapToLong(entry -> (Long) entry.get("count"))
                .sum());

        return response;
    }
}
