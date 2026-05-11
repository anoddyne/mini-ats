package ru.practice.mini_ats.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;


@Entity
@Getter
@Setter
@Table(name = "resume")
@SoftDelete(columnName = "active", strategy = SoftDeleteType.ACTIVE)
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

    @Column(name = "skills")
    private String skills;

    @Column(name = "experience")
    private String experience;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
}
