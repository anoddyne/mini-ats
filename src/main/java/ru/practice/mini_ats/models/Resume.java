package ru.practice.mini_ats.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "resume")
public class Resume {
    @Id
    @Column(name = "resume_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resumeId;

    @Column(name = "summary")
    private String summary;

    @Column(name = "education")
    private String education;

    @Column(name = "desired_salary")
    private Integer desiredSalary;

    @Column(name = "resume_file_url")
    private String resumeFileUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skills", columnDefinition = "jsonb")
    private Map<String, Object> skills;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experience", columnDefinition = "jsonb")
    private Map<String, Object> experience;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
}
