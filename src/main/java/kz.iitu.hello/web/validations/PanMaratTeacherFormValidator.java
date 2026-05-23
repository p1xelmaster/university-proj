package kz.iitu.hello.web.validations;

import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.domain.enums.PanMaratUserRole;
import kz.iitu.hello.domain.repository.PanMaratTeachersRepository;
import kz.iitu.hello.domain.repository.PanMaratUsersRepository;
import kz.iitu.hello.web.dto.form.PanMaratTeacherFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PanMaratTeacherFormValidator {

    private final PanMaratUsersRepository usersRepository;
    private final PanMaratTeachersRepository teachersRepository;

    public void validate(PanMaratTeacherFormDto form, BindingResult bindingResult, Long currentId) {
        if (form.getUserId() == null) {
            return;
        }

        Optional<PanMaratUser> userOpt = usersRepository.findById(form.getUserId());
        if (userOpt.isEmpty()) {
            bindingResult.rejectValue("userId", "userId.notFound", "Selected user does not exist");
            return;
        }

        if (userOpt.get().getRole() != PanMaratUserRole.TEACHER) {
            bindingResult.rejectValue("userId", "userId.wrongRole", "Selected user must have TEACHER role");
        }

        boolean alreadyLinked = teachersRepository.findByUserId(form.getUserId())
                .map(existing -> !existing.getId().equals(currentId))
                .orElse(false);

        if (alreadyLinked) {
            bindingResult.rejectValue("userId", "userId.duplicate", "This user is already linked to another teacher");
        }
    }
}
