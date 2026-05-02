package ru.practice.mini_ats.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import ru.practice.mini_ats.models.enums.InterviewStatus;
import ru.practice.mini_ats.models.enums.InterviewType;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "interviews")
@SoftDelete(columnName = "active", strategy = SoftDeleteType.ACTIVE)
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_id")
    private Integer interviewId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private InterviewType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InterviewStatus status;

    @Column(name = "feedback")
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "resume_reaction_id", referencedColumnName = "resume_reaction_id")
    private ResumeReaction resumeReaction;
}
