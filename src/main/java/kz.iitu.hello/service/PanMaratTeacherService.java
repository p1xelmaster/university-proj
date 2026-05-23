package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.PanMaratCourse;
import kz.iitu.hello.domain.entity.PanMaratTeacher;
import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.domain.repository.PanMaratCoursesRepository;
import kz.iitu.hello.domain.repository.TeachersRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.exception.BusinessException;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.PanMaratTeacherConverter;
import kz.iitu.hello.web.dto.form.PanMaratTeacherFormDto;
import kz.iitu.hello.web.dto.grid.PanMaratCourseGridDto;
import kz.iitu.hello.web.dto.grid.PanMaratUserGridDto;
import kz.iitu.hello.web.dto.search.PanMaratTeacherSearchForm;
import kz.iitu.hello.web.dto.view.PanMaratTeacherViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PanMaratTeacherService {
    private static final String DEFAULT_SORT = "teacherName";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "teacherName", "experienceYears", "department");

    private final TeachersRepository teachersRepository;
    private final UsersRepository usersRepository;
    private final PanMaratCoursesRepository coursesRepository;
    private final PanMaratTeacherConverter teacherConverter;

    @Transactional(readOnly = true)
    public Page<PanMaratTeacherViewDto> search(PanMaratTeacherSearchForm form, Pageable pageable) {
        Sort.Direction direction = form.getSortDirection() == null ? Sort.Direction.ASC : form.getSortDirection();
        String requestedSortBy = form.getSortBy() == null ? DEFAULT_SORT : form.getSortBy();
        String sortBy = ALLOWED_SORT_FIELDS.contains(requestedSortBy) ? requestedSortBy : DEFAULT_SORT;

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortBy));

        return teachersRepository.searchTeachers(form.getDepartment(), form.getName(), sortedPageable)
                .map(teacherConverter::toViewDto);
    }

    @Transactional(readOnly = true)
    public List<PanMaratUserGridDto> findAllUsers() {
        return usersRepository.findAll().stream().map(teacherConverter::toUserGridDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PanMaratCourseGridDto> findAllCourses() {
        return coursesRepository.findAll().stream().map(teacherConverter::toCourseGridDto).toList();
    }

    @Transactional(readOnly = true)
    public PanMaratTeacherFormDto getForm(Long id) {
        return id == null ? new PanMaratTeacherFormDto() : teacherConverter.toFormDto(findById(id));
    }

    public void create(PanMaratTeacherFormDto form) {
        PanMaratTeacher teacher = new PanMaratTeacher();
        applyForm(form, teacher);
        teachersRepository.save(teacher);
    }

    public void update(Long id, PanMaratTeacherFormDto form) {
        PanMaratTeacher teacher = findById(id);
        applyForm(form, teacher);
        teachersRepository.save(teacher);
    }

    public void delete(Long id) {
        PanMaratTeacher teacher = findById(id);
        if (teacher.getCourses() != null && !teacher.getCourses().isEmpty()) {
            throw new BusinessException("Cannot delete teacher with assigned courses");
        }
        teachersRepository.delete(teacher);
    }

    @Transactional(readOnly = true)
    public PanMaratTeacher findById(Long id) {
        return teachersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("PanMaratTeacher not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public PanMaratTeacher findByUserId(Long userId) {
        return teachersRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("PanMaratTeacher not found for user id: " + userId));
    }

    private void applyForm(PanMaratTeacherFormDto form, PanMaratTeacher teacher) {
        PanMaratUser user = usersRepository.findById(form.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("PanMaratUser not found with id: " + form.getUserId()));
        List<Long> courseIds = form.getCourseIds();
        List<PanMaratCourse> courses = courseIds == null ? new ArrayList<>() : new ArrayList<>(coursesRepository.findAllById(courseIds));
        teacherConverter.applyFormToEntity(form, teacher, user, courses);
    }
}
