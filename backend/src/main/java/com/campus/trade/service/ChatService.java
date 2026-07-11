package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.ChatMessageVO;
import com.campus.trade.dto.ConversationVO;
import com.campus.trade.entity.ChatMessage;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.ChatMessageMapper;
import com.campus.trade.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ChatService {
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public ChatMessageVO send(Long fromUserId, Long peerId, String content) {
        if (fromUserId.equals(peerId)) {
            throw new BusinessException("不能给自己发消息");
        }
        User peer = userMapper.selectById(peerId);
        if (peer == null || peer.getStatus() != 1) {
            throw new BusinessException("对方用户不存在或已禁用");
        }
        ChatMessage msg = new ChatMessage();
        msg.setFromUserId(fromUserId);
        msg.setToUserId(peerId);
        msg.setContent(content.trim());
        msg.setReadFlag(0);
        chatMessageMapper.insert(msg);
        return toVO(msg, fromUserId);
    }

    public List<ChatMessageVO> listMessages(Long userId, Long peerId, int limit) {
        //筛选消息
        List<ChatMessage> list = chatMessageMapper.selectList(new LambdaQueryWrapper<ChatMessage>()
                .and(w -> w
                        //发的是买家，收的是卖家
                        .and(w1 -> w1.eq(ChatMessage::getFromUserId, userId).eq(ChatMessage::getToUserId, peerId))
                        //或者发的是卖家，收的是买家
                        .or(w1 -> w1.eq(ChatMessage::getFromUserId, peerId).eq(ChatMessage::getToUserId, userId)))
                //然后按创建时间排序，增序排序
                .orderByAsc(ChatMessage::getCreateTime)
                //限制条数，此处agent添加的，防止查太多数据造成数据库压力
                .last("LIMIT " + Math.min(limit, 200)));
        //加载readflag状态
        markRead(userId, peerId);
        //转换为VO再发送给前端
        return list.stream().map(m -> toVO(m, userId)).toList();
    }

    public List<ConversationVO> listConversations(Long userId) {
        List<ChatMessage> all = chatMessageMapper.selectList(new LambdaQueryWrapper<ChatMessage>()
                .and(w -> w.eq(ChatMessage::getFromUserId, userId).or().eq(ChatMessage::getToUserId, userId))
                .orderByDesc(ChatMessage::getCreateTime));
        Map<Long, ChatMessage> lastByPeer = new LinkedHashMap<>();
        for (ChatMessage m : all) {
            Long peer = m.getFromUserId().equals(userId) ? m.getToUserId() : m.getFromUserId();
            lastByPeer.putIfAbsent(peer, m);
        }
        List<ConversationVO> result = new ArrayList<>();
        for (Map.Entry<Long, ChatMessage> e : lastByPeer.entrySet()) {
            Long peerId = e.getKey();
            ChatMessage last = e.getValue();
            User peer = userMapper.selectById(peerId);
            if (peer == null) continue;
            ConversationVO vo = new ConversationVO();
            vo.setPeerId(peerId);
            vo.setPeerName(displayName(peer));
            vo.setPeerUsername(peer.getUsername());
            vo.setLastMessage(last.getContent());
            vo.setLastTime(last.getCreateTime());
            Long unread = chatMessageMapper.selectCount(new LambdaQueryWrapper<ChatMessage>()
                    .eq(ChatMessage::getFromUserId, peerId)
                    .eq(ChatMessage::getToUserId, userId)
                    .eq(ChatMessage::getReadFlag, 0));
            vo.setUnreadCount(unread != null ? unread.intValue() : 0);
            result.add(vo);
        }
        return result;
    }

    @Transactional
    public void markRead(Long userId, Long peerId) {
        //更新逻辑写在了wrapper里面，不用对象更新
        chatMessageMapper.update(null, new LambdaUpdateWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, peerId)
                .eq(ChatMessage::getToUserId, userId)
                .eq(ChatMessage::getReadFlag, 0)
                .set(ChatMessage::getReadFlag, 1));
    }

    public int unreadCount(Long userId) {
        Long count = chatMessageMapper.selectCount(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getToUserId, userId)
                .eq(ChatMessage::getReadFlag, 0));
        return count != null ? count.intValue() : 0;
    }

    private ChatMessageVO toVO(ChatMessage m, Long viewerId) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(m.getId());
        vo.setFromUserId(m.getFromUserId());
        vo.setToUserId(m.getToUserId());
        vo.setContent(m.getContent());
        vo.setMine(m.getFromUserId().equals(viewerId));
        vo.setCreateTime(m.getCreateTime());
        return vo;
    }

    private String displayName(User user) {
        return user.getRealName() != null && !user.getRealName().isBlank()
                ? user.getRealName() : user.getUsername();
    }
}
