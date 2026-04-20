package org.example.tacoorderingsystemapi.controller;

import org.example.tacoorderingsystemapi.dto.AdminLoginDTO;
import org.example.tacoorderingsystemapi.service.B_AuthService;
import org.example.tacoorderingsystemapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/b/auth")
public class B_AuthController {

    @Autowired
    private B_AuthService authService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody AdminLoginDTO dto) {
        String token = authService.login(dto.getUsername(), dto.getPassword());
        return Result.success(token);
    }
}
