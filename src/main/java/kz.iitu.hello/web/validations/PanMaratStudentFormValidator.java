package kz.iitu.hello.web.validations;

import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.domain.enums.PanMaratUserRole;
import kz.iitu.hello.domain.repository.PanMaratStudentsRepository;
import kz.iitu.hello.domain.repository.PanMaratUsersRepository;
import kz.iitu.hello.web.dto.form.PanMaratStudentFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PanMaratStudentFormValidator {

    private final PanMaratUsersRepository usersRepository;
    private final PanMaratStudentsRepository studentsRepository;

    public void validate(PanMaratStudentFormDto form, BindingResult bindingResult, Long currentId) {
        if (form.getUserId() == null) {
            return;
        }

        Optional<PanMaratUser> userOpt = usersRepository.findById(form.getUserId());
        if (userOpt.isEmpty()) {
            bindingResult.rejectValue("userId", "userId.notFound", "Selected user does not exist");
            return;
        }

        if (userOpt.get().getRole() != PanMaratUserRole.STUDENT) {
            bindingResult.rejectValue("userId", "userId.wrongRole", "Selected user must have STUDENT role");
        }

        boolean alreadyLinked = studentsRepository.findByUserId(form.getUserId())
                .map(existing -> !existing.getId().equals(currentId))
                .orElse(false);

        if (alreadyLinked) {
            bindingResult.rejectValue("userId", "userId.duplicate", "This user is already linked to another student");
        }
    }
}
