package org.example.tacoorderingsystemapi.controller;

import org.example.tacoorderingsystemapi.dto.WxLoginDTO;
import org.example.tacoorderingsystemapi.service.C_AuthService;
import org.example.tacoorderingsystemapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/c/auth")
public class C_AuthController {

    @Autowired
    private C_AuthService authService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody WxLoginDTO dto) {
        String token = authService.login(dto.getCode());
        return Result.success(token);
    }
}
