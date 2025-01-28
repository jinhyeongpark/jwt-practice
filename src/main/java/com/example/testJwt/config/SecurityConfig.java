package com.example.testJwt.config;

import com.example.testJwt.jwt.JWTFilter;
import com.example.testJwt.jwt.JWTUtil;
import com.example.testJwt.jwt.LoginFilter;
import com.example.testJwt.jwt.TokenBlacklist;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
        TokenBlacklist tokenBlacklist) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http
            .csrf((auth) -> auth.disable());

        http
            .formLogin((auth) -> auth.disable());

        http
            .httpBasic((auth) -> auth.disable());

        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/", "/join", "/logout").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        http
            .addFilterBefore(new JWTFilter(jwtUtil, tokenBlacklist), LoginFilter.class);

        http
            .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
            .cors((cordCutomizer -> cordCutomizer.configurationSource(
                request -> {

                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                    return configuration;
                })));

        http
            .logout((logout) -> logout
            .logoutUrl("/logout")
            .addLogoutHandler(logoutHandler())
            .logoutSuccessHandler(logoutSuccessHandler()));

        return http.build();
    }
    @Bean
    public LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.split(" ")[1];
                long expirationTime = jwtUtil.getExpiration(token).getTime();
                tokenBlacklist.add(token, expirationTime); // 토큰을 블랙리스트에 추가
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().flush();
        };
    }
}
