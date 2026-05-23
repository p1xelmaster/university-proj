package kz.iitu.hello.web.dto.view;

import kz.iitu.hello.web.dto.grid.PanMaratStudentGridDto;
import kz.iitu.hello.web.dto.grid.PanMaratTeacherGridDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanMaratCourseViewDto {

    private Long id;
    private String courseName;
    private Integer credits;
    private Integer maxStudents;
    private PanMaratTeacherGridDto teacher;
    private List<PanMaratStudentGridDto> students;
}
