package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.ChatMessageVO;
import com.campus.trade.dto.ConversationVO;
import com.campus.trade.dto.SendMessageRequest;
import com.campus.trade.security.SecurityUtils;
import com.campus.trade.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/conversations")
    public Result<List<ConversationVO>> conversations() {
        return Result.ok(chatService.listConversations(SecurityUtils.currentUser().getId()));
    }

    @GetMapping("/messages")
    public Result<List<ChatMessageVO>> messages(
            @RequestParam Long peerId,
            @RequestParam(defaultValue = "100") int limit) {
        return Result.ok(chatService.listMessages(SecurityUtils.currentUser().getId(), peerId, limit));
    }

    @PostMapping("/messages")
    public Result<ChatMessageVO> send(@Valid @RequestBody SendMessageRequest req) {
        return Result.ok(chatService.send(
                SecurityUtils.currentUser().getId(), req.getPeerId(), req.getContent()));
    }

    @PutMapping("/messages/read")
    public Result<Void> markRead(@RequestBody Map<String, Long> body) {
        chatService.markRead(SecurityUtils.currentUser().getId(), body.get("peerId"));
        return Result.ok();
    }

    @GetMapping("/unread-count")
    public Result<Integer> unreadCount() {
        return Result.ok(chatService.unreadCount(SecurityUtils.currentUser().getId()));
    }
}
