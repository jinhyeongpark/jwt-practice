package com.example.testJwt.service;

import com.example.testJwt.dto.CustomUserDetails;
import com.example.testJwt.entity.User;
import com.example.testJwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //DB에서 조회
        User user = userRepository.findByUsername(username);

        if (user == null) throw new UsernameNotFoundException("존재하지 않는 유저네임 입니다.");
        //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
        return new CustomUserDetails(user);
    }
}
