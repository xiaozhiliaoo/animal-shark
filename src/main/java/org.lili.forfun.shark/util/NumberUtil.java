package org.lili.forfun.shark.util;


import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.UUID;

public final class NumberUtil {

    private NumberUtil() {
    }

    /**
     * 生成requestId
     * @return
     */
    public static String getRequestId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成sessionId
     * @return
     */
    public static String getSessionId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 从List中随机选一个元素：用于性能测试
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T getRandomListElement(List<T> list) {
        return list.get(RandomUtils.nextInt(0, list.size()));
    }
}
