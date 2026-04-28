package com.researchworkbench.controller;

import com.researchworkbench.dto.ApiResponse;
import com.researchworkbench.dto.profile.ProfileDtos;
import com.researchworkbench.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ApiResponse<ProfileDtos.ProfileResponse> getProfile() {
        return ApiResponse.ok(profileService.getProfile());
    }

    @PutMapping
    public ApiResponse<ProfileDtos.ProfileResponse> updateProfile(@Valid @RequestBody ProfileDtos.ProfileUpdateRequest request) {
        return ApiResponse.ok(profileService.updateProfile(request));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProfileDtos.ProfileResponse> uploadAvatar(@RequestPart("file") MultipartFile file) {
        return ApiResponse.ok(profileService.uploadAvatar(file));
    }

    @PutMapping("/links/{provider}")
    public ApiResponse<ProfileDtos.ProfileResponse> updateLink(
        @PathVariable String provider,
        @Valid @RequestBody ProfileDtos.LinkUpdateRequest request
    ) {
        return ApiResponse.ok(profileService.updateLink(provider, request));
    }
}
