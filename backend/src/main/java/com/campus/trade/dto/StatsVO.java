package com.campus.trade.dto;

import java.util.List;

public class StatsVO {
    //交易总数
    private long totalTrades;
    //已完成交易数
    private long completedTrades;
    //待处理交易数
    private long pendingTrades;
    //用户总数
    private long totalUsers;
    //物品总数
    private long totalItems;
    //待审核物品总数
    private long pendingAuditItems;
    //热门商品
    private List<ItemVO> hotItems;

    public long getTotalTrades() { return totalTrades; }
    public void setTotalTrades(long totalTrades) { this.totalTrades = totalTrades; }
    public long getCompletedTrades() { return completedTrades; }
    public void setCompletedTrades(long completedTrades) { this.completedTrades = completedTrades; }
    public long getPendingTrades() { return pendingTrades; }
    public void setPendingTrades(long pendingTrades) { this.pendingTrades = pendingTrades; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
    public long getPendingAuditItems() { return pendingAuditItems; }
    public void setPendingAuditItems(long pendingAuditItems) { this.pendingAuditItems = pendingAuditItems; }
    public List<ItemVO> getHotItems() { return hotItems; }
    public void setHotItems(List<ItemVO> hotItems) { this.hotItems = hotItems; }
}
