package org.lili.forfun.shark.util;

import java.util.Properties;

public class ConfigUtil {

    private ConfigUtil() {

    }

    /**
     * 配置选项
     */
    private static final Properties CONFIG_PROPS = PropsUtil.loadProps("config.properties");

    /**
     * @param key
     * @return
     */
    public static String getString(String key) {
        return PropsUtil.getString(CONFIG_PROPS, key);
    }

    /**
     *
     * @param key
     * @return
     */
    public static int getNumber(String key) {
        return PropsUtil.getNumber(CONFIG_PROPS, key);
    }

}
