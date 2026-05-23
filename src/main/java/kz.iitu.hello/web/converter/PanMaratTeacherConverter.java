package kz.iitu.hello.web.converter;

import kz.iitu.hello.domain.entity.PanMaratCourse;
import kz.iitu.hello.domain.entity.PanMaratTeacher;
import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.web.dto.form.PanMaratTeacherFormDto;
import kz.iitu.hello.web.dto.grid.PanMaratCourseGridDto;
import kz.iitu.hello.web.dto.grid.PanMaratUserGridDto;
import kz.iitu.hello.web.dto.view.PanMaratTeacherViewDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PanMaratTeacherConverter {

    public void applyFormToEntity(PanMaratTeacherFormDto form, PanMaratTeacher teacher, PanMaratUser user, List<PanMaratCourse> courses) {
        teacher.setTeacherName(form.getTeacherName());
        teacher.setDepartment(form.getDepartment());
        teacher.setExperienceYears(form.getExperienceYears());
        teacher.setUser(user);
        teacher.setCourses(courses);
    }

    public PanMaratTeacherFormDto toFormDto(PanMaratTeacher teacher) {
        PanMaratTeacherFormDto dto = new PanMaratTeacherFormDto();
        dto.setId(teacher.getId());
        dto.setTeacherName(teacher.getTeacherName());
        dto.setDepartment(teacher.getDepartment());
        dto.setExperienceYears(teacher.getExperienceYears());
        if (teacher.getUser() != null) {
            dto.setUserId(teacher.getUser().getId());
        }
        if (teacher.getCourses() != null) {
            dto.setCourseIds(teacher.getCourses().stream().map(PanMaratCourse::getId).toList());
        }
        return dto;
    }

    public PanMaratTeacherViewDto toViewDto(PanMaratTeacher teacher) {
        PanMaratTeacherViewDto dto = new PanMaratTeacherViewDto();
        dto.setId(teacher.getId());
        dto.setTeacherName(teacher.getTeacherName());
        dto.setDepartment(teacher.getDepartment());
        dto.setExperienceYears(teacher.getExperienceYears());
        if (teacher.getUser() != null) {
            dto.setUser(toUserGridDto(teacher.getUser()));
        }
        List<PanMaratCourseGridDto> courses = new ArrayList<>();
        if (teacher.getCourses() != null) {
            for (PanMaratCourse course : teacher.getCourses()) {
                courses.add(toCourseGridDto(course));
            }
        }
        dto.setCourses(courses);
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
