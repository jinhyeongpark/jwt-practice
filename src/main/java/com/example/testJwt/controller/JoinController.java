package com.example.testJwt.controller;

import com.example.testJwt.dto.JoinDto;
import com.example.testJwt.service.JoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController {
    private final JoinService joinService;

    public JoinController(JoinService joinService) {

        this.joinService = joinService;
    }

    @PostMapping("/join")
    public ResponseEntity<JoinDto> joinProcess(@RequestBody JoinDto joinDto) {

        System.out.println(joinDto.getUsername());
        joinService.joinProcess(joinDto);

        return ResponseEntity.ok(joinDto);
    }
}
