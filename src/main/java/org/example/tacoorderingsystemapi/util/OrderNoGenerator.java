package org.example.tacoorderingsystemapi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单流水号生成工具
 */
public class OrderNoGenerator {

    // 定义时间格式化器
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 全新的订单号生成方法，作为一个独立的替代方案
     * 格式：时间戳(14位) + 随机数(4位) = 18位订单号
     * 示例：202604181745308821
     *
     * @return 18位唯一业务流水号
     */
    public static String generateNewTradeNo() {
        // 1. 获取当前时间的格式化字符串
        String timeStr = LocalDateTime.now().format(TIME_FORMATTER);

        // 2. 生成 1000 到 9999 之间的随机数 (ThreadLocalRandom 在多线程下性能更好且安全)
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 10000);

        // 3. 拼接并返回
        return timeStr + randomNum;
    }
}
