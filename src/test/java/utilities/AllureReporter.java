/*package utilities;



import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;

    public class AllureReporter implements ISuiteListener {

        private static final String ALLURE_RESULTS_DIR = "allure-results";
        private static final String ALLURE_REPORT_DIR = "allure-report";

        @Override
        public void onFinish(ITestContext context) {
            try {
                // Generate Allure report
                Process generateReport = new ProcessBuilder("allure", "generate", ALLURE_RESULTS_DIR, "--clean", "-o", ALLURE_REPORT_DIR)
                        .inheritIO()
                        .start();
                generateReport.waitFor();

                // Open the generated report index.html in default browser
                File reportIndex = new File(ALLURE_REPORT_DIR + "/index.html");
                if (reportIndex.exists()) {
                    Desktop.getDesktop().browse(reportIndex.toURI());
                } else {
                    System.out.println("Allure report not found!");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/






package utilities;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.IOException;

public class AllureReporter implements ISuiteListener {

    private static final String ALLURE_RESULTS_DIR = "allure-results";

    @Override
    public void onFinish(ISuite suite) {
        try {
            // Generate and open Allure report in one command
            ProcessBuilder pb = new ProcessBuilder("allure", "open", ALLURE_RESULTS_DIR);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();

            System.out.println("Allure report generated and opened successfully!");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(ISuite suite) {
        // Optional
    }
}
