package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.PanMaratCourse;
import kz.iitu.hello.domain.entity.PanMaratStudent;
import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.domain.mapper.PanMaratStudentsMyBatisMapper;
import kz.iitu.hello.domain.repository.PanMaratCoursesRepository;
import kz.iitu.hello.domain.repository.PanMaratStudentsRepository;
import kz.iitu.hello.domain.repository.PanMaratUsersRepository;
import kz.iitu.hello.exception.PanMaratEntityNotFoundException;
import kz.iitu.hello.web.converter.PanMaratStudentConverter;
import kz.iitu.hello.web.dto.form.PanMaratStudentFormDto;
import kz.iitu.hello.web.dto.grid.PanMaratCourseGridDto;
import kz.iitu.hello.web.dto.grid.PanMaratUserGridDto;
import kz.iitu.hello.web.dto.search.PanMaratStudentSearchForm;
import kz.iitu.hello.web.dto.view.PanMaratStudentViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PanMaratStudentService {
    private static final String DEFAULT_SORT = "studentName";
    private static final Map<String, String> SORT_COLUMN_MAPPING = Map.of(
            "id", "id",
            "studentName", "student_name",
            "age", "age",
            "groupName", "group_name",
            "gpa", "gpa"
    );

    private final PanMaratStudentsRepository studentRepository;
    private final PanMaratUsersRepository userRepository;
    private final PanMaratCoursesRepository courseRepository;
    private final PanMaratStudentsMyBatisMapper studentsMyBatisMapper;
    private final PanMaratStudentConverter studentConverter;

    @Transactional(readOnly = true)
    public Page<PanMaratStudentViewDto> search(PanMaratStudentSearchForm form, Pageable pageable) {
        String requestedSortBy = form.getSortBy() == null ? DEFAULT_SORT : form.getSortBy();
        String sortBy = SORT_COLUMN_MAPPING.getOrDefault(requestedSortBy, SORT_COLUMN_MAPPING.get(DEFAULT_SORT));
        Sort.Direction direction = form.getSortDirection() == null ? Sort.Direction.ASC : form.getSortDirection();

        List<PanMaratStudent> students = studentsMyBatisMapper.findStudents(
                form.getName(),
                form.getGroupName(),
                form.getAgeFrom(),
                form.getAgeTo(),
                form.getGpaFrom(),
                form.getGpaTo(),
                sortBy,
                direction.name(),
                pageable.getPageSize(),
                pageable.getOffset()
        );

        long total = studentsMyBatisMapper.countStudents(
                form.getName(),
                form.getGroupName(),
                form.getAgeFrom(),
                form.getAgeTo(),
                form.getGpaFrom(),
                form.getGpaTo()
        );

        return new PageImpl<>(students, pageable, total)
                .map(studentConverter::toViewDto);
    }

    @Transactional(readOnly = true)
    public List<PanMaratUserGridDto> findAllUsers() {
        return userRepository.findAll().stream().map(studentConverter::toUserGridDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PanMaratCourseGridDto> findAllCourses() {
        return courseRepository.findAll().stream().map(studentConverter::toCourseGridDto).toList();
    }

    @Transactional(readOnly = true)
    public PanMaratStudentFormDto getForm(Long id) {
        return id == null ? new PanMaratStudentFormDto() : studentConverter.toFormDto(findById(id));
    }

    public void create(PanMaratStudentFormDto form) {
        PanMaratStudent student = new PanMaratStudent();
        applyForm(form, student);
        studentRepository.save(student);
    }

    public void update(Long id, PanMaratStudentFormDto form) {
        PanMaratStudent student = findById(id);
        applyForm(form, student);
        studentRepository.save(student);
    }

    public void delete(Long id) {
        PanMaratStudent student = findById(id);
        if (student.getCourses() != null) {
            for (PanMaratCourse course : student.getCourses()) {
                if (course.getStudents() != null) {
                    course.getStudents().remove(student);
                }
            }
            student.getCourses().clear();
        }
        studentRepository.delete(student);
    }

    @Transactional(readOnly = true)
    public PanMaratStudent findById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new PanMaratEntityNotFoundException("PanMaratStudent not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public PanMaratStudent findByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new PanMaratEntityNotFoundException("PanMaratStudent not found for user id: " + userId));
    }

    private void applyForm(PanMaratStudentFormDto form, PanMaratStudent student) {
        PanMaratUser user = userRepository.findById(form.getUserId())
                .orElseThrow(() -> new PanMaratEntityNotFoundException("PanMaratUser not found with id: " + form.getUserId()));
        List<Long> courseIds = form.getCourseIds();
        Set<PanMaratCourse> courses = courseIds == null ? new HashSet<>() : new HashSet<>(courseRepository.findAllById(courseIds));
        studentConverter.applyFormToEntity(form, student, user, courses);
    }
}
