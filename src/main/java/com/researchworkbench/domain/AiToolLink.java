package com.researchworkbench.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ai_tool_links")
public class AiToolLink extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 800)
    private String description;

    @Column(nullable = false, length = 400)
    private String url;

    @Column(nullable = false, length = 60)
    private String tag;

    @Column(nullable = false)
    private Integer sortOrder;
}
