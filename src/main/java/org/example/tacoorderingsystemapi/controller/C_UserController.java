package org.example.tacoorderingsystemapi.controller;

import org.example.tacoorderingsystemapi.dto.UpdateProfileDTO;
import org.example.tacoorderingsystemapi.service.C_UserService;
import org.example.tacoorderingsystemapi.vo.Result;
import org.example.tacoorderingsystemapi.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/c/user")
public class C_UserController {

    @Autowired
    private C_UserService userService;

    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile() {
        UserProfileVO profile = userService.getProfile();
        return Result.success(profile);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileDTO dto) {
        userService.updateProfile(dto);
        return Result.success();
    }
}
