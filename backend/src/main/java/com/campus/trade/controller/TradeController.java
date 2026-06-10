package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.TradeVO;
import com.campus.trade.security.SecurityUtils;
import com.campus.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trades")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    @PostMapping
    public Result<TradeVO> create(@RequestBody Map<String, Long> body) {
        Long itemId = body.get("itemId");
        if (itemId == null) return Result.fail("物品ID不能为空");
        return Result.ok(tradeService.createTrade(SecurityUtils.currentUser().getId(), itemId));
    }

    @GetMapping("/mine")
    public Result<List<TradeVO>> myTrades() {
        return Result.ok(tradeService.myTrades(SecurityUtils.currentUser().getId()));
    }

    @GetMapping("/{tradeNo}")
    public Result<TradeVO> detail(@PathVariable String tradeNo) {
        return Result.ok(tradeService.getByTradeNo(tradeNo));
    }

    @PutMapping("/{id}/status")
    public Result<TradeVO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) return Result.fail("状态不能为空");
        return Result.ok(tradeService.updateStatus(
                SecurityUtils.currentUser().getId(), id, status, SecurityUtils.isAdmin()));
    }
}
