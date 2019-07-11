package org.lili.forfun.shark;

import org.apache.commons.cli.*;
import org.lili.forfun.shark.tester.TesterFacade;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final String HELP = "please input (-bench -h | -func -h) for help message";

    public static void main(String[] args) throws IOException, RunnerException {
        try {
            List<String> input = Arrays.asList(args);
            //这里不应该删除第一个，应该删除包含-func或者-bench的，用户不一定记得顺序
            String[] args1 = rmFirstArgs(args);
            if (input.contains("-func")) {
                //功能测试入口
                FuncCommand command = new FuncCommand(args1);
                TesterFacade tester = command.create();
                tester.run();
            } else if (args.length >= 2 && input.contains("-bench")) {
                //性能测试入口(直接由jmh main方法运行)
                org.openjdk.jmh.Main.main(args1);
            } else {
                System.out.println(HELP);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            System.exit(0);
        }
    }


    /**
     * 删除第一位(-func,-bench)
     *
     * @param args
     * @return
     */
    private static String[] rmFirstArgs(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }


    /**
     * 功能测试入口类
     */
    private static class FuncCommand {

        private CommandLine cmdParams = null;
        private Options options = new Options();
        private String[] args = null;
        private static final String PLATFORM = "p";
        private static final String INPUT = "i";
        private static final String OUTPUT = "o";
        private static final String EMAIL = "e";
        private static final String REPORT = "r";
        private static final String HELP = "h";

        public FuncCommand(String[] args) {
            this.args = args;

            Option input = new Option("i", "input", true, "测试用例输入文件(json),默认在config.properties配置");
            input.setArgName("string");
            Option output = new Option("o", "output", true, "测试结果输出路径(默认在shark-result中)");
            output.setArgName("string");
            Option email = new Option("e", "email", true, "发送邮箱地址(默认不发送)");
            email.setArgName("string");
            Option platform = new Option("p", "platform", true, "测试平台选择(可选：shark)");
            platform.setArgName("string");
            options.addOption(platform);
            options.addOption(input);
            options.addOption(output);
            options.addOption(email);
            options.addOption("r", "report", false, "是否输出测试报告(默认不输出)");
            options.addOption("h", "help", false, "输出帮助消息");
        }

        /**
         * 创建测试入口类
         *
         * @return
         */
        public TesterFacade create() {

            CommandLineParser parser = new DefaultParser();

            try {
                cmdParams = parser.parse(options, args);
            } catch (ParseException exp) {
                System.err.println("Parsing failed.  Reason: " + exp.getMessage());
                exp.printStackTrace();
            }

            if (args.length == 0) {
                System.out.println("please input (-func -h) for help message");
                System.exit(0);
            }

        /*
          输出帮助信息
         */
            if (cmdParams.hasOption(HELP)) {
                HelpFormatter formatter = new HelpFormatter();
                //禁止排序
                formatter.setOptionComparator(null);
                formatter.setLeftPadding(4);
                formatter.printHelp("java -jar xxx.jar [options] or sh cli.sh -func [options]", options);
                System.exit(0);
            }

            String platform = "";
            String input = "";
            String output = "";
            String email = null;
            String hasReport = "false";

            if (cmdParams.hasOption(PLATFORM)) {
                platform = cmdParams.getOptionValue("platform");
            }

            if (cmdParams.hasOption(INPUT)) {
                input = cmdParams.getOptionValue("input");
            }

            if (cmdParams.hasOption(OUTPUT)) {
                output = cmdParams.getOptionValue("output");
            }

            if (cmdParams.hasOption(EMAIL)) {
                email = cmdParams.getOptionValue("email");
            }

            if (cmdParams.hasOption(REPORT)) {
                hasReport = "true";
            }

        /*
          统一功能测试入口:平台，输入文件，输出路径，邮件
         */
            return new TesterFacade(platform, input, output, email, Boolean.valueOf(hasReport));
        }
    }
}












