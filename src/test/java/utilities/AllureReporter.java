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
