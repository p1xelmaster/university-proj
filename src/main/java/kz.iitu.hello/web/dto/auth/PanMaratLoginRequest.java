package kz.iitu.hello.web.dto.auth;

import lombok.Data;

@Data
public class PanMaratLoginRequest {
    private String username;
    private String password;
}
