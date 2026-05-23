package kz.iitu.hello.web.dto.search;

import kz.iitu.hello.domain.enums.PanMaratUserRole;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class PanMaratUserSearchForm {
    private String username;
    private String email;
    private PanMaratUserRole role;
    private String sortBy;
    private Sort.Direction sortDirection;
}
