package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.BusinessException;
import com.campus.trade.common.PageResult;
import com.campus.trade.dto.ItemRequest;
import com.campus.trade.dto.ItemVO;
import com.campus.trade.entity.Item;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.ItemMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;
    @Autowired
    private CacheService cacheService;

    private static final String CACHE_PREFIX = "items:list:";

    @Transactional
    public ItemVO create(Long sellerId, ItemRequest req) {
        sensitiveWordFilter.check(req.getTitle(), req.getDescription());
        Item item = new Item();
        mapRequest(item, req);
        item.setSellerId(sellerId);
        item.setStatus("ON_SALE");
        item.setAuditStatus("PENDING");
        item.setViewCount(0);
        item.setDeleted(0);
        itemMapper.insert(item);
        cacheService.evictByPrefix(CACHE_PREFIX);
        return toVO(item, sellerId);
    }

    @Transactional
    public ItemVO update(Long userId, Long itemId, ItemRequest req, boolean isAdmin) {
        Item item = getActiveItem(itemId);
        if (!isAdmin && !item.getSellerId().equals(userId)) {
            throw new BusinessException("无权修改该物品");
        }
        sensitiveWordFilter.check(req.getTitle(), req.getDescription());
        mapRequest(item, req);
        if (req.getStatus() != null) item.setStatus(req.getStatus());
        itemMapper.updateById(item);
        cacheService.evictByPrefix(CACHE_PREFIX);
        return toVO(item, userId);
    }

    @Transactional
    public void delete(Long userId, Long itemId, boolean isAdmin) {
        Item item = getActiveItem(itemId);
        if (!isAdmin && !item.getSellerId().equals(userId)) {
            throw new BusinessException("无权删除该物品");
        }
        item.setStatus("OFF_SHELF");
        itemMapper.deleteById(itemId);
        cacheService.evictByPrefix(CACHE_PREFIX);
    }

    @Transactional
    public void adminOffShelf(Long itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        item.setStatus("OFF_SHELF");
        itemMapper.deleteById(itemId);
        cacheService.evictByPrefix(CACHE_PREFIX);
    }

    @Transactional
    public void audit(Long itemId, String auditStatus) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        item.setAuditStatus(auditStatus);
        if ("REJECTED".equals(auditStatus)) {
            item.setStatus("AUDIT_REJECT");
        } else if ("APPROVED".equals(auditStatus)) {
            item.setStatus("ON_SALE");
        }
        itemMapper.updateById(item);
        cacheService.evictByPrefix(CACHE_PREFIX);
    }

    @Transactional
    public ItemVO getDetail(Long id, Long viewerId) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("物品不存在或已下架");
        }
        item.setViewCount(item.getViewCount() + 1);
        itemMapper.updateById(item);
        return toVO(item, viewerId);
    }

    @SuppressWarnings("unchecked")
    public PageResult<ItemVO> search(String keyword, String category, BigDecimal minPrice,
                                     BigDecimal maxPrice, int page, int size, Long viewerId) {
        String cacheKey = CACHE_PREFIX + keyword + ":" + category + ":" + minPrice + ":" + maxPrice + ":" + page + ":" + size;
        if (viewerId == null) {
            PageResult<ItemVO> cached = cacheService.get(cacheKey, PageResult.class);
            if (cached != null) return cached;
        }
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<Item>()
                .orderByDesc(Item::getCreateTime);
        if (viewerId != null) {
            wrapper.and(w -> w
                    .eq(Item::getSellerId, viewerId).eq(Item::getStatus, "ON_SALE")
                    .or(o -> o.eq(Item::getAuditStatus, "APPROVED").eq(Item::getStatus, "ON_SALE")));
        } else {
            wrapper.eq(Item::getAuditStatus, "APPROVED").eq(Item::getStatus, "ON_SALE");
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Item::getTitle, keyword.trim())
                    .or().like(Item::getDescription, keyword.trim()));
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Item::getCategory, category);
        }
        if (minPrice != null) wrapper.ge(Item::getPrice, minPrice);
        if (maxPrice != null) wrapper.le(Item::getPrice, maxPrice);
        Page<Item> pageData = itemMapper.selectPage(new Page<>(page, size), wrapper);
        PageResult<ItemVO> result = new PageResult<>(
                pageData.getRecords().stream().map(i -> toVO(i, viewerId)).toList(),
                pageData.getTotal(), page, size);
        if (viewerId == null) {
            cacheService.set(cacheKey, result, 5);
        }
        return result;
    }

    public PageResult<ItemVO> myItems(Long sellerId, int page, int size) {
        Page<Item> pageData = itemMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Item>()
                        .eq(Item::getSellerId, sellerId)
                        .orderByDesc(Item::getCreateTime));
        return new PageResult<>(
                pageData.getRecords().stream().map(i -> toVO(i, sellerId)).toList(),
                pageData.getTotal(), page, size);
    }

    public List<ItemVO> hotItems(int limit, Long viewerId) {
        List<Item> list = itemMapper.selectList(new LambdaQueryWrapper<Item>()
                .eq(Item::getAuditStatus, "APPROVED")
                .eq(Item::getStatus, "ON_SALE")
                .orderByDesc(Item::getViewCount)
                .last("LIMIT " + limit));
        return list.stream().map(i -> toVO(i, viewerId)).toList();
    }

    public List<ItemVO> pendingAudit() {
        List<Item> list = itemMapper.selectList(new LambdaQueryWrapper<Item>()
                .eq(Item::getAuditStatus, "PENDING")
                .orderByDesc(Item::getCreateTime));
        return list.stream().map(i -> toVO(i, null)).toList();
    }

    private Item getActiveItem(Long id) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("物品不存在或已下架");
        }
        return item;
    }

    private void mapRequest(Item item, ItemRequest req) {
        item.setTitle(req.getTitle());
        item.setCategory(req.getCategory());
        item.setPrice(req.getPrice());
        item.setDescription(req.getDescription());
        if (req.getImageUrl() != null) item.setImageUrl(req.getImageUrl());
    }

    public ItemVO toVO(Item item, Long viewerId) {
        ItemVO vo = new ItemVO();
        vo.setId(item.getId());
        vo.setTitle(item.getTitle());
        vo.setCategory(item.getCategory());
        vo.setPrice(item.getPrice());
        vo.setDescription(item.getDescription());
        vo.setImageUrl(item.getImageUrl());
        vo.setSellerId(item.getSellerId());
        User seller = userMapper.selectById(item.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getRealName() != null && !seller.getRealName().isBlank()
                    ? seller.getRealName() : seller.getUsername());
        }
        vo.setMine(viewerId != null && viewerId.equals(item.getSellerId()));
        vo.setStatus(item.getStatus());
        vo.setAuditStatus(item.getAuditStatus());
        vo.setViewCount(item.getViewCount());
        vo.setCreateTime(item.getCreateTime());
        vo.setUpdateTime(item.getUpdateTime());
        return vo;
    }
}
