package com.campus.trade.dto;

import java.time.LocalDateTime;

public class ConversationVO {
    private Long peerId;
    private String peerName;
    private String peerUsername;
    private String lastMessage;
    private LocalDateTime lastTime;
    private int unreadCount;

    public Long getPeerId() { return peerId; }
    public void setPeerId(Long peerId) { this.peerId = peerId; }
    public String getPeerName() { return peerName; }
    public void setPeerName(String peerName) { this.peerName = peerName; }
    public String getPeerUsername() { return peerUsername; }
    public void setPeerUsername(String peerUsername) { this.peerUsername = peerUsername; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public LocalDateTime getLastTime() { return lastTime; }
    public void setLastTime(LocalDateTime lastTime) { this.lastTime = lastTime; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
