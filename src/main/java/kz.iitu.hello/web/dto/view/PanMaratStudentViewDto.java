package kz.iitu.hello.web.dto.view;

import kz.iitu.hello.web.dto.grid.PanMaratCourseGridDto;
import kz.iitu.hello.web.dto.grid.PanMaratUserGridDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanMaratStudentViewDto {

    private Long id;
    private String studentName;
    private Integer age;
    private String groupName;
    private Double gpa;
    private PanMaratUserGridDto user;
    private List<PanMaratCourseGridDto> courses;
}
