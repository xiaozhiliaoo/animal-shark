package org.lili.forfun.shark.testcase;

import org.lili.forfun.shark.analyzer.FolderManager;
import org.lili.forfun.shark.domain.SharkInfo;
import org.lili.forfun.shark.domain.SharkTestCases;
import org.lili.forfun.shark.parser.TestCaseHolder;
import org.testng.annotations.DataProvider;

import java.util.List;

/**
 * @author lili
 * @description 提供测试用例的数据
 * @create 2018-04-13 下午2:09
 * @since
 **/
public class CommonDataProvider {

    /**
     * 数据源的文件路径：来源（Client输入路径）
     */
    private static String input;

    /**
     * 测试结果输出路径
     */
    private static String output;

    /**
     * 是否输出测试报告,默认输出测试报告
     */
    private static boolean makeReport = true;


    /**
     * 获取测试结果输出路径
     *
     * @return
     */
    protected static String getOutput() {
        return FolderManager.getInstance().getOutputDir();
    }

    public static String getInput() {
        return input;
    }

    public static boolean isMakeReport() {
        return makeReport;
    }

    public static void setInput(String inputs) {
        input = inputs;
    }

    public static void setOutput(String outputs) {
        output = outputs;
    }

    public static void setMakeReport(boolean makeReports) {
        makeReport = makeReports;
    }

    /**
     * 设置输入输出路径
     *
     * @param inputs  输入文件路径/文件名
     * @param outputs 输出文件路径
     */
    public static void setPath(String inputs, String outputs) {
        input = inputs;
        output = outputs;
        FolderManager.getInstance().setInputFile(inputs);
        FolderManager.getInstance().setOutputDir(outputs);
    }

    /**
     * 生成shark测试数据
     *
     * @return
     */
    @DataProvider(name = "shark-data")
    public static Object[][] generateSharkFuncTestData() {
        Object[][] sharkInfo = null;
        SharkTestCases sharkTestCases = new TestCaseHolder(input).getSharkFuncCases();
        if (sharkTestCases != null) {
            String url = sharkTestCases.getUrl();
            List<SharkInfo> sharkInfoList = sharkTestCases.getSharkInfoList();
            sharkInfo = new Object[sharkInfoList.size()][];
            for (int i = 0; i < sharkInfo.length; i++) {
                SharkInfo prepareSharkInfo = sharkInfoList.get(i);
                prepareSharkInfo.setUrl(url);
                sharkInfo[i] = new Object[]{prepareSharkInfo};
            }
        }
        return sharkInfo;
    }
}
