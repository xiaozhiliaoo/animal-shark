package org.lili.forfun.shark.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


@Data
public class SharkTestCases {

    @JSONField(name = "testSuiteName")
    private String testSuiteName;

    @JSONField(name = "testCases")
    private List<SharkInfo> sharkInfoList;

    @JSONField(name = "url")
    private String url;

    @JSONField(name = "optional")
    private String optional;

    public static SharkTestCases generate(String text) {
        if (StringUtils.isNotEmpty(text)) {
            return JSON.parseObject(text, SharkTestCases.class, Feature.OrderedField);
        }
        return null;
    }
}
