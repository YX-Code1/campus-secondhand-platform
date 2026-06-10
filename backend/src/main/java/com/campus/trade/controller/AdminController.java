package com.campus.trade.controller;

import com.campus.trade.common.PageResult;
import com.campus.trade.common.Result;
import com.campus.trade.dto.ItemVO;
import com.campus.trade.dto.StatsVO;
import com.campus.trade.dto.UserVO;
import com.campus.trade.service.AdminService;
import com.campus.trade.service.ItemService;
import com.campus.trade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @GetMapping("/stats")
    public Result<StatsVO> stats() {
        return Result.ok(adminService.getStats());
    }

    @GetMapping("/users")
    public Result<PageResult<UserVO>> users(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(userService.listUsers(page, size));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @GetMapping("/items/audit")
    public Result<List<ItemVO>> pendingAudit() {
        return Result.ok(adminService.pendingAuditItems());
    }

    @PutMapping("/items/{id}/audit")
    public Result<Void> auditItem(@PathVariable Long id, @RequestBody Map<String, String> body) {
        itemService.audit(id, body.get("auditStatus"));
        return Result.ok();
    }

    @PutMapping("/items/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id) {
        itemService.adminOffShelf(id);
        return Result.ok();
    }
}
