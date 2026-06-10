package com.campus.trade.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.entity.Item;
import com.campus.trade.entity.TradeRecord;
import com.campus.trade.mapper.ItemMapper;
import com.campus.trade.mapper.TradeRecordMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/admin/export")
public class ExportController {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private TradeRecordMapper tradeMapper;

    @GetMapping("/items")
    public void exportItems(HttpServletResponse response) throws IOException {
        setExcelHeader(response, "物品列表");
        List<Item> items = itemMapper.selectList(
                new LambdaQueryWrapper<Item>().orderByDesc(Item::getCreateTime));
        List<ItemExportRow> rows = items.stream().map(i -> {
            ItemExportRow r = new ItemExportRow();
            r.setId(i.getId());
            r.setTitle(i.getTitle());
            r.setCategory(i.getCategory());
            r.setPrice(i.getPrice().toString());
            r.setStatus(i.getStatus());
            return r;
        }).toList();
        EasyExcel.write(response.getOutputStream(), ItemExportRow.class).sheet("物品").doWrite(rows);
    }

    //用于发布物品
    @GetMapping("/trades")
    public void exportTrades(HttpServletResponse response) throws IOException {
        setExcelHeader(response, "交易记录");
        List<TradeRecord> trades = tradeMapper.selectList(
                new LambdaQueryWrapper<TradeRecord>().orderByDesc(TradeRecord::getCreateTime));
        List<TradeExportRow> rows = trades.stream().map(t -> {
            TradeExportRow r = new TradeExportRow();
            r.setTradeNo(t.getTradeNo());
            r.setItemId(t.getItemId());
            r.setAmount(t.getAmount().toString());
            r.setStatus(t.getStatus());
            return r;
        }).toList();
        EasyExcel.write(response.getOutputStream(), TradeExportRow.class).sheet("交易").doWrite(rows);
    }

    private void setExcelHeader(HttpServletResponse response, String name) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }

    public static class ItemExportRow {
        private Long id;
        private String title;
        private String category;
        private String price;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class TradeExportRow {
        private String tradeNo;
        private Long itemId;
        private String amount;
        private String status;

        public String getTradeNo() { return tradeNo; }
        public void setTradeNo(String tradeNo) { this.tradeNo = tradeNo; }
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
