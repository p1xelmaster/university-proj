package kz.iitu.hello.web.dto.form;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanMaratStudentFormDto {

    private Long id;

    @NotBlank(message = "PanMaratStudent name is required")
    @Size(min = 2, max = 50, message = "PanMaratStudent name must be between 2 and 50 characters")
    private String studentName;

    @NotNull(message = "Age is required")
    @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 100, message = "Age must be at most 100")
    private Integer age;

    @NotNull(message = "GPA is required")
    @DecimalMin(value = "0.0", message = "GPA must be at least 0.0")
    @DecimalMax(value = "4.0", message = "GPA must be at most 4.0")
    private Double gpa;

    @NotBlank(message = "Group name is required")
    @Size(min = 2, max = 30, message = "Group name must be between 2 and 30 characters")
    private String groupName;

    @NotNull(message = "PanMaratUser is required")
    private Long userId;

    private List<Long> courseIds;
}
