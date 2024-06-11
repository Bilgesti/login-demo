package com.example.login.web;

import com.example.login.model.User;
import com.example.login.model.UserDTO;
import com.example.login.model.UserRepository;
import jakarta.validation.Valid;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("Error");
            return "register_form";
        } else {
            User user = new User();
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setEmail(userDTO.getEmail());
            user.setSecret(Base32.random());
            user.setRole("USER");
            userRepository.save(user);

            model.addAttribute(user);

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
        return "Login";
    }

    @GetMapping("/admin/home")
    public String adminPage(){
        return "AdminPage";
    }

    @GetMapping("/user/home")
    public String userPage(@ModelAttribute("user") UserDTO user, Model model){
        model.addAttribute( "user", user );
        return "userPage";
    }
}
