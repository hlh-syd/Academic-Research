package com.researchworkbench.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "research_papers")
public class ResearchPaper extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false, length = 280)
    private String title;

    @Column(nullable = false, length = 300)
    private String authors;

    @Column(nullable = false, length = 40)
    private String publicationYear;

    @Column(nullable = false, length = 180)
    private String journal;

    @Column(nullable = false, length = 1500)
    private String summary;

    @Column(nullable = false, length = 500)
    private String tags;

    @Column(nullable = false)
    private Integer citedBy;

    @Column(nullable = false)
    private boolean inKnowledgeBase;
}
