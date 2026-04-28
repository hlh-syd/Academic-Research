package com.researchworkbench.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
        @Email(message = "请输入合法邮箱")
        @NotBlank(message = "邮箱不能为空")
        String email,
        @NotBlank(message = "密码不能为空")
        String password
    ) {
    }

    public record AuthResponse(String token, String email, String displayName, String role) {
    }
}
