package org.lili.forfun.shark.parser;


import org.lili.forfun.shark.domain.SharkTestCases;

public final class TestCaseHolder {

    /**
     * 测试用例来源路径
     */
    private String fileInputPath;

    public TestCaseHolder(String fileInputPath) {
        this.fileInputPath = fileInputPath;
    }

    public SharkTestCases getSharkFuncCases() {
        return new SharkParser().parseFuncCases(fileInputPath);
    }

    public SharkTestCases getSharkBenchCases() {
        return new SharkParser().parseBenchCases(fileInputPath);
    }
    
}
