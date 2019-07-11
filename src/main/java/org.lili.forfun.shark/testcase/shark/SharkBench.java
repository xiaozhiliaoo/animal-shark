package org.lili.forfun.shark.testcase.shark;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.lili.forfun.shark.domain.SharkInfo;
import org.lili.forfun.shark.domain.SharkTestCases;
import org.lili.forfun.shark.parser.TestCaseHolder;
import org.lili.forfun.shark.util.HttpUtil;
import org.lili.forfun.shark.util.NumberUtil;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lili
 * @since
 **/
@Slf4j
@State(Scope.Thread)
public class SharkBench {

    @Param("")
    private String in;

    private SharkTestCases sharkTestCases;

    @Setup
    public void setup() {
        TestCaseHolder holder = new TestCaseHolder(in);
        sharkTestCases = holder.getSharkBenchCases();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(5)
    public void measureAvgTime() throws InterruptedException {
        testShark();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Threads(5)
    public void measureThroughput() throws InterruptedException {
        testShark();
    }

    private void testShark() {
        List<String> requestList = sharkTestCases.getSharkInfoList().stream().map(SharkInfo::getInput).collect(Collectors.toList());
        String randomListElement = NumberUtil.getRandomListElement(requestList);
        long startTime = System.currentTimeMillis();
        String result = HttpUtil.postPayload(JSONObject.parseObject(randomListElement).getString("url"), randomListElement);
        log.info("shark ==> bench response={}, cost time ={} ms", result, System.currentTimeMillis() - startTime);
    }
}
