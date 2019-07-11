package org.lili.forfun.shark.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class LogUtil {

    private LogUtil() {

    }


    /**
     * 查询日志
     *
     * @param requestId
     * @return
     */
    public static String searchLogByReqId(String requestId) {
        if (Strings.isNullOrEmpty(requestId)) {
            return null;
        }
        Map<String, String> request = new HashMap<>(1);
        request.put("query", requestId);
        String logUrl = ConfigUtil.getString("log_url");
        return HttpUtil.get(logUrl, request);
    }


    /**
     * 日志格式化：Html中显示JSON
     *
     * @param log
     * @return
     */
    public static String formatLog(String log) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object json = mapper.readValue(log, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
