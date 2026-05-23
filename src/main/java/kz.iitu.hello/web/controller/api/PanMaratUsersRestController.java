package kz.iitu.hello.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.iitu.hello.service.PanMaratUserService;
import kz.iitu.hello.web.dto.form.PanMaratUserFormDto;
import kz.iitu.hello.web.dto.grid.PanMaratUserGridDto;
import kz.iitu.hello.web.dto.search.PanMaratUserSearchForm;
import kz.iitu.hello.web.validations.BindingResultValidationUtils;
import kz.iitu.hello.web.validations.UserFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "CRUD operations for users (ADMIN only)")
public class PanMaratUsersRestController {
    private final PanMaratUserService userService;
    private final UserFormValidator userFormValidator;

    @GetMapping
    @Operation(summary = "Search users", description = "Search and paginate users with optional filters")
    public Page<PanMaratUserGridDto> read(PanMaratUserSearchForm searchForm,
                                          @PageableDefault(size = 10) Pageable pageable) {
        return userService.search(searchForm, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user", description = "Create a new user account")
    public void create(@RequestBody PanMaratUserFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        userFormValidator.validate(form, br, null);
        BindingResultValidationUtils.validate(br);
        userService.create(form);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user by ID")
    public void update(@PathVariable Long id, @RequestBody PanMaratUserFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        userFormValidator.validate(form, br, id);
        BindingResultValidationUtils.validate(br);
        userService.update(id, form);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user", description = "Delete a user by ID")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
