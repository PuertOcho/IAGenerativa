package com.nuka.nuka_server.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.nuka.nuka_server.Constantes;
import com.nuka.nuka_server.models.User;
import com.nuka.nuka_server.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userNick) throws UsernameNotFoundException {
        User user = userRepository.findByUserNick(userNick);
        List<String> roles = new ArrayList<>();
        
        if (!ObjectUtils.isEmpty(user) && !ObjectUtils.isEmpty(user.getPermission())) {
            roles.add(user.getPermission());
        } else {
            roles.add(Constantes.ROLE_APP_USER);
        }
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUserNick())
                        .password(user.getPassword())
                        .roles(roles.toArray(new String[0]))
                        .build();
        return userDetails;
    }
}
