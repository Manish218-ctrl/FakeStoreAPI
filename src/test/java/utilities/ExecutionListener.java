package utilities;

import org.testng.IExecutionListener;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class ExecutionListener implements IExecutionListener {

    @Override
    public void onExecutionStart() {
        System.out.println("TestNG execution started");
    }

    @Override
    public void onExecutionFinish() {
        System.out.println("TestNG execution finished");
        try {
            File report = new File("test-output/emailable-report.html");
            if (Desktop.isDesktopSupported() && report.exists()) {
                Desktop.getDesktop().open(report);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
