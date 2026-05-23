package kz.iitu.hello.web.dto.auth;

import kz.iitu.hello.domain.enums.PanMaratUserRole;
import lombok.Data;

@Data
public class PanMaratRegisterRequest {
    private String username;
    private String email;
    private String password;
    private PanMaratUserRole role;
}
