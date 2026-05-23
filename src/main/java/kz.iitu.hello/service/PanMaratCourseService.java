package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.PanMaratCourse;
import kz.iitu.hello.domain.entity.PanMaratStudent;
import kz.iitu.hello.domain.entity.PanMaratTeacher;
import kz.iitu.hello.domain.repository.CoursesRepository;
import kz.iitu.hello.domain.repository.StudentsRepository;
import kz.iitu.hello.domain.repository.TeachersRepository;
import kz.iitu.hello.domain.specification.CourseSpecification;
import kz.iitu.hello.exception.CourseLimitExceededException;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.PanMaratCourseConverter;
import kz.iitu.hello.web.dto.form.PanMaratCourseFormDto;
import kz.iitu.hello.web.dto.grid.PanMaratStudentGridDto;
import kz.iitu.hello.web.dto.grid.PanMaratTeacherGridDto;
import kz.iitu.hello.web.dto.search.PanMaratCourseSearchForm;
import kz.iitu.hello.web.dto.view.PanMaratCourseViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PanMaratCourseService {
    private static final String DEFAULT_SORT = "courseName";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("courseName", "maxStudents", "teacher");

    private final CoursesRepository coursesRepository;
    private final TeachersRepository teachersRepository;
    private final StudentsRepository studentsRepository;
    private final PanMaratCourseConverter courseConverter;

    @Transactional(readOnly = true)
    public Page<PanMaratCourseViewDto> search(PanMaratCourseSearchForm form, Pageable pageable) {
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.ASC, DEFAULT_SORT);

        if (form.getSortBy() != null && !form.getSortBy().isBlank()) {
            String requestedSortBy = form.getSortBy();
            if (!ALLOWED_SORT_FIELDS.contains(requestedSortBy)) {
                throw new IllegalArgumentException("Invalid sort field: " + requestedSortBy);
            }

            Sort.Direction direction = form.getSortDirection() == null ? Sort.Direction.ASC : form.getSortDirection();
            String sortProperty = "teacher".equals(requestedSortBy) ? "teacher.teacherName" : requestedSortBy;
            sort = Sort.by(direction, sortProperty);
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return coursesRepository.findAll(CourseSpecification.withFilters(form), sortedPageable)
                .map(courseConverter::toViewDto);
    }

    @Transactional(readOnly = true)
    public List<PanMaratTeacherGridDto> findAllTeachers() {
        return teachersRepository.findAll().stream().map(courseConverter::toTeacherGridDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PanMaratStudentGridDto> findAllStudents() {
        return studentsRepository.findAll().stream().map(courseConverter::toStudentGridDto).toList();
    }

    @Transactional(readOnly = true)
    public PanMaratCourseFormDto getForm(Long id) {
        return id == null ? new PanMaratCourseFormDto() : courseConverter.toFormDto(findById(id));
    }

    public void create(PanMaratCourseFormDto form) {
        PanMaratCourse course = new PanMaratCourse();
        applyForm(form, course);
        coursesRepository.save(course);
    }

    public void update(Long id, PanMaratCourseFormDto form) {
        PanMaratCourse course = findById(id);
        applyForm(form, course);
        coursesRepository.save(course);
    }

    public void delete(Long id) {
        PanMaratCourse course = findById(id);
        Set<PanMaratStudent> assignedStudents = new HashSet<>(course.getStudents());
        for (PanMaratStudent student : assignedStudents) {
            student.getCourses().remove(course);
        }
        course.getStudents().clear();
        coursesRepository.delete(course);
    }

    @Transactional(readOnly = true)
    public PanMaratCourse findById(Long id) {
        return coursesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("PanMaratCourse not found with id: " + id));
    }

    private void applyForm(PanMaratCourseFormDto form, PanMaratCourse course) {
        PanMaratTeacher teacher = teachersRepository.findById(form.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("PanMaratTeacher not found with id: " + form.getTeacherId()));

        List<Long> studentIds = form.getStudentIds() == null ? List.of() : form.getStudentIds();
        Set<PanMaratStudent> students = new HashSet<>(studentsRepository.findAllById(studentIds));

        if (students.size() != new HashSet<>(studentIds).size()) {
            throw new EntityNotFoundException("One or more students not found");
        }

        if (students.size() > form.getMaxStudents()) {
            throw new CourseLimitExceededException("PanMaratStudent count exceeds maximum allowed for this course");
        }

        Set<PanMaratStudent> oldStudents = new HashSet<>(course.getStudents());
        for (PanMaratStudent oldStudent : oldStudents) {
            oldStudent.getCourses().remove(course);
        }

        courseConverter.applyFormToEntity(form, course, teacher, new HashSet<>());

        for (PanMaratStudent student : students) {
            student.getCourses().add(course);
        }

        course.setStudents(students);
    }
}
