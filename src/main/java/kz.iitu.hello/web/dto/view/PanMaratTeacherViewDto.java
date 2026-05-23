package kz.iitu.hello.web.dto.view;

import kz.iitu.hello.domain.enums.PanMaratDepartment;
import kz.iitu.hello.web.dto.grid.PanMaratCourseGridDto;
import kz.iitu.hello.web.dto.grid.PanMaratUserGridDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanMaratTeacherViewDto {

    private Long id;
    private String teacherName;
    private Integer experienceYears;
    private PanMaratDepartment department;
    private PanMaratUserGridDto user;
    private List<PanMaratCourseGridDto> courses;
}
