package com.example.testJwt.service;

import com.example.testJwt.dto.JoinDto;
import com.example.testJwt.entity.User;
import com.example.testJwt.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDto joinDto) {

        if (userRepository.existsByUsername(joinDto.getUsername())) {
            throw new RuntimeException("이미 존재하는 유저네임 입니다.");
        }
        User user = dtoToUser(joinDto);
        user.setRole("ROLE_ADMIN");
        userRepository.save(user);
    }

    public User dtoToUser(JoinDto joinDto) {
        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(joinDto.getPassword()));
        return user;
    }
}
