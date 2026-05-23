package kz.iitu.hello.web.controller.mvc;

import jakarta.validation.Valid;
import kz.iitu.hello.service.PanMaratStudentService;
import kz.iitu.hello.web.dto.form.PanMaratStudentFormDto;
import kz.iitu.hello.web.dto.search.PanMaratStudentSearchForm;
import kz.iitu.hello.web.validations.BindingResultValidationUtils;
import kz.iitu.hello.web.validations.StudentFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/students")
public class PanMaratStudentsController {
    private final PanMaratStudentService studentService;
    private final StudentFormValidator studentFormValidator;

    @GetMapping
    public String read(@RequestParam(name = "id", required = false) Long id,
                       @ModelAttribute("searchForm") PanMaratStudentSearchForm searchForm,
                       @PageableDefault(size = 10) Pageable pageable,
                       Model model) {
        model.addAttribute("form", studentService.getForm(id));
        model.addAttribute("editMode", id != null);
        fillCommonAttributes(model, searchForm, pageable);
        return "students";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") PanMaratStudentFormDto form, BindingResult bindingResult, Model model) {
        studentFormValidator.validate(form, bindingResult, form.getId());
        if (BindingResultValidationUtils.hasErrors(bindingResult)) {
            return renderFormWithErrors(model, form, false);
        }
        studentService.create(form);
        return "redirect:/students";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") PanMaratStudentFormDto form, BindingResult bindingResult, Model model) {
        studentFormValidator.validate(form, bindingResult, form.getId());
        if (BindingResultValidationUtils.hasErrors(bindingResult)) {
            form.setId(id);
            return renderFormWithErrors(model, form, true);
        }
        studentService.update(id, form);
        return "redirect:/students";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        studentService.delete(id);
        return "redirect:/students";
    }

    private String renderFormWithErrors(Model model, PanMaratStudentFormDto form, boolean editMode) {
        model.addAttribute("form", form);
        model.addAttribute("editMode", editMode);
        fillCommonAttributes(model, new PanMaratStudentSearchForm(), Pageable.ofSize(10));
        return "students";
    }

    private void fillCommonAttributes(Model model, PanMaratStudentSearchForm form, Pageable pageable) {
        model.addAttribute("page", studentService.search(form, pageable));
        model.addAttribute("searchForm", form);
        model.addAttribute("users", studentService.findAllUsers());
        model.addAttribute("courses", studentService.findAllCourses());
    }
}
