package kz.iitu.hello.domain.repository;

import kz.iitu.hello.domain.entity.PanMaratUserFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PanMaratUserFileRepository extends JpaRepository<PanMaratUserFile, Long> {
    List<PanMaratUserFile> findByUserId(Long userId);

    List<PanMaratUserFile> findByUserIdAndFileType(Long userId, String fileType);
}
