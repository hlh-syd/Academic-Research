package com.researchworkbench.controller;

import com.researchworkbench.dto.ApiResponse;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PublicController {

    @GetMapping
    public ApiResponse<Map<String, Object>> root() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", "research-workbench");
        payload.put("status", "running");
        payload.put("time", LocalDateTime.now().toString());
        payload.put("publicEndpoints", List.of(
            "POST /api/auth/login",
            "POST /api/auth/logout",
            "GET /api/health"
        ));
        payload.put("message", "Backend is up. Use JWT to access protected /api/** endpoints.");
        return ApiResponse.ok(payload);
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.ok(Map.of(
            "status", "UP",
            "service", "research-workbench"
        ));
    }
}
