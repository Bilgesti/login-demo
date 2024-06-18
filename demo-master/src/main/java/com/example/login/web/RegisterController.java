package com.example.login.web;

import com.example.login.model.User;
import com.example.login.model.UserDTO;
import com.example.login.repository.UserRepository;
import com.example.login.service.AdminDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RegisterController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AdminDetailsService adminDetailsService;
    private final UserDetailsService userDetailsService;

    public RegisterController(PasswordEncoder passwordEncoder, UserRepository userRepository, AdminDetailsService adminDetailsService, @Qualifier("userDetailsService") UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.adminDetailsService = adminDetailsService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register_form";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register_form";
        } else {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                model.addAttribute("emailError", "Email is already registered");
                return "register_form";
            }

            User user = new User();
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setEmail(userDTO.getEmail());
            user.setRole("USER");
            userRepository.save(user);
            return "register_success";
        }
    }

    @GetMapping("/homepage")
    public String loggedIn() {
        return "homepage";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "login";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "adminPage";
    }

    @GetMapping("/users")
    public String userPage(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "userPage";
    }

    @GetMapping("/delete")
    public String deleteForm(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("email", "");
        return "delete_form";
    }

    @PostMapping("/delete")
    public String deleteUser(@ModelAttribute("email") String email, Model model, Authentication authentication) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "User not found");
            return "delete_error";
        }

        if (user.getEmail().equals(authentication.getName())) {
            model.addAttribute("error", "Admin cannot delete themselves");
            return "delete_error";
        }

        userRepository.delete(user);
        return "redirect:/delete_success";
    }

    @GetMapping("/delete_success")
    public String deleteSuccess() {
        return "delete_success";
    }

    @GetMapping("/delete-error")
    public String deleteError() {
        return "delete_error";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

}