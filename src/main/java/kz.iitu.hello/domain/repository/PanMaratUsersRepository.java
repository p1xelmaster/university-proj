package kz.iitu.hello.domain.repository;

import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.domain.enums.PanMaratUserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PanMaratUsersRepository extends JpaRepository<PanMaratUser, Long> {

    Optional<PanMaratUser> findByUserName(String userName);

    boolean existsByUserNameIgnoreCaseAndIdNot(String userName, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByUserNameIgnoreCase(String userName);

    boolean existsByEmailIgnoreCase(String email);

    Page<PanMaratUser> findByUserNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
            String username,
            String email,
            Pageable pageable
    );

    Page<PanMaratUser> findByUserNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRole(
            String username,
            String email,
            PanMaratUserRole role,
            Pageable pageable
    );
}
