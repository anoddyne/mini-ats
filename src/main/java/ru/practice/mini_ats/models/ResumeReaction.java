package ru.practice.mini_ats.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="resume_reactions")
public class ResumeReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_reaction_id")
    private Integer resumeReactionId;

    @Column(name = "cover_letter")
    private String coverLetter;

    @Column(name = "applied_at")
    private LocalDate appliedAt;

    @ManyToOne
    @JoinColumn(name = "vacancy_id", referencedColumnName = "vacancy_id")
    private Vacancy vacancy;

    @ManyToOne
    @JoinColumn(name="resume_id", referencedColumnName = "resume_id")
    private Resume resume;
}
