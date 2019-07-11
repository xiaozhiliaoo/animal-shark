package org.lili.forfun.shark.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public final class LogHitCheckUtil {


    private LogHitCheckUtil() {

    }

    /**
     * 通过日志接口查询是否有hit信息，暂时假设至少有一个，就认为有日志
     *
     * @param log
     * @return
     */
    public static boolean logHitsIsEmpty(String log) {
        boolean sharkHit = false;
        if (log == null) {
            return false;
        } else {
            JSONObject data = JSON.parseObject(log).getJSONObject("data");
            JSONObject shark = data.getJSONObject("shark");
            if (shark != null) {
                sharkHit = !shark.getJSONArray("hits").isEmpty();
            }
            return !isAtLeastOne(sharkHit);
        }
    }

    /**
     * 至少一个为true就返回true，全部false返回false
     *
     * @param values
     * @return
     */
    private static boolean isAtLeastOne(Boolean... values) {
        for (Boolean value : values) {
            if (value) {
                return true;
            }
        }
        return false;
    }
}
