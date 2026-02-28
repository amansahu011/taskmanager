package com.vivatech.taskmanager.service;

import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> getTasksByStatus();
    Map<String, Object> getDailyTaskCount();
}