package kz.iitu.hello.web.converter;

import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.web.dto.form.PanMaratUserFormDto;
import kz.iitu.hello.web.dto.grid.PanMaratUserGridDto;
import org.springframework.stereotype.Component;

@Component
public class PanMaratUserConverter {

    public void applyFormToEntity(PanMaratUserFormDto form, PanMaratUser user) {
        user.setUserName(form.getUserName());
        user.setEmail(form.getEmail());
        user.setRole(form.getRole());
    }

    public PanMaratUserFormDto toFormDto(PanMaratUser user) {
        PanMaratUserFormDto dto = new PanMaratUserFormDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    public PanMaratUserGridDto toGridDto(PanMaratUser user) {
        PanMaratUserGridDto dto = new PanMaratUserGridDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
