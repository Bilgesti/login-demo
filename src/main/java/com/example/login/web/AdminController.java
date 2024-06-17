package com.example.login.web;

import com.example.login.model.User;
import com.example.login.model.UserDTO;
import com.example.login.service.UserService;
import com.example.login.util.MaskingUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDTO());
        logger.debug("Register user");
        return "register_form";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult) {
        logger.debug("Processing registration for email: {}", MaskingUtils.anonymize(userDTO.getEmail()));
        if (bindingResult.hasErrors()) {
            logger.warn("Registration failed due to binding errors for email: {}", MaskingUtils.anonymize(userDTO.getEmail()));
            return "register_form";
        }

        userService.registerUser(userDTO);
        logger.debug("User registered successfully with email: {}", MaskingUtils.anonymize(userDTO.getEmail()));
        return "register_success";
    }

    @GetMapping("/homepage")
    public String loggedIn() {
        logger.debug("Homepage");
        return "homepage";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new UserDTO());
        logger.debug("Showing login page.");
        return "login";
    }

    @GetMapping("/admin")
    public String adminPage() {
        logger.debug("Admin accessed admin page.");
        return "adminPage";
    }

    @GetMapping("/users")
    public String userPage(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        logger.debug("Showing usersPage with a list of registered users.");
        return "userPage";
    }

    @GetMapping("/delete")
    public String deleteForm(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("email", "");
        logger.debug("Showing delete form.");
        return "delete_form";
    }

    @PostMapping("/delete")
    public String deleteUser(@ModelAttribute("email") String email, Model model, Authentication authentication) {
        logger.debug("Attempting to delete user with email: {}", MaskingUtils.anonymize(email));

        try {
            userService.deleteUser(email);
            logger.debug("User with email: {} deleted successfully.", MaskingUtils.anonymize(email));
            return "redirect:/delete_success";
        } catch (RuntimeException e) {
            logger.warn("User with email: {} could not be found", MaskingUtils.anonymize(email));
            model.addAttribute("error", "User not found");
            return "delete_error";
        }
    }

    @GetMapping("/delete_success")
    public String deleteSuccess() {
        logger.debug("User deleted successfully.");
        return "delete_success";
    }

    @GetMapping("/delete-error")
    public String deleteError() {
        logger.debug("Error during user deletion.");
        return "delete_error";
    }
}
