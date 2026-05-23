package kz.iitu.hello.web.dto.search;

import kz.iitu.hello.domain.enums.PanMaratDepartment;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class PanMaratTeacherSearchForm {
    private String name;
    private PanMaratDepartment department;
    private Integer experienceYearsFrom;
    private Integer experienceYearsTo;
    private String sortBy;
    private Sort.Direction sortDirection;
}
