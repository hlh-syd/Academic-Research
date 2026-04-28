package com.researchworkbench.dto.profile;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class ProfileDtos {

    private ProfileDtos() {
    }

    public record ProfileResponse(
        String email,
        String displayName,
        String title,
        String institution,
        String bio,
        String avatarUrl,
        List<LinkResponse> links
    ) {
    }

    public record LinkResponse(String name, boolean linked, String account) {
    }

    public record ProfileUpdateRequest(
        @NotBlank(message = "姓名不能为空") String displayName,
        String title,
        String institution,
        String bio
    ) {
    }

    public record LinkUpdateRequest(boolean linked, String account) {
    }
}
