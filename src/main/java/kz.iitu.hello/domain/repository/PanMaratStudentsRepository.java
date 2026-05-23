package kz.iitu.hello.domain.repository;

import kz.iitu.hello.domain.entity.PanMaratStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PanMaratStudentsRepository extends JpaRepository<PanMaratStudent, Long> {
    Optional<PanMaratStudent> findByUserId(Long userId);
}
