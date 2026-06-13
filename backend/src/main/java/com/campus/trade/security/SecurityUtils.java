package com.campus.trade.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static UserPrincipal currentUser() {
        //获取到认证的用户信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //判断是否存在，然后看获取的信息是否是用户信息
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal p) {
            //返回用户信息
            return p;
        }
        throw new IllegalStateException("未登录");
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(currentUser().getRole());
    }

    /** 未登录时返回 null，供首页搜索等可选登录场景使用 */
    public static Long currentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal p) {
            return p.getId();
        }
        return null;
    }
}
