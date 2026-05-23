package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.domain.enums.PanMaratUserRole;
import kz.iitu.hello.domain.repository.PanMaratUsersRepository;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.PanMaratUserConverter;
import kz.iitu.hello.web.dto.form.PanMaratUserFormDto;
import kz.iitu.hello.web.dto.grid.PanMaratUserGridDto;
import kz.iitu.hello.web.dto.search.PanMaratUserSearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PanMaratUserService {
    private static final String DEFAULT_SORT = "createdAt";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "userName", "email", "createdAt", "role");

    private final PanMaratUsersRepository usersRepository;
    private final PanMaratUserConverter userConverter;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<PanMaratUserGridDto> search(PanMaratUserSearchForm form, Pageable pageable) {
        String userNameFilter = form.getUsername() == null ? "" : form.getUsername();
        String emailFilter = form.getEmail() == null ? "" : form.getEmail();
        PanMaratUserRole roleFilter = form.getRole();

        Sort.Direction direction = form.getSortDirection() == null ? Sort.Direction.ASC : form.getSortDirection();
        String requestedSortBy = form.getSortBy() == null ? DEFAULT_SORT : form.getSortBy();
        String sortBy = ALLOWED_SORT_FIELDS.contains(requestedSortBy) ? requestedSortBy : DEFAULT_SORT;

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortBy));

        if(roleFilter == null)
            return usersRepository
                    .findByUserNameContainingIgnoreCaseAndEmailContainingIgnoreCase(userNameFilter, emailFilter, sortedPageable)
                    .map(userConverter::toGridDto);

        return usersRepository
                .findByUserNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRole(userNameFilter, emailFilter, roleFilter, sortedPageable)
                .map(userConverter::toGridDto);
    }

    @Transactional(readOnly = true)
    public PanMaratUserFormDto getForm(Long id) {
        return id == null ? new PanMaratUserFormDto() : userConverter.toFormDto(findById(id));
    }

    public void create(PanMaratUserFormDto form) {
        PanMaratUser user = new PanMaratUser();
        userConverter.applyFormToEntity(form, user);
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        usersRepository.save(user);
    }

    public void update(Long id, PanMaratUserFormDto form) {
        PanMaratUser user = findById(id);
        userConverter.applyFormToEntity(form, user);
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        usersRepository.save(user);
    }

    public void delete(Long id) {
        usersRepository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public PanMaratUser findById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PanMaratUser not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public PanMaratUser findByUsername(String username) {
        return usersRepository.findByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException("PanMaratUser not found: " + username));
    }
}
