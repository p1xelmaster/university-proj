package kz.iitu.hello.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PanMaratStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentName;

    @Min(16)
    @Max(100)
    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String groupName;

    @DecimalMin("0.0")
    @DecimalMax("4.0")
    @Column(nullable = false)
    private Double gpa;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private PanMaratUser user;

    @ManyToMany
    @JoinTable(name = "student_courses", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<PanMaratCourse> courses = new HashSet<>();
}
