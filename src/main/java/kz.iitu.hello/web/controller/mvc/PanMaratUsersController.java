package kz.iitu.hello.web.controller.mvc;

import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.service.PanMaratUserService;
import kz.iitu.hello.web.dto.form.PanMaratUserFormDto;
import kz.iitu.hello.web.dto.search.PanMaratUserSearchForm;
import kz.iitu.hello.web.validations.PanMaratBindingResultValidationUtils;
import kz.iitu.hello.web.validations.PanMaratUserFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class PanMaratUsersController {
    private final PanMaratUserService userService;
    private final PanMaratUserFormValidator userFormValidator;

    @GetMapping
    public String read(@RequestParam(name = "id", required = false) Long id,
                       @ModelAttribute("searchForm") PanMaratUserSearchForm searchForm,
                       @PageableDefault(size = 10) Pageable pageable,
                       Model model) {
        model.addAttribute("editMode", id != null);
        model.addAttribute("form", userService.getForm(id));
        fillCommonAttributes(model, searchForm, pageable);
        return "users";
    }

    @PostMapping
    public String create(@ModelAttribute("form") PanMaratUserFormDto form, BindingResult bindingResult, Model model) {
        userFormValidator.validate(form, bindingResult, form.getId());
        if (PanMaratBindingResultValidationUtils.hasErrors(bindingResult)) {
            return renderFormWithErrors(model, form, false);
        }
        userService.create(form);
        return "redirect:/users";
    }

    @PutMapping("{id}")
    public String update(@PathVariable Long id, @ModelAttribute("form") PanMaratUserFormDto form, BindingResult bindingResult, Model model) {
        userFormValidator.validate(form, bindingResult, form.getId());
        if (PanMaratBindingResultValidationUtils.hasErrors(bindingResult)) {
            form.setId(id);
            return renderFormWithErrors(model, form, true);
        }
        userService.update(id, form);
        return "redirect:/users";
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/users";
    }

    private String renderFormWithErrors(Model model, PanMaratUserFormDto form, boolean editMode) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("form", form);
        fillCommonAttributes(model, new PanMaratUserSearchForm(), Pageable.ofSize(10));
        return "users";
    }

    private void fillCommonAttributes(Model model, PanMaratUserSearchForm form, Pageable pageable) {
        model.addAttribute("page", userService.search(form, pageable));
        model.addAttribute("searchForm", form);
        model.addAttribute("roles", UserRole.values());
    }
}
