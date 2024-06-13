package com.example.login.web;

import com.example.login.model.User;
import com.example.login.model.UserDTO;
import com.example.login.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class RegisterController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    public RegisterController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register_form";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register_form";
        } else {
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
    public String adminPage(){
        return "adminPage";
    }

    @GetMapping("/users")
    public String userPage( Model model){
        List<User> users = userRepository.findAll();
        model.addAttribute( "users", users );
        return "userPage";
    }

    @PostMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId) {
        userRepository.deleteById(userId);
        return "redirect:/admin/delete-success";
    }

    @GetMapping("/admin/delete-success")
    public String deleteSuccess() {
        return "delete_success";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

}
