package kz.iitu.hello.web.converter;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Student;
import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.web.dto.form.PanMaratCourseFormDto;
import kz.iitu.hello.web.dto.grid.PanMaratStudentGridDto;
import kz.iitu.hello.web.dto.grid.PanMaratTeacherGridDto;
import kz.iitu.hello.web.dto.view.PanMaratCourseViewDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class PanMaratCourseConverter {

    public void applyFormToEntity(PanMaratCourseFormDto form, Course course, Teacher teacher, Set<Student> students) {
        course.setCourseName(form.getCourseName());
        course.setCredits(form.getCredits());
        course.setMaxStudents(form.getMaxStudents());
        course.setTeacher(teacher);
        course.setStudents(students);
    }

    public PanMaratCourseFormDto toFormDto(Course course) {
        PanMaratCourseFormDto dto = new PanMaratCourseFormDto();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(course.getCredits());
        dto.setMaxStudents(course.getMaxStudents());
        if (course.getTeacher() != null) {
            dto.setTeacherId(course.getTeacher().getId());
        }
        if (course.getStudents() != null) {
            dto.setStudentIds(course.getStudents().stream().map(Student::getId).toList());
        }
        return dto;
    }

    public PanMaratCourseViewDto toViewDto(Course course) {
        PanMaratCourseViewDto dto = new PanMaratCourseViewDto();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(course.getCredits());
        dto.setMaxStudents(course.getMaxStudents());
        if (course.getTeacher() != null) {
            dto.setTeacher(toTeacherGridDto(course.getTeacher()));
        }
        List<PanMaratStudentGridDto> students = new ArrayList<>();
        if (course.getStudents() != null) {
            for (Student student : course.getStudents()) {
                students.add(toStudentGridDto(student));
            }
        }
        dto.setStudents(students);
        return dto;
    }

    public PanMaratTeacherGridDto toTeacherGridDto(Teacher teacher) {
        if (teacher == null) {
            return null;
        }
        PanMaratTeacherGridDto dto = new PanMaratTeacherGridDto();
        dto.setId(teacher.getId());
        dto.setTeacherName(teacher.getTeacherName());
        dto.setDepartment(teacher.getDepartment());
        dto.setExperienceYears(teacher.getExperienceYears());
        return dto;
    }

    public PanMaratStudentGridDto toStudentGridDto(Student student) {
        if (student == null) {
            return null;
        }
        PanMaratStudentGridDto dto = new PanMaratStudentGridDto();
        dto.setId(student.getId());
        dto.setStudentName(student.getStudentName());
        dto.setAge(student.getAge());
        dto.setGroupName(student.getGroupName());
        dto.setGpa(student.getGpa());
        return dto;
    }
}
