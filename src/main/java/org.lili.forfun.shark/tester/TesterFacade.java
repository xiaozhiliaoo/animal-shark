package org.lili.forfun.shark.tester;

import org.lili.forfun.shark.analyzer.FolderManager;
import org.lili.forfun.shark.util.MailUtil;

import java.io.File;


public class TesterFacade {

    private static final int ONLY_ONE_FILE = 1;
    /**
     * 选择平台
     */
    private String platform;

    /**
     * 输入文件名字
     */
    private String input;

    /**
     * 输出路径
     */
    private String output;

    /**
     * 是否输出测试报告
     */
    private boolean hasReport;
    /**
     * Email
     */
    private String email;

    public TesterFacade(String platform, String input, String output, String email, boolean hasReport) {
        this.platform = platform;
        this.input = input;
        this.output = output;
        this.email = email;
        this.hasReport = hasReport;
    }

    /**
     * 运行方法，通过client运行run
     * 判断每个命令具体执行哪些操作
     */
    public void run() {

        TesterFactory factory = new TesterFactoryImpl();
        Tester tester = factory.makeTest(platform, input, output, hasReport);
        if (tester != null) {
            tester.run();
            /**
             * 测试完成之后发送email
             */
            if (email != null) {
                File[] funcReport = FolderManager.getInstance().getFuncReport();
                if (funcReport.length == ONLY_ONE_FILE) {
                    System.out.println("Send mail now, Please wait ...");
                    MailUtil.sendMail(email, "测试报告-" + platform, funcReport);
                }
            }
        }
    }
}
