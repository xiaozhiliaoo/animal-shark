package org.lili.forfun.shark.tester;

/**
 * @author lili
 * @description 测试类
 * @since
 **/
public abstract class Tester {

    protected String input;
    protected String output;
    protected boolean hasReport;

    public Tester(String input, String output, boolean hasReport) {
        this.input = input;
        this.output = output;
        this.hasReport = hasReport;
    }

    /**
     * 运行测试用例
     */
    abstract void run();
}

