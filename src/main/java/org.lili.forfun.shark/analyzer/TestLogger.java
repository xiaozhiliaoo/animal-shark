package org.lili.forfun.shark.analyzer;

import org.lili.forfun.shark.testcase.CommonDataProvider;
import org.lili.forfun.shark.util.ConfigUtil;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lili
 * @description 测试结果监听器:记录开始和结束
 * @create 2018-03-07 下午7:34
 * @since
 **/
public class TestLogger extends TestListenerAdapter {

    @Override
    public void onStart(ITestContext testContext) {
        System.out.println("Test in suite start: " + testContext.getName() + " and time is " + getCurrentTime());
    }

    @Override
    public void onFinish(ITestContext testContext) {

        System.out.println("Test in suite finish: " + testContext.getName() + " and time is " + getCurrentTime());
        //需要生成测试报告的时候才
        if (CommonDataProvider.isMakeReport()) {
            int finishWait = ConfigUtil.getNumber("finish_test_wait");
            System.out.println("Wait for log generate ... it will spend " + finishWait + "ms");
            try {
            /*
              测试结束前，等待finishTestWait秒
             */
                Thread.sleep(finishWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test method started: " + result.getName() + " and time is " + getCurrentTime());
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        System.out.println("Test method success: " + tr.getName() + " and time is " + getCurrentTime());
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        System.out.println("Test method failed: " + tr.getName() + " and time is " + getCurrentTime());

    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        System.out.println("Test method skipped: " + tr.getName() + " and time is " + getCurrentTime());
    }

    /**
     * 获取当时测试时间
     * @return
     */
    private String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
