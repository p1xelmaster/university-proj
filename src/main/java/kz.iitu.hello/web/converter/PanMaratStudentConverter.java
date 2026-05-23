package kz.iitu.hello.web.converter;

import kz.iitu.hello.domain.entity.PanMaratCourse;
import kz.iitu.hello.domain.entity.PanMaratStudent;
import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.web.dto.form.PanMaratStudentFormDto;
import kz.iitu.hello.web.dto.grid.PanMaratCourseGridDto;
import kz.iitu.hello.web.dto.grid.PanMaratUserGridDto;
import kz.iitu.hello.web.dto.view.PanMaratStudentViewDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PanMaratStudentConverter {

    public void applyFormToEntity(PanMaratStudentFormDto form, PanMaratStudent student, PanMaratUser user, java.util.Set<PanMaratCourse> courses) {
        student.setStudentName(form.getStudentName());
        student.setAge(form.getAge());
        student.setGpa(form.getGpa());
        student.setGroupName(form.getGroupName());
        student.setUser(user);
        student.setCourses(courses);
    }

    public PanMaratStudentFormDto toFormDto(PanMaratStudent student) {
        PanMaratStudentFormDto dto = new PanMaratStudentFormDto();
        dto.setId(student.getId());
        dto.setStudentName(student.getStudentName());
        dto.setAge(student.getAge());
        dto.setGpa(student.getGpa());
        dto.setGroupName(student.getGroupName());
        if (student.getUser() != null) {
            dto.setUserId(student.getUser().getId());
        }
        if (student.getCourses() != null) {
            dto.setCourseIds(student.getCourses().stream().map(PanMaratCourse::getId).toList());
        }
        return dto;
    }

    public PanMaratStudentViewDto toViewDto(PanMaratStudent student) {
        PanMaratStudentViewDto dto = new PanMaratStudentViewDto();
        dto.setId(student.getId());
        dto.setStudentName(student.getStudentName());
        dto.setAge(student.getAge());
        dto.setGpa(student.getGpa());
        dto.setGroupName(student.getGroupName());
        if (student.getUser() != null) {
            dto.setUser(toUserGridDto(student.getUser()));
        }
        List<PanMaratCourseGridDto> courseDtos = new ArrayList<>();
        if (student.getCourses() != null) {
            for (PanMaratCourse course : student.getCourses()) {
                courseDtos.add(toCourseGridDto(course));
            }
        }
        dto.setCourses(courseDtos);
        return dto;
    }

    public PanMaratUserGridDto toUserGridDto(PanMaratUser user) {
        if (user == null) {
            return null;
        }
        PanMaratUserGridDto dto = new PanMaratUserGridDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    public PanMaratCourseGridDto toCourseGridDto(PanMaratCourse course) {
        if (course == null) {
            return null;
        }
        PanMaratCourseGridDto dto = new PanMaratCourseGridDto();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(course.getCredits());
        dto.setMaxStudents(course.getMaxStudents());
        return dto;
    }
}
