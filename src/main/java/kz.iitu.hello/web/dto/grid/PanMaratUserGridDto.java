package kz.iitu.hello.web.dto.grid;

import kz.iitu.hello.domain.enums.PanMaratUserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanMaratUserGridDto {

    private Long id;
    private String userName;
    private String email;
    private PanMaratUserRole role;
}
