package org.lili.forfun.shark.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class SharkInfo {

    @JSONField(name = "url")
    private String url;

    @JSONField(name = "input")
    private String input;

    @JSONField(name = "output")
    private List<String> output;

}
