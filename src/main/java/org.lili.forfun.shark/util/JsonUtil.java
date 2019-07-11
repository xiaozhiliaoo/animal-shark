package org.lili.forfun.shark.util;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.Feature;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author lili
 * @description 读取测试用例JSON文件
 * @create 2018-02-27 下午4:18
 * @since
 **/
public final class JsonUtil {

    private JsonUtil() {
    }

    /**
     * 记住关闭流!!!!!!!
     *
     * @param file
     * @return
     */
    public static String read(String file) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (inputStream == null) {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        InputStreamReader reader = new InputStreamReader(inputStream);
        //OrderedField禁止重排序，导致
        return new JSONReader(reader, Feature.OrderedField).readObject().toString();
    }
}
