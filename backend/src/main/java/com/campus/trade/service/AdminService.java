package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.dto.ItemVO;
import com.campus.trade.dto.StatsVO;
import com.campus.trade.entity.Item;
import com.campus.trade.entity.TradeRecord;
import com.campus.trade.mapper.ItemMapper;
import com.campus.trade.mapper.TradeRecordMapper;
import com.campus.trade.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private TradeRecordMapper tradeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CacheService cacheService;

    private static final String STATS_CACHE = "admin:stats";

    public StatsVO getStats() {
        StatsVO cached = cacheService.get(STATS_CACHE, StatsVO.class);
        if (cached != null) return cached;

        StatsVO stats = new StatsVO();
        //交易总量
        stats.setTotalTrades(tradeMapper.selectCount(null));
        stats.setCompletedTrades(tradeMapper.selectCount(new LambdaQueryWrapper<TradeRecord>()
                .eq(TradeRecord::getStatus, "COMPLETED")));
        stats.setPendingTrades(tradeMapper.selectCount(new LambdaQueryWrapper<TradeRecord>()
                .eq(TradeRecord::getStatus, "PENDING")));
        stats.setTotalUsers(userMapper.selectCount(null));
        stats.setTotalItems(itemMapper.selectCount(null));
        stats.setPendingAuditItems(itemMapper.selectCount(new LambdaQueryWrapper<Item>()
                .eq(Item::getAuditStatus, "PENDING")));
        stats.setHotItems(itemService.hotItems(10, null));
        cacheService.set(STATS_CACHE, stats, 10);
        return stats;
    }

    public List<ItemVO> pendingAuditItems() {
        return itemService.pendingAudit();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void refreshStatsCache() {
        cacheService.evict(STATS_CACHE);
        getStats();
    }
}
