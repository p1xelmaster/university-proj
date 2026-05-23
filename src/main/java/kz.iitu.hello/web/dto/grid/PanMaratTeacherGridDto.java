package kz.iitu.hello.web.dto.grid;

import kz.iitu.hello.domain.enums.PanMaratDepartment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanMaratTeacherGridDto {

    private Long id;
    private String teacherName;
    private Integer experienceYears;
    private PanMaratDepartment department;
}
