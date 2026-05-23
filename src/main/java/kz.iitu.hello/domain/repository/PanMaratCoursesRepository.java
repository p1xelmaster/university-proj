package kz.iitu.hello.domain.repository;

import kz.iitu.hello.domain.entity.PanMaratCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PanMaratCoursesRepository extends JpaRepository<PanMaratCourse, Long>, JpaSpecificationExecutor<PanMaratCourse> {
}
