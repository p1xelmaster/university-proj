package kz.iitu.hello.web.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class PanMaratLoginController {

    @GetMapping
    public String loginPage() {
        return "public";
    }
}
