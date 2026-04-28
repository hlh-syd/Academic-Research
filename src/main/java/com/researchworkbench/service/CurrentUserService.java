package com.researchworkbench.service;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.exception.NotFoundException;
import com.researchworkbench.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final AppUserRepository appUserRepository;

    public CurrentUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new NotFoundException("Current user not found");
        }

        return appUserRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Current user not found"));
    }
}
