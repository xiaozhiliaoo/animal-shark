package org.lili.forfun.shark.testcase.shark;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.lili.forfun.shark.analyzer.TestLogger;
import org.lili.forfun.shark.analyzer.TestReport;
import org.lili.forfun.shark.domain.SharkInfo;
import org.lili.forfun.shark.testcase.CommonDataProvider;
import org.lili.forfun.shark.testcase.validator.Validator;
import org.lili.forfun.shark.util.HttpUtil;
import org.testng.ITest;
import org.testng.TestNG;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author lili
 * @create 2018-03-19 下午3:07
 * @since
 **/
@Slf4j
public class SharkFunc extends CommonDataProvider implements ITest {

    private ThreadLocal<String> testName = new ThreadLocal<>();

    @BeforeMethod
    public void init(Object[] testArgs) {
        testName.set(((SharkInfo) testArgs[0]).getInput());
    }

    @Test(dataProvider = "shark-data")
    public void testShark(SharkInfo sharkInfo) {
        String request = sharkInfo.getInput();
        List<String> expectedOutput = sharkInfo.getOutput();
        String url = sharkInfo.getUrl();
        String payload = JSON.toJSONString(request);
        String result = HttpUtil.postPayload(url, payload);
        new Validator().checkResult(result, expectedOutput);
    }

    public void testStart() {
        TestNG tng = new TestNG();
        //不打开默认的监听器
        tng.setUseDefaultListeners(false);
        //设置输出目录
        tng.setOutputDirectory(getOutput());
        tng.setTestClasses(new Class[]{SharkFunc.class});
        if (isMakeReport()) {
            tng.setListenerClasses(Arrays.asList(TestLogger.class, TestReport.class));
        } else {
            tng.setListenerClasses(Arrays.asList(TestLogger.class));
        }
        tng.run();
    }

    @Override
    public String getTestName() {
        return testName.get();
    }

}


