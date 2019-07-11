package org.lili.forfun.shark.util;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Slf4j
public final class PropsUtil {

    private PropsUtil() {
    }

    /**
     * 从路径加载属性文件
     *
     * @param propsPath 路径
     * @return
     */
    public static Properties loadProps(String propsPath) {

        Properties props = new Properties();
        if (propsPath.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String suffix = ".properties";
        if (propsPath.lastIndexOf(suffix) == -1) {
            propsPath += suffix;
        }
        try (InputStream is = PropsUtil.class.getClassLoader().getResourceAsStream(propsPath)) {
            if (is != null) {
                //UTF-8解决中文乱码问题
                props.load(new InputStreamReader(is, "UTF-8"));
            }
            return props;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取字符型属性
     */
    public static String getString(Properties props, String key) {
        return getString(props, key, "");
    }


    /**
     * 获取字符型属性（带有默认值）
     */
    public static String getString(Properties props, String key, String defalutValue) {
        String value = defalutValue;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    /**
     * 获取数值型属性
     *
     * @param props
     * @param key
     * @return
     */
    public static int getNumber(Properties props, String key) {
        return Integer.parseInt(getString(props, key, ""));
    }

    /**
     * 获取数值型属性（带有默认值）
     *
     * @param props
     * @param key
     * @param defalutValue
     * @return
     */
    public static int getNumber(Properties props, String key, int defalutValue) {
        return Integer.parseInt(getString(props, key, String.valueOf(defalutValue)));
    }

    /**
     * 属性文件的值映射到Map
     *
     * @param propsPath
     * @return
     */
    public static Map<String, String> loadPropsToMap(String propsPath) {
        Map<String, String> map = new HashMap<>();
        Properties props = loadProps(propsPath);
        for (String key : props.stringPropertyNames()) {
            map.put(key, props.getProperty(key));
        }
        return map;
    }
}
