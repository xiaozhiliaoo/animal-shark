package org.lili.forfun.shark.parser;

/*
   @author lili
 * @description JSON解析器（JSON文件解析成对象）
 * @create 2018-04-12 下午3:05
 * @since
 */

/**
 * @param <T> 解析成的pojo对象
 */
interface Parser<T> {

    /**
     * 解析性能测试用例
     *
     * @param path 性能测试用例路径
     * @return
     */
    T parseBenchCases(String path);

    /**
     * 解析功能测试用例
     *
     * @param path 功能测试用例路径
     * @return
     */
    T parseFuncCases(String path);
}
