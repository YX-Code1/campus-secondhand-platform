package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.TradeVO;
import com.campus.trade.entity.Item;
import com.campus.trade.entity.TradeRecord;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.ItemMapper;
import com.campus.trade.mapper.TradeRecordMapper;
import com.campus.trade.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class TradeService {
    @Autowired
    private TradeRecordMapper tradeMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public TradeVO createTrade(Long buyerId, Long itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (!"ON_SALE".equals(item.getStatus()) || !"APPROVED".equals(item.getAuditStatus())) {
            throw new BusinessException("物品不可购买");
        }
        if (item.getSellerId().equals(buyerId)) {
            throw new BusinessException("不能购买自己发布的物品");
        }
        TradeRecord trade = new TradeRecord();
        trade.setTradeNo(generateTradeNo());
        trade.setItemId(itemId);
        trade.setItemTitle(item.getTitle());
        trade.setBuyerId(buyerId);
        trade.setSellerId(item.getSellerId());
        trade.setAmount(item.getPrice());
        trade.setStatus("PENDING");
        item.setStatus("IN_TRADE");
        itemMapper.updateById(item);
        tradeMapper.insert(trade);
        return toVO(trade);
    }

    @Transactional
    public TradeVO updateStatus(Long userId, Long tradeId, String status, boolean isAdmin) {
        TradeRecord trade = tradeMapper.selectById(tradeId);
        if (trade == null) {
            throw new BusinessException("交易不存在");
        }
        if ("COMPLETED".equals(trade.getStatus()) || "CANCELLED".equals(trade.getStatus())) {
            throw new BusinessException("交易已完成或已取消，无法更改状态");
        }
        if (!isAdmin && !trade.getBuyerId().equals(userId) && !trade.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作该交易");
        }
        if (!List.of("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED").contains(status)) {
            throw new BusinessException("无效的交易状态");
        }
        trade.setStatus(status);
        Item item = itemMapper.selectById(trade.getItemId());
        if (item != null) {
            if ("COMPLETED".equals(status)) {
                item.setStatus("SOLD");
            } else if ("CANCELLED".equals(status)) {
                item.setStatus("ON_SALE");
            }
            itemMapper.updateById(item);
        }
        tradeMapper.updateById(trade);
        return toVO(trade);
    }

    public List<TradeVO> myTrades(Long userId) {
        List<TradeRecord> list = tradeMapper.selectList(new LambdaQueryWrapper<TradeRecord>()
                .and(w -> w.eq(TradeRecord::getBuyerId, userId).or().eq(TradeRecord::getSellerId, userId))
                .orderByDesc(TradeRecord::getCreateTime));
        return list.stream().map(this::toVO).toList();
    }

    public TradeVO getByTradeNo(String tradeNo) {
        TradeRecord trade = tradeMapper.selectOne(new LambdaQueryWrapper<TradeRecord>()
                .eq(TradeRecord::getTradeNo, tradeNo));
        if (trade == null) {
            throw new BusinessException("交易不存在");
        }
        return toVO(trade);
    }

    private String generateTradeNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "T" + time + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private TradeVO toVO(TradeRecord trade) {
        TradeVO vo = new TradeVO();
        vo.setId(trade.getId());
        vo.setTradeNo(trade.getTradeNo());
        vo.setItemId(trade.getItemId());
        vo.setItemTitle(trade.getItemTitle());
        vo.setBuyerId(trade.getBuyerId());
        vo.setSellerId(trade.getSellerId());
        User buyer = userMapper.selectById(trade.getBuyerId());
        if (buyer != null) vo.setBuyerName(displayName(buyer));
        User seller = userMapper.selectById(trade.getSellerId());
        if (seller != null) vo.setSellerName(displayName(seller));
        vo.setAmount(trade.getAmount());
        vo.setStatus(trade.getStatus());
        vo.setCreateTime(trade.getCreateTime());
        vo.setUpdateTime(trade.getUpdateTime());
        return vo;
    }

    private String displayName(User u) {
        return u.getRealName() != null ? u.getRealName() : u.getUsername();
    }
}
