package com.researchworkbench.service;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.dto.profile.ProfileDtos;
import com.researchworkbench.exception.BadRequestException;
import com.researchworkbench.repository.AppUserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileService {

    private final CurrentUserService currentUserService;
    private final AppUserRepository appUserRepository;
    private final FileStorageService fileStorageService;

    public ProfileService(
        CurrentUserService currentUserService,
        AppUserRepository appUserRepository,
        FileStorageService fileStorageService
    ) {
        this.currentUserService = currentUserService;
        this.appUserRepository = appUserRepository;
        this.fileStorageService = fileStorageService;
    }

    public ProfileDtos.ProfileResponse getProfile() {
        return toResponse(currentUserService.getCurrentUser());
    }

    public ProfileDtos.ProfileResponse updateProfile(ProfileDtos.ProfileUpdateRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        user.setDisplayName(request.displayName());
        user.setTitle(request.title());
        user.setInstitution(request.institution());
        user.setBio(request.bio());
        return toResponse(appUserRepository.save(user));
    }

    public ProfileDtos.ProfileResponse uploadAvatar(MultipartFile file) {
        AppUser user = currentUserService.getCurrentUser();
        user.setAvatarUrl(fileStorageService.store(file, "avatars"));
        return toResponse(appUserRepository.save(user));
    }

    public ProfileDtos.ProfileResponse updateLink(String provider, ProfileDtos.LinkUpdateRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        String normalized = provider.trim().toLowerCase();
        if ("wechat".equals(normalized)) {
            user.setWechatLinked(request.linked());
            user.setWechatAccount(request.account());
        } else if ("feishu".equals(normalized)) {
            user.setFeishuLinked(request.linked());
            user.setFeishuAccount(request.account());
        } else {
            throw new BadRequestException("Unsupported link provider: " + provider);
        }

        return toResponse(appUserRepository.save(user));
    }

    private ProfileDtos.ProfileResponse toResponse(AppUser user) {
        return new ProfileDtos.ProfileResponse(
            user.getEmail(),
            user.getDisplayName(),
            user.getTitle(),
            user.getInstitution(),
            user.getBio(),
            user.getAvatarUrl(),
            List.of(
                new ProfileDtos.LinkResponse("微信", user.isWechatLinked(), defaultText(user.getWechatAccount())),
                new ProfileDtos.LinkResponse("飞书", user.isFeishuLinked(), defaultText(user.getFeishuAccount()))
            )
        );
    }

    private String defaultText(String value) {
        return value == null || value.isBlank() ? "未关联" : value;
    }
}
