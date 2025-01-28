package com.example.testJwt.controller;

import com.example.testJwt.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader("Refresh-Token").substring(7); // "Bearer " 제거
        if (jwtUtil.isExpired(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtUtil.getUsername(refreshToken);
        String accessToken = jwtUtil.createAccessToken(username, jwtUtil.getRole(refreshToken), 60*60*1000L); // 1시간

        response.addHeader("Authorization", "Bearer " + accessToken);
    }
}
