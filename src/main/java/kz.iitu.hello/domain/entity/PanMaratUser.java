package kz.iitu.hello.domain.entity;

import jakarta.persistence.*;
import kz.iitu.hello.domain.enums.PanMaratUserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PanMaratUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PanMaratUserRole role;

    @Column
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user")
    private PanMaratStudent student;

    @OneToOne(mappedBy = "user")
    private PanMaratTeacher teacher;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
