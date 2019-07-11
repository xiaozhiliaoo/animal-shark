package org.lili.forfun.shark.tester;

/**
 * @author lili
 * @description ${DESCRIPTION}
 * @since
 **/
public class TesterFactoryImpl implements TesterFactory {

    @Override
    public Tester makeTest(String platform, String input, String output, boolean hasReport) {
        switch (platform) {
            case "shark":
                return new SharkTester(input, output, hasReport);
            default:
                System.out.println("please choose -p (platform:shark)");
                return null;
        }
    }
}
