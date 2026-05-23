package kz.iitu.hello.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.iitu.hello.service.PanMaratTeacherService;
import kz.iitu.hello.web.dto.form.PanMaratTeacherFormDto;
import kz.iitu.hello.web.dto.search.PanMaratTeacherSearchForm;
import kz.iitu.hello.web.dto.view.PanMaratTeacherViewDto;
import kz.iitu.hello.web.validations.BindingResultValidationUtils;
import kz.iitu.hello.web.validations.TeacherFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "CRUD operations for teachers (ADMIN only)")
public class PanMaratTeachersRestController {
    private final PanMaratTeacherService teacherService;
    private final TeacherFormValidator teacherFormValidator;

    @GetMapping
    @Operation(summary = "Search teachers", description = "Search and paginate teachers with optional filters")
    public Page<PanMaratTeacherViewDto> read(PanMaratTeacherSearchForm form,
                                             @PageableDefault(size = 10) Pageable pageable) {
        return teacherService.search(form, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create teacher", description = "Create a new teacher linked to an existing user")
    public void create(@Valid @RequestBody PanMaratTeacherFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        teacherFormValidator.validate(form, br, null);
        BindingResultValidationUtils.validate(br);
        teacherService.create(form);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update teacher", description = "Update an existing teacher by ID")
    public void update(@PathVariable Long id, @Valid @RequestBody PanMaratTeacherFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        teacherFormValidator.validate(form, br, id);
        BindingResultValidationUtils.validate(br);
        teacherService.update(id, form);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete teacher", description = "Delete a teacher by ID")
    public void delete(@PathVariable Long id) {
        teacherService.delete(id);
    }
}
