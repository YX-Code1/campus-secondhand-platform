package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.UserBriefVO;
import com.campus.trade.dto.UserProfileRequest;
import com.campus.trade.dto.UserVO;
import com.campus.trade.security.SecurityUtils;
import com.campus.trade.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    //响应/profile请求
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.ok(userService.getProfile(SecurityUtils.currentUser().getId()));
    }

    @GetMapping("/{id}/brief")
    public Result<UserBriefVO> brief(@PathVariable Long id) {
        return Result.ok(userService.getBrief(id));
    }

    @PutMapping("/me")
    public Result<UserVO> updateMe(@RequestBody UserProfileRequest req) {
        return Result.ok(userService.updateProfile(SecurityUtils.currentUser().getId(), req));
    }
}
