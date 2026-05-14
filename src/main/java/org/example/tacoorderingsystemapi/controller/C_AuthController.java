package org.example.tacoorderingsystemapi.controller;

import org.example.tacoorderingsystemapi.dto.WxLoginDTO;
import org.example.tacoorderingsystemapi.service.C_AuthService;
import org.example.tacoorderingsystemapi.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/c/auth")
public class C_AuthController {

    private static final Logger log = LoggerFactory.getLogger(C_AuthController.class);

    @Autowired
    private C_AuthService authService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody WxLoginDTO dto) {
        String token = authService.login(dto.getCode(), dto.getNickname(), dto.getPhone());
        return Result.success(token);
    }
}
