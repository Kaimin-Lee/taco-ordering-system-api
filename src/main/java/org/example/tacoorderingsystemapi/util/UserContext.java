package org.example.tacoorderingsystemapi.util;

/**
 * 用户上下文工具类 (基于 ThreadLocal)
 * 用于在同一个 HTTP 请求线程中共享用户 ID
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_THREAD_LOCAL.set(userId);
    }

    public static Long getUserId() {
        return USER_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}