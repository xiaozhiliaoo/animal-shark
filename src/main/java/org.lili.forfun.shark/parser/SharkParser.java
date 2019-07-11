package org.lili.forfun.shark.parser;


import com.google.common.base.Strings;
import org.lili.forfun.shark.domain.SharkTestCases;
import org.lili.forfun.shark.util.ConfigUtil;
import org.lili.forfun.shark.util.JsonUtil;

/**
 * @author lili
 * @description 解析json文件到pojo
 * @since
 **/
class SharkParser implements Parser<SharkTestCases> {

    @Override
    public SharkTestCases parseBenchCases(String path) {
        return SharkTestCases.generate(
                Strings.isNullOrEmpty(path) ? JsonUtil.read(ConfigUtil.getString("shark_bench")) : JsonUtil.read(path));
    }

    @Override
    public SharkTestCases parseFuncCases(String path) {
        return SharkTestCases.generate(
                Strings.isNullOrEmpty(path) ? JsonUtil.read(ConfigUtil.getString("shark_func")) : JsonUtil.read(path));
    }
}
