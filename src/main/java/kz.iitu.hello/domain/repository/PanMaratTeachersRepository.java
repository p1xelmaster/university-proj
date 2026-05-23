package kz.iitu.hello.domain.repository;

import kz.iitu.hello.domain.entity.PanMaratTeacher;
import kz.iitu.hello.domain.enums.PanMaratDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface PanMaratTeachersRepository extends JpaRepository<PanMaratTeacher, Long> {

    @Query("""
            SELECT t FROM PanMaratTeacher t
            WHERE (:department IS NULL OR t.department = :department)
              AND (:name IS NULL OR LOWER(t.teacherName) LIKE CONCAT('%', LOWER(CAST(:name as string)), '%'))
            """)
    Page<PanMaratTeacher> searchTeachers(@Param("department") PanMaratDepartment department,
                                         @Param("name") String name,
                                         Pageable pageable);

    Optional<PanMaratTeacher> findByUserId(Long userId);
}
