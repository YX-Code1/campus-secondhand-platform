package com.campus.trade.dto;

import java.time.LocalDateTime;

public class ChatMessageVO {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String content;
    private boolean mine;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isMine() { return mine; }
    public void setMine(boolean mine) { this.mine = mine; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
