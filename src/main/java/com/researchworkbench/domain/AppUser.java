package com.researchworkbench.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_users")
public class AppUser extends BaseEntity {

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 80)
    private String displayName;

    @Column(length = 120)
    private String title;

    @Column(length = 160)
    private String institution;

    @Column(length = 1200)
    private String bio;

    @Column(length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    private boolean wechatLinked;

    @Column(length = 120)
    private String wechatAccount;

    @Column(nullable = false)
    private boolean feishuLinked;

    @Column(length = 120)
    private String feishuAccount;
}
