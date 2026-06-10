package com.campus.trade.controller;

import com.campus.trade.common.PageResult;
import com.campus.trade.common.Result;
import com.campus.trade.dto.ItemRequest;
import com.campus.trade.dto.ItemVO;
import com.campus.trade.security.SecurityUtils;
import com.campus.trade.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @org.springframework.beans.factory.annotation.Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping
    public Result<PageResult<ItemVO>> search(
            //@RequestParam(required = false)表示这个参数不是必须的
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return Result.ok(itemService.search(keyword, category, minPrice, maxPrice, page, size,
                SecurityUtils.currentUserIdOrNull()));
    }

    @GetMapping("/hot")
    public Result<java.util.List<ItemVO>> hot(@RequestParam(defaultValue = "8") int limit) {
        return Result.ok(itemService.hotItems(limit, SecurityUtils.currentUserIdOrNull()));
    }

    @GetMapping("/mine")
    public Result<PageResult<ItemVO>> myItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(itemService.myItems(SecurityUtils.currentUser().getId(), page, size));
    }

    @GetMapping("/{id}")
    public Result<ItemVO> detail(@PathVariable Long id) {
        return Result.ok(itemService.getDetail(id, SecurityUtils.currentUserIdOrNull()));
    }

    @PostMapping
    public Result<ItemVO> create(@Valid @RequestBody ItemRequest req) {
        //将创建商品后产生的返回给前端
        return Result.ok(itemService.create(SecurityUtils.currentUser().getId(), req));
    }

    @PutMapping("/{id}")
    public Result<ItemVO> update(@PathVariable Long id, @Valid @RequestBody ItemRequest req) {
        return Result.ok(itemService.update(SecurityUtils.currentUser().getId(), id, req, SecurityUtils.isAdmin()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        itemService.delete(SecurityUtils.currentUser().getId(), id, SecurityUtils.isAdmin());
        return Result.ok();
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.fail("文件为空");
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;
        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);
        file.transferTo(dir.resolve(filename).toFile());
        return Result.ok("/uploads/" + filename);
    }
}
