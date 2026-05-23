package kz.iitu.hello.web.validations;

import kz.iitu.hello.web.dto.form.PanMaratCourseFormDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class PanMaratCourseFormValidator {

    public void validate(PanMaratCourseFormDto form, BindingResult bindingResult, Long currentId) {
        if (form.getMaxStudents() != null && form.getStudentIds() != null
                && form.getStudentIds().size() > form.getMaxStudents()) {
            bindingResult.rejectValue("studentIds", "studentIds.tooMany",
                    "Number of selected students exceeds the maximum allowed (" + form.getMaxStudents() + ")");
        }
    }
}
