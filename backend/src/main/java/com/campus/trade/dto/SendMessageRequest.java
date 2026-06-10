package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SendMessageRequest {
    @NotNull(message = "对方用户不能为空")
    private Long peerId;

    @NotBlank(message = "消息不能为空")
    @Size(max = 500, message = "消息最多500字")
    private String content;

    public Long getPeerId() { return peerId; }
    public void setPeerId(Long peerId) { this.peerId = peerId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
