package com.researchworkbench.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({"/", "/login", "/app", "/app/**"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
