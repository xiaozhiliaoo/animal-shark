package org.lili.forfun.shark.analyzer;


import org.lili.forfun.shark.domain.SharkInfo;
import org.lili.forfun.shark.util.ConfigUtil;
import org.lili.forfun.shark.util.LogUtil;
import org.testng.*;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.lili.forfun.shark.util.DateUtil.getNow;
import static org.lili.forfun.shark.util.DateUtil.stampToDate;
import static org.lili.forfun.shark.util.LogHitCheckUtil.logHitsIsEmpty;
import static org.lili.forfun.shark.util.LogUtil.formatLog;


/**
 * @author lili
 * @description 生成对话HTML测试报告，并记录错误日志
 * @create 2018-03-11 下午3:20
 * @since
 **/
public class TestReport implements IReporter {

    private static final Logger LOG = Logger.getLogger(TestReport.class);

    private PrintWriter writer;

    private final List<SuiteResult> suiteResults = Lists.newArrayList();

    // Reusable buffer
    private final StringBuilder buffer = new StringBuilder();

    private String fileName = "report" + getNow() + ".html";

    private static final String JVM_ARG = "emailable.report2.name";


    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        try {
            writer = createWriter(outputDirectory);
        } catch (IOException e) {
            LOG.error("Unable to create output file", e);
            return;
        }
        for (ISuite suite : suites) {
            suiteResults.add(new SuiteResult(suite));
        }

        writeDocumentStart();
        writeHead();
        writeBody();
        writeDocumentEnd();
        writer.close();
    }

    protected PrintWriter createWriter(String outdir) throws IOException {
        new File(outdir).mkdirs();
        String jvmArg = System.getProperty(JVM_ARG);
        if (jvmArg != null && !jvmArg.trim().isEmpty()) {
            fileName = jvmArg;
        }
        return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, fileName))));
    }

    /**
     * 生成文档头
     */
    protected void writeDocumentStart() {
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        writer.print("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    }

    /**
     * 生成header
     */
    protected void writeHead() {
        writer.print("<head>");
        writer.print("<title>TestNG Report</title>");
        writeStylesheet();
        writer.print("</head>");
    }

    /**
     * 生成css样式表
     */
    protected void writeStylesheet() {
        writer.print("<style type=\"text/css\">");
        writer.print("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
        writer.print("th,td {border:1px solid #009;padding:.25em .5em}");
        writer.print("th {vertical-align:bottom}");
        writer.print("td {vertical-align:top}");
        writer.print("table a {font-weight:bold}");
        writer.print(".stripe td {background-color: #E6EBF9}");
        writer.print(".num {text-align:right}");
        writer.print(".passedodd td {background-color: #3F3}");
        writer.print(".passedeven td {background-color: #0A0}");
        writer.print(".skippedodd td {background-color: #DDD}");
        writer.print(".skippedeven td {background-color: #CCC}");
        writer.print(".failedodd td,.attn {background-color: #F33}");
        writer.print(".failedeven td,.stripe .attn {background-color: #D00}");
        writer.print(".stacktrace {white-space:pre-wrap;font-family:monospace}");
        writer.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
        writer.print("</style>");
    }

    /**
     * 生成html的正文body
     */
    protected void writeBody() {
        writer.print("<body>");
        //测试套件总结
        writeSuiteSummary();
        //测试脚本总结
        writeScenarioSummary();
        //测试脚本细节
        writeScenarioDetails();
        writer.print("</body>");
    }

    /**
     * 生成文档尾标签
     */
    protected void writeDocumentEnd() {
        writer.print("</html>");
    }

    protected void writeSuiteSummary() {
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        NumberFormat decimalFormat = NumberFormat.getNumberInstance();

        int totalPassedTests = 0;
        int totalSkippedTests = 0;
        int totalFailedTests = 0;
        long totalDuration = 0;

        writer.print("<table>");
        writer.print("<tr>");
        writer.print("<th>Test</th>");
        writer.print("<th># Passed</th>");
        writer.print("<th># Skipped</th>");
        writer.print("<th># Failed</th>");
        writer.print("<th>Time (ms)</th>");
        writer.print("<th>Included Groups</th>");
        writer.print("<th>Excluded Groups</th>");
        writer.print("</tr>");

        int testIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            writer.print("<tr><th colspan=\"7\">");
            writer.print(Utils.escapeHtml(suiteResult.getSuiteName()));
            writer.print("</th></tr>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                int passedTests = testResult.getPassedTestCount();
                int skippedTests = testResult.getSkippedTestCount();
                int failedTests = testResult.getFailedTestCount();
                long duration = testResult.getDuration();

                writer.print("<tr");
                if ((testIndex % 2) == 1) {
                    writer.print(" class=\"stripe\"");
                }
                writer.print(">");

                buffer.setLength(0);
                writeTableData(buffer.append("<a href=\"#t").append(testIndex)
                        .append("\">")
                        .append(Utils.escapeHtml(testResult.getTestName()))
                        .append("</a>").toString());
                writeTableData(integerFormat.format(passedTests), "num");
                writeTableData(integerFormat.format(skippedTests),
                        (skippedTests > 0 ? "num attn" : "num"));
                writeTableData(integerFormat.format(failedTests),
                        (failedTests > 0 ? "num attn" : "num"));
                writeTableData(decimalFormat.format(duration), "num");
                writeTableData(testResult.getIncludedGroups());
                writeTableData(testResult.getExcludedGroups());

                writer.print("</tr>");

                totalPassedTests += passedTests;
                totalSkippedTests += skippedTests;
                totalFailedTests += failedTests;
                totalDuration += duration;

                testIndex++;
            }
        }

        // Print totals if there was more than one test
        if (testIndex > 1) {
            writer.print("<tr>");
            writer.print("<th>Total</th>");
            writeTableHeader(integerFormat.format(totalPassedTests), "num");
            writeTableHeader(integerFormat.format(totalSkippedTests),
                    (totalSkippedTests > 0 ? "num attn" : "num"));
            writeTableHeader(integerFormat.format(totalFailedTests),
                    (totalFailedTests > 0 ? "num attn" : "num"));
            writeTableHeader(decimalFormat.format(totalDuration), "num");
            writer.print("<th colspan=\"2\"></th>");
            writer.print("</tr>");
        }

        writer.print("</table>");
    }

    /**
     * Writes a summary of all the test scenarios.
     */
    protected void writeScenarioSummary() {
        writer.print("<table id='summary'>");
        writer.print("<thead>");
        writer.print("<tr>");
        writer.print("<th>Class.Method</th>");
        writer.print("<th>TestCase</th>");
        writer.print("<th>Expected Result Include</th>");
        writer.print("<th>Start</th>");
        writer.print("<th>Time (ms)</th>");
        writer.print("</tr>");
        writer.print("</thead>");

        int testIndex = 0;
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            writer.print("<tbody><tr><th colspan=\"5\">");
            writer.print("</th></tr></tbody>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                writer.print("<tbody id=\"t");
                writer.print(testIndex);
                writer.print("\">");

                String testName = "";
                //测试配置失败的例子
                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; failed (configuration methods)",
                        testResult.getFailedConfigurationResults(), "failed",
                        scenarioIndex);
                // 测试失败的例子
                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; failed", testResult.getFailedTestResults(),
                        "failed", scenarioIndex);
                // 测试配置忽略的例子
                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; skipped (configuration methods)",
                        testResult.getSkippedConfigurationResults(), "skipped",
                        scenarioIndex);
                //测试忽略例子
                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; skipped",
                        testResult.getSkippedTestResults(), "skipped",
                        scenarioIndex);
                //测试通过的例子
                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; passed", testResult.getPassedTestResults(),
                        "passed", scenarioIndex);

                writer.print("</tbody>");

                testIndex++;
            }
        }
        writer.print("</table>");
    }

    /**
     * Writes the scenario summary for the results of a given state for a single
     * test.
     */
    private int writeScenarioSummary(String description,
                                     List<ClassResult> classResults, String cssClassPrefix,
                                     int startingScenarioIndex) {
        int scenarioCount = 0;
        if (!classResults.isEmpty()) {
            writer.print("<tr><th colspan=\"5\">");
            writer.print(description);
            writer.print("</th></tr>");

            int scenarioIndex = startingScenarioIndex;
            int classIndex = 0;
            String methodName = "";
            for (ClassResult classResult : classResults) {
                String cssClass = cssClassPrefix + ((classIndex % 2) == 0 ? "even" : "odd");

                buffer.setLength(0);

                int scenariosPerClass = 0;
                int methodIndex = 0;

                for (MethodResult methodResult : classResult.getMethodResults()) {
                    List<ITestResult> results = methodResult.getResults();
                    //测试耗时
                    List<Long> collectTime = results.stream().map(x -> (x.getEndMillis() - x.getStartMillis())).collect(Collectors.toList());
                    //用例名字
                    List<String> nameList = results.stream().map(ITestResult::getName).collect(Collectors.toList());
                    //开始时间
                    List<Long> startTime = results.stream().map(ITestResult::getStartMillis).collect(Collectors.toList());
                    //输入参数
                    List<Object[]> paramList = results.stream().map(ITestResult::getParameters).collect(Collectors.toList());

                    int resultsCount = results.size();
                    assert resultsCount > 0;

                    //第一个结果
                    ITestResult firstResult = results.iterator().next();
                    //第一个方法名
                    methodName = Utils.escapeHtml(firstResult.getMethod().getMethodName());

                    for (int i = 0; i < resultsCount; i++) {

                        Object[] objects = paramList.get(i);
                        if (objects.length == 0) {
                            continue;
                        }
                        Object parameter = objects[0];
                        String expectedResult = "";
                        if (parameter instanceof SharkInfo) {
                            expectedResult = ((SharkInfo) parameter).getOutput().toString();
                        }
                        buffer.append("<tr class=\"").append(cssClass)
                                .append("\">").append("<td><a href=\"#m").append(scenarioIndex)
                                .append("\">").append(nameList.get(i))
                                .append("</a></td>")
                                .append("<td>").append(expectedResult).append("</td>")
                                .append("<td>").append(stampToDate(startTime.get(i)))
                                .append("</td>").append("<td>")
                                .append(collectTime.get(i)).append("</td></tr>");
                        scenarioIndex++;
                    }

                    scenariosPerClass += resultsCount;
                    methodIndex++;
                }

                // Write the test results for the class
                writer.print("<tr class=\"");
                writer.print(cssClass);
                writer.print("\">");
                writer.print("<td rowspan=\"");
                writer.print(scenariosPerClass + 1);
                writer.print("\">");
                String className = classResult.getClassName();
                //
                writer.print(Utils.escapeHtml(className.substring(className.lastIndexOf(".") + 1)) + "." + methodName);
                writer.print("</td>");
                writer.print(buffer);
                classIndex++;
            }
            scenarioCount = scenarioIndex - startingScenarioIndex;
        }
        return scenarioCount;
    }

    /**
     * Writes the details for all test scenarios.
     */
    protected void writeScenarioDetails() {
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            for (TestResult testResult : suiteResult.getTestResults()) {
                writer.print("<h2>");
                writer.print(Utils.escapeHtml(testResult.getTestName()));
                writer.print("</h2>");
                //测试失败配置
                scenarioIndex += writeScenarioDetails(
                        testResult.getFailedConfigurationResults(),
                        scenarioIndex);
                //测试失败结果
                scenarioIndex += writeScenarioDetails(
                        testResult.getFailedTestResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(
                        testResult.getSkippedConfigurationResults(),
                        scenarioIndex);
                scenarioIndex += writeScenarioDetails(
                        testResult.getSkippedTestResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(
                        testResult.getPassedTestResults(), scenarioIndex);
            }
        }
    }

    /**
     * Writes the scenario details for the results of a given state for a single
     * test.
     */
    private int writeScenarioDetails(List<ClassResult> classResults,
                                     int startingScenarioIndex) {
        int scenarioIndex = startingScenarioIndex;
        for (ClassResult classResult : classResults) {
            String className = classResult.getClassName();
            for (MethodResult methodResult : classResult.getMethodResults()) {
                List<ITestResult> results = methodResult.getResults();
                assert !results.isEmpty();

                String label = Utils.escapeHtml(className + "#"
                        + results.iterator().next().getMethod()
                        .getMethodName());
                for (ITestResult result : results) {
                    writeScenario(scenarioIndex, label, result);
                    scenarioIndex++;
                }
            }
        }

        return scenarioIndex - startingScenarioIndex;
    }

    /**
     * Writes the details for an individual test scenario.
     */
    private void writeScenario(int scenarioIndex, String label,
                               ITestResult result) {
        writer.print("<h3 id=\"m");
        writer.print(scenarioIndex);
        writer.print("\">");
        writer.print(label);
        writer.print("</h3>");

        writer.print("<table class=\"result\">");

        // Write test parameters (if any)
        Object[] parameters = result.getParameters();
        int parameterCount = (parameters == null ? 0 : parameters.length);
        if (parameterCount > 0) {
            writer.print("<tr class=\"param\">");
            for (int i = 1; i <= parameterCount; i++) {
                writer.print("<th>请求参数 Parameter");
                writer.print(i);
                writer.print("</th>");
            }
            writer.print("</tr><tr class=\"param stripe\">");
            for (Object parameter : parameters) {
                String query = "";
                if (parameter instanceof SharkInfo) {
                    query = ((SharkInfo) parameter).getInput();
                }
                writer.print("<td>");
                writer.print(Utils.escapeHtml(Utils.toString(query)));
                writer.print("</td>");
            }
            writer.print("</tr>");
        }

        // Write reporter messages (if any)
        List<String> reporterMessages = Reporter.getOutput(result);
        if (!reporterMessages.isEmpty()) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">Messages</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">");
            writeReporterMessages(reporterMessages);
            writer.print("</td></tr>");
        }

        // Write exception (if any)  如果发生错误
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            //这里扩展性不好
            String requestId = "";
            if (parameterCount != 0) {
                Object paramter = parameters[0];
                if (paramter instanceof SharkInfo) {
                    requestId = ((SharkInfo) paramter).getInput();
                }
                writer.print("<tr> RequestId 【 " + requestId + " 】<th");
            }

            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">");

            //打印错误堆栈信息
            writer.print((result.getStatus() == ITestResult.SUCCESS ? "Expected Exception"
                    : "异常信息 Exception"));
            writer.print("</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">");
            //如果有错误，打印错误堆栈信息
            writeStackTrace(throwable);
            writer.print("</td></tr>");

            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">");

            //打印es日志信息
            writer.print((result.getStatus() == ITestResult.SUCCESS ? "Expected Exception"
                    : "日志输出 Log"));
            writer.print("</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">");
            //如果有错误，则打印es的日志
            writeLogMessage(requestId);
            writer.print("</td></tr>");
        }

        writer.print("</table>");
        writer.print("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");
    }

    protected void writeReporterMessages(List<String> reporterMessages) {
        writer.print("<div class=\"messages\">");
        Iterator<String> iterator = reporterMessages.iterator();
        assert iterator.hasNext();
        if (Reporter.getEscapeHtml()) {
            writer.print(Utils.escapeHtml(iterator.next()));
        } else {
            writer.print(iterator.next());
        }
        while (iterator.hasNext()) {
            writer.print("<br/>");
            if (Reporter.getEscapeHtml()) {
                writer.print(Utils.escapeHtml(iterator.next()));
            } else {
                writer.print(iterator.next());
            }
        }
        writer.print("</div>");
    }

    /**
     * 打印错误堆栈信息
     *
     * @param throwable
     */
    protected void writeStackTrace(Throwable throwable) {
        writer.print("<div class=\"stacktrace\">");
        writer.print(Utils.shortStackTrace(throwable, true));
        writer.print("</div>");
    }

    /**
     * 如果组件请求出现错误，则查询日志
     *
     * @param requestId
     */
    protected void writeLogMessage(String requestId) {

        int searchLogWait = ConfigUtil.getNumber("search_log_wait");
        int searchLogTimeout = ConfigUtil.getNumber("search_log_timeout");

        writer.print("<div class=\"stacktrace\"><pre>");
        System.out.println("Wait for log search , requestId " + requestId + " , will spend " + searchLogWait + " ms or more ...");
        try {
            //每次输出错误日志先等待
            Thread.sleep(searchLogWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        String log;
        try {
            do {
                log = LogUtil.searchLogByReqId(requestId);
                long endTime = System.currentTimeMillis();
                // 循环请求超过searchLogTimeout就退出循环。
                if (endTime - startTime > searchLogTimeout) {
                    break;
                }
            } while (logHitsIsEmpty(log));
            //写入日志
            writer.print(formatLog(log));
        } catch (Exception e) {
            writer.print(e);
        }
        writer.print("</pre></div>");
    }

    /**
     * Writes a TH element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableHeader(String html, String cssClasses) {
        writeTag("th", html, cssClasses);
    }

    /**
     * Writes a TD element with the specified contents.
     *
     * @param html the HTML contents
     */
    protected void writeTableData(String html) {
        writeTableData(html, null);
    }

    /**
     * Writes a TD element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableData(String html, String cssClasses) {
        writeTag("td", html, cssClasses);
    }

    /**
     * Writes an arbitrary HTML element with the specified contents and CSS
     * class names.
     *
     * @param tag        the tag name
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTag(String tag, String html, String cssClasses) {
        writer.print("<");
        writer.print(tag);
        if (cssClasses != null) {
            writer.print(" class=\"");
            writer.print(cssClasses);
            writer.print("\"");
        }
        writer.print(">");
        writer.print(html);
        writer.print("</");
        writer.print(tag);
        writer.print(">");
    }


    /**
     * Groups {@link TestResult}s by suite.
     */
    protected static class SuiteResult {
        private final String suiteName;
        private final List<TestResult> testResults = Lists.newArrayList();

        public SuiteResult(ISuite suite) {
            suiteName = suite.getName();
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                testResults.add(new TestResult(suiteResult.getTestContext()));
            }
        }

        public String getSuiteName() {
            return suiteName;
        }

        /**
         * @return the test results (possibly empty)
         */
        public List<TestResult> getTestResults() {
            return testResults;
        }
    }

    /**
     * Groups {@link ClassResult}s by test, type (configuration or test), and
     * status.
     */
    protected static class TestResult {
        /**
         * Orders test results by class name and then by method name (in
         * lexicographic order).
         */
        protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {
            @Override
            public int compare(ITestResult o1, ITestResult o2) {
                int result = o1.getTestClass().getName()
                        .compareTo(o2.getTestClass().getName());
                if (result == 0) {
                    result = o1.getMethod().getMethodName()
                            .compareTo(o2.getMethod().getMethodName());
                }
                return result;
            }
        };

        private final String testName;
        private final List<ClassResult> failedConfigurationResults;
        private final List<ClassResult> failedTestResults;
        private final List<ClassResult> skippedConfigurationResults;
        private final List<ClassResult> skippedTestResults;
        private final List<ClassResult> passedTestResults;
        private final int failedTestCount;
        private final int skippedTestCount;
        private final int passedTestCount;
        private final long duration;
        private final String includedGroups;
        private final String excludedGroups;

        public TestResult(ITestContext context) {
            testName = context.getName();

            Set<ITestResult> failedConfigurations = context
                    .getFailedConfigurations().getAllResults();
            Set<ITestResult> failedTests = context.getFailedTests()
                    .getAllResults();
            Set<ITestResult> skippedConfigurations = context
                    .getSkippedConfigurations().getAllResults();
            Set<ITestResult> skippedTests = context.getSkippedTests()
                    .getAllResults();
            Set<ITestResult> passedTests = context.getPassedTests()
                    .getAllResults();

            failedConfigurationResults = groupResults(failedConfigurations);
            failedTestResults = groupResults(failedTests);
            skippedConfigurationResults = groupResults(skippedConfigurations);
            skippedTestResults = groupResults(skippedTests);
            passedTestResults = groupResults(passedTests);

            failedTestCount = failedTests.size();
            skippedTestCount = skippedTests.size();
            passedTestCount = passedTests.size();

            duration = context.getEndDate().getTime()
                    - context.getStartDate().getTime();

            includedGroups = formatGroups(context.getIncludedGroups());
            excludedGroups = formatGroups(context.getExcludedGroups());
        }

        /**
         * Groups test results by method and then by class.
         */
        protected List<ClassResult> groupResults(Set<ITestResult> results) {
            List<ClassResult> classResults = Lists.newArrayList();
            if (!results.isEmpty()) {
                List<MethodResult> resultsPerClass = Lists.newArrayList();
                List<ITestResult> resultsPerMethod = Lists.newArrayList();

                List<ITestResult> resultsList = Lists.newArrayList(results);
                Collections.sort(resultsList, RESULT_COMPARATOR);
                Iterator<ITestResult> resultsIterator = resultsList.iterator();
                assert resultsIterator.hasNext();

                ITestResult result = resultsIterator.next();
                resultsPerMethod.add(result);

                String previousClassName = result.getTestClass().getName();
                String previousMethodName = result.getMethod().getMethodName();
                while (resultsIterator.hasNext()) {
                    result = resultsIterator.next();

                    String className = result.getTestClass().getName();
                    if (!previousClassName.equals(className)) {
                        // Different class implies different method
                        assert !resultsPerMethod.isEmpty();
                        resultsPerClass.add(new MethodResult(resultsPerMethod));
                        resultsPerMethod = Lists.newArrayList();

                        assert !resultsPerClass.isEmpty();
                        classResults.add(new ClassResult(previousClassName,
                                resultsPerClass));
                        resultsPerClass = Lists.newArrayList();

                        previousClassName = className;
                        previousMethodName = result.getMethod().getMethodName();
                    } else {
                        String methodName = result.getMethod().getMethodName();
                        if (!previousMethodName.equals(methodName)) {
                            assert !resultsPerMethod.isEmpty();
                            resultsPerClass.add(new MethodResult(resultsPerMethod));
                            resultsPerMethod = Lists.newArrayList();

                            previousMethodName = methodName;
                        }
                    }
                    resultsPerMethod.add(result);
                }
                assert !resultsPerMethod.isEmpty();
                resultsPerClass.add(new MethodResult(resultsPerMethod));
                assert !resultsPerClass.isEmpty();
                classResults.add(new ClassResult(previousClassName,
                        resultsPerClass));
            }
            return classResults;
        }

        public String getTestName() {
            return testName;
        }

        /**
         * @return the results for failed configurations (possibly empty)
         */
        public List<ClassResult> getFailedConfigurationResults() {
            return failedConfigurationResults;
        }

        /**
         * @return the results for failed tests (possibly empty)
         */
        public List<ClassResult> getFailedTestResults() {
            return failedTestResults;
        }

        /**
         * @return the results for skipped configurations (possibly empty)
         */
        public List<ClassResult> getSkippedConfigurationResults() {
            return skippedConfigurationResults;
        }

        /**
         * @return the results for skipped tests (possibly empty)
         */
        public List<ClassResult> getSkippedTestResults() {
            return skippedTestResults;
        }

        /**
         * @return the results for passed tests (possibly empty)
         */
        public List<ClassResult> getPassedTestResults() {
            return passedTestResults;
        }

        public int getFailedTestCount() {
            return failedTestCount;
        }

        public int getSkippedTestCount() {
            return skippedTestCount;
        }

        public int getPassedTestCount() {
            return passedTestCount;
        }

        public long getDuration() {
            return duration;
        }

        public String getIncludedGroups() {
            return includedGroups;
        }

        public String getExcludedGroups() {
            return excludedGroups;
        }

        /**
         * Formats an array of groups for display.
         */
        protected String formatGroups(String[] groups) {
            if (groups.length == 0) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            builder.append(groups[0]);
            for (int i = 1; i < groups.length; i++) {
                builder.append(", ").append(groups[i]);
            }
            return builder.toString();
        }
    }

    /**
     * Groups {@link MethodResult}s by class.
     */
    protected static class ClassResult {
        private final String className;
        private final List<MethodResult> methodResults;

        /**
         * @param className     the class name
         * @param methodResults the non-null, non-empty {@link MethodResult} list
         */
        public ClassResult(String className, List<MethodResult> methodResults) {
            this.className = className;
            this.methodResults = methodResults;
        }

        public String getClassName() {
            return className;
        }

        /**
         * @return the non-null, non-empty {@link MethodResult} list
         */
        public List<MethodResult> getMethodResults() {
            return methodResults;
        }
    }

    /**
     * Groups test results by method.
     */
    protected static class MethodResult {
        private final List<ITestResult> results;

        /**
         * @param results the non-null, non-empty result list
         */
        public MethodResult(List<ITestResult> results) {
            this.results = results;
        }

        /**
         * @return the non-null, non-empty result list
         */
        public List<ITestResult> getResults() {
            return results;
        }
    }
}

