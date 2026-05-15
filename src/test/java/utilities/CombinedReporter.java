package utilities;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class CombinedReporter implements ISuiteListener {

    private static final String EMAIL_REPORT = "test-output/emailable-report.html";
    private static final String ALLURE_RESULTS = "allure-results";
    private static final String ALLURE_REPORT = "allure-report";

    @Override
    public void onStart(ISuite suite) {
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("Starting Combined Reporter Process");
        openEmailableReport();
        generateAndOpenAllureReport();
        System.out.println("Combined Reporter Process Finished");
    }

    private void openEmailableReport() {
        try {
            File report = new File(EMAIL_REPORT);
            if (Desktop.isDesktopSupported() && report.exists()) {
                Desktop.getDesktop().browse(report.toURI());
                System.out.println("Emailable report opened successfully at: " + report.getAbsolutePath());
            } else {
                System.out.println("Emailable report not found or Desktop not supported.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateAndOpenAllureReport() {
        try {
            File resultsDir = new File(ALLURE_RESULTS);
            File reportDir = new File(ALLURE_REPORT);

            File[] files = resultsDir.listFiles();
            if (!resultsDir.exists() || files == null || files.length == 0) {
                System.out.println("Allure results folder is empty. No report generated!");
                return;
            }

            System.out.println("Checking for Allure CLI and generating report...");

            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String command = "allure generate " + ALLURE_RESULTS + " --clean -o " + ALLURE_REPORT;

            ProcessBuilder pb = isWindows
                    ? new ProcessBuilder("cmd", "/c", command)
                    : new ProcessBuilder("bash", "-c", command);

            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.out.println("Allure report generation failed. Please ensure Allure CLI is installed and in PATH.");
                return;
            }

            File indexHtml = new File(reportDir, "index.html");
            if (indexHtml.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(indexHtml.toURI());
                System.out.println("Allure report opened successfully at: " + indexHtml.getAbsolutePath());
            } else {
                System.out.println("Allure report generated at: " + indexHtml.getAbsolutePath());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}