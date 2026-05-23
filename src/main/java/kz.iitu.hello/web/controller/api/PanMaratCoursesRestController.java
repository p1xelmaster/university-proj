package kz.iitu.hello.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.iitu.hello.service.PanMaratCourseService;
import kz.iitu.hello.web.dto.form.PanMaratCourseFormDto;
import kz.iitu.hello.web.dto.search.PanMaratCourseSearchForm;
import kz.iitu.hello.web.dto.view.PanMaratCourseViewDto;
import kz.iitu.hello.web.validations.PanMaratBindingResultValidationUtils;
import kz.iitu.hello.web.validations.PanMaratCourseFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "CRUD operations for courses (ADMIN only)")
public class PanMaratCoursesRestController {
    private final PanMaratCourseService courseService;
    private final PanMaratCourseFormValidator courseFormValidator;

    @GetMapping
    @Operation(summary = "Search courses", description = "Search and paginate courses with optional filters")
    public Page<PanMaratCourseViewDto> read(PanMaratCourseSearchForm form,
                                            @PageableDefault(size = 10) Pageable pageable) {
        return courseService.search(form, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create course", description = "Create a new course with teacher and optional students")
    public void create(@RequestBody PanMaratCourseFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        courseFormValidator.validate(form, br, null);
        PanMaratBindingResultValidationUtils.validate(br);
        courseService.create(form);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course", description = "Update an existing course by ID")
    public void update(@PathVariable Long id, @RequestBody PanMaratCourseFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        courseFormValidator.validate(form, br, id);
        PanMaratBindingResultValidationUtils.validate(br);
        courseService.update(id, form);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete course", description = "Delete a course by ID")
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}
