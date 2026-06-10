package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.BusinessException;
import com.campus.trade.common.PageResult;
import com.campus.trade.dto.UserBriefVO;
import com.campus.trade.dto.UserProfileRequest;
import com.campus.trade.dto.UserVO;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.util.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthService authService;

    public UserVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return authService.toVO(user);
    }

    public UserBriefVO getBrief(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException("用户不存在");
        }
        UserBriefVO vo = new UserBriefVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        return vo;
    }

    @Transactional
    public UserVO updateProfile(Long userId, UserProfileRequest req) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (req.getRealName() != null) user.setRealName(req.getRealName());
        if (req.getPhone() != null) user.setPhone(CryptoUtil.encrypt(req.getPhone()));
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        userMapper.updateById(user);
        return authService.toVO(user);
    }

    public PageResult<UserVO> listUsers(int page, int size) {
        Page<User> p = userMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime));
        return new PageResult<>(
                p.getRecords().stream().map(authService::toVO).toList(),
                p.getTotal(), page, size);
    }

    @Transactional
    public void updateStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能禁用管理员");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能删除管理员");
        }
        userMapper.deleteById(userId);
    }
}
