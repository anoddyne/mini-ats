package ru.practice.mini_ats.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "interviews")
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_id")
    private Integer interviewId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "feedback")
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "resume_reaction_id", referencedColumnName = "resume_reaction_id")
    private ResumeReaction resumeReaction;
}
