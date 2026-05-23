package kz.iitu.hello.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.iitu.hello.service.PanMaratStudentService;
import kz.iitu.hello.web.dto.form.PanMaratStudentFormDto;
import kz.iitu.hello.web.dto.search.PanMaratStudentSearchForm;
import kz.iitu.hello.web.dto.view.PanMaratStudentViewDto;
import kz.iitu.hello.web.validations.PanMaratBindingResultValidationUtils;
import kz.iitu.hello.web.validations.PanMaratStudentFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "CRUD operations for students (ADMIN only)")
public class PanMaratStudentsRestController {
    private final PanMaratStudentService studentService;
    private final PanMaratStudentFormValidator studentFormValidator;

    @GetMapping
    @Operation(summary = "Search students", description = "Search and paginate students with optional filters")
    public Page<PanMaratStudentViewDto> read(PanMaratStudentSearchForm form,
                                             @PageableDefault(size = 10) Pageable pageable) {
        return studentService.search(form, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create student", description = "Create a new student linked to an existing user")
    public void create(@Valid @RequestBody PanMaratStudentFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        studentFormValidator.validate(form, br, null);
        PanMaratBindingResultValidationUtils.validate(br);
        studentService.create(form);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student", description = "Update an existing student by ID")
    public void update(@PathVariable Long id, @Valid @RequestBody PanMaratStudentFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        studentFormValidator.validate(form, br, id);
        PanMaratBindingResultValidationUtils.validate(br);
        studentService.update(id, form);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete student", description = "Delete a student by ID")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}
