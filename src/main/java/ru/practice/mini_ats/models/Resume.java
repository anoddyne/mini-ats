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
public class Resume {
    @Id
    @Column(name = "resume_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resumeId;

    @Column(name = "resume_file_url")
    private String resumeFileUrl;

    @Column(name = "file_name")
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
}
