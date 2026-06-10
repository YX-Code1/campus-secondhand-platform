package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.LoginRequest;
import com.campus.trade.dto.LoginResponse;
import com.campus.trade.dto.RegisterRequest;
import com.campus.trade.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    //判断是否符合格式，然后按逻辑在数据库保存数据
    public Result<Void> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return Result.ok();
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(authService.login(req));
    }
}
