package io.ussopm.csrfbackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @GetMapping("new-password")
    public String getUserStaticPage() {
        return "redirect:/user";
    }

//    @GetMapping("change-password")
//    public String changePasswordByGet(@AuthenticationPrincipal UserDetails userDetails, String newPassword) {
//        LOG.info("GET user password {} was changed to {}", userDetails.getUsername(), newPassword);
//        return "redirect:/password-changed.html";
//    }

    @GetMapping("/static")
    public String getUser() {
        return "user_static";
    }

    @PostMapping("change-password")
    public String changePasswordByPost(@AuthenticationPrincipal UserDetails userDetails, String newPassword) {
        LOG.info("POST user password {} was changed to {}", userDetails.getUsername(), newPassword);
        return "redirect:/password-changed.html";
    }
}
