package com.campus.trade.dto;

import java.util.List;

public class StatsVO {
    private long totalTrades;
    private long completedTrades;
    private long pendingTrades;
    private long totalUsers;
    private long totalItems;
    private long pendingAuditItems;
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
