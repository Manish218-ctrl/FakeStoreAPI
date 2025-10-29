package utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.testng.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReporter implements ITestListener, ISuiteListener {

    private static ExtentReports extent;
    private static ExtentSparkReporter sparkReporter;
    private static ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();
    private static Map<String, ExtentTest> testSuiteMap = new ConcurrentHashMap<>();
    private static String reportName;

    private static final List<String> orderedSuites = Arrays.asList(
            "Product Tests",
            "Cart Tests",
            "User Tests",
            "Login Tests"
    );

    public synchronized static ExtentReports getExtentInstance() {
        if (extent == null) {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            reportName = "Extent-Report-" + timeStamp + ".html";
            String reportPath = System.getProperty("user.dir") + File.separator + "reports" + File.separator + reportName;

            sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("API Automation Test Report");
            sparkReporter.config().setReportName("API Test Execution Summary");
            sparkReporter.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Host Name", "Localhost");
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("User","Automation Tester");
        }
        return extent;
    }

    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getName();
        ExtentReports report = getExtentInstance();

        if (!testSuiteMap.containsKey(suiteName)) {
            ExtentTest suiteNode = report.createTest(suiteName);
            suiteNode.info("Suite started: " + suiteName);
            suiteNode.assignCategory("Suite: " + suiteName);
            testSuiteMap.put(suiteName, suiteNode);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest suiteNode = testSuiteMap.get(result.getTestContext().getName());
        ExtentTest methodNode = suiteNode.createNode(result.getMethod().getMethodName());
        methodNode.assignCategory(result.getTestContext().getName());
        testNode.set(methodNode);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.PASS, result.getMethod().getMethodName() + " passed.");
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.FAIL, result.getMethod().getMethodName() + " failed.");
        test.log(Status.FAIL, result.getThrowable());
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.SKIP, result.getMethod().getMethodName() + " skipped.");
        if (result.getThrowable() != null)
            test.info("Reason: " + result.getThrowable());
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentTest suiteNode = testSuiteMap.get(context.getName());
        if (suiteNode != null) {
            suiteNode.info("Suite finished: " + context.getSuite().getName());
            suiteNode.info("Passed: " + context.getPassedTests().size());
            suiteNode.info("Failed: " + context.getFailedTests().size());
            suiteNode.info("Skipped: " + context.getSkippedTests().size());
        }
        // DO NOT FLUSH HERE (handled in onFinish(ISuite))
    }

    // ---- Handle once per entire suite ----
    @Override
    public void onFinish(ISuite suite) {
        reorderSuitesInReport();
        getExtentInstance().flush();
        openReport();
    }

    private void reorderSuitesInReport() {
        Map<String, ExtentTest> reordered = new LinkedHashMap<>();
        for (String name : orderedSuites) {
            if (testSuiteMap.containsKey(name)) {
                reordered.put(name, testSuiteMap.get(name));
            }
        }
        testSuiteMap.clear();
        testSuiteMap.putAll(reordered);
    }

    private void openReport() {
        try {
            File reportFile = new File(System.getProperty("user.dir")
                    + File.separator + "reports" + File.separator + reportName);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(reportFile.toURI());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getExecutionTime(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}

