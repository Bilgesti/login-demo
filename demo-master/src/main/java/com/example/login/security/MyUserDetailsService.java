package com.example.login.security;

import com.example.login.model.UserDTO;
import com.example.login.model.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    public MyUserDetailsService(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        com.example.login.model.User user = userRepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("No user found with email: " + username);
        }
        Collection<? extends GrantedAuthority> authorities = getAuthorities(user.getRole());

        return new User(user.getEmail(), user.getPassword(),true,true,true,true, authorities);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
