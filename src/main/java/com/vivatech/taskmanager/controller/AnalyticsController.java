package com.vivatech.taskmanager.controller;

import com.vivatech.taskmanager.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;


    // Get task count grouped by status - Admin only
    @GetMapping("/tasks-by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTasksByStatus() {
        return ResponseEntity.ok(analyticsService.getTasksByStatus());
    }

    // Get task count grouped by date - Admin only
    @GetMapping("/daily-task-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDailyTaskCount() {
        return ResponseEntity.ok(analyticsService.getDailyTaskCount());
    }
}
