package org.lili.forfun.shark.tester;

import org.lili.forfun.shark.testcase.shark.SharkFunc;

/**
 * @author lili
 * @create 2018-04-12 下午6:36
 * @since
 **/
class SharkTester extends Tester {

    public SharkTester(String input, String output, boolean hasReport) {
        super(input, output, hasReport);
    }

    @Override
    void run() {
        SharkFunc sharkFunc = new SharkFunc();
        sharkFunc.setMakeReport(hasReport);
        sharkFunc.setPath(input, output);
        sharkFunc.testStart();
    }
}
