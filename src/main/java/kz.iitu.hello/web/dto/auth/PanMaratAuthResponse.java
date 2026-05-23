package kz.iitu.hello.web.dto.auth;

import kz.iitu.hello.domain.enums.PanMaratUserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PanMaratAuthResponse {
    private String token;
    private PanMaratUserRole role;
    private Long userId;
}
