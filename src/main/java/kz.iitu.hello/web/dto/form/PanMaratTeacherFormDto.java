package kz.iitu.hello.web.dto.form;

import jakarta.validation.constraints.*;
import kz.iitu.hello.domain.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanMaratTeacherFormDto {

    private Long id;

    @NotBlank(message = "PanMaratTeacher name is required")
    @Size(min = 2, max = 100, message = "PanMaratTeacher name must be between 2 and 100 characters")
    private String teacherName;

    @NotNull(message = "Experience years is required")
    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 60, message = "Experience years must be at most 60")
    private Integer experienceYears;

    @NotNull(message = "Department is required")
    private Department department;

    @NotNull(message = "PanMaratUser is required")
    private Long userId;

    @Size(max = 1000, message = "Too many courses selected")
    private List<Long> courseIds;
}
