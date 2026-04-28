package com.researchworkbench.controller;

import com.researchworkbench.dto.ApiResponse;
import com.researchworkbench.service.RelaxService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relax")
public class RelaxController {

    private final RelaxService relaxService;

    public RelaxController(RelaxService relaxService) {
        this.relaxService = relaxService;
    }

    @GetMapping("/fortune")
    public ApiResponse<Map<String, String>> fortune() {
        return ApiResponse.ok(relaxService.fortune());
    }

    @GetMapping("/meal")
    public ApiResponse<Map<String, String>> meal() {
        return ApiResponse.ok(relaxService.meal());
    }
}
