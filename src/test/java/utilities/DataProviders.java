package utilities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.testng.annotations.DataProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataProviders {

    // --- Core JSON Reader ---
    // Reads any JSON file from the testdata folder and returns a 2D array of Maps.
    private Object[][] readJsonData(String fileName) throws IOException {
        String filePath = ".\\testdata\\" + fileName;

        // Use ObjectMapper to read JSON file and map it to a List of Maps
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> dataList = objectMapper.readValue(new File(filePath),
                new TypeReference<List<Map<String, String>>>() {
                });

        // Convert List<Map<String, String>> to Object[][] (Each row is a single Map object)
        Object[][] dataArray = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = new Object[] { dataList.get(i) };
        }

        return dataArray;
    }

    // --- Core CSV Reader ---
    // Reads any CSV file from the testdata folder, skipping the header, and returns a 2D array of String arrays.
    private Object[][] readCsvData(String fileName) throws IOException {
        String filePath = ".\\testdata\\" + fileName;

        List<String[]> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip the first line (header)
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                // Splits the line by comma. Adjust delimiter if necessary.
                String[] data = line.split(",");
                dataList.add(data);
            }
        }

        // Convert List<String[]> to Object[][] (Each row is a String array)
        Object[][] dataArray = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = dataList.get(i);
        }

        return dataArray;
    }


    // --- JSON Data Provider Wrappers ---

    @DataProvider(name = "productJsonDataProvider")
    public Object[][] productJsonDataProvider() throws IOException {
        return readJsonData("product.json");
    }

    @DataProvider(name = "userJsonDataProvider")
    public Object[][] userJsonDataProvider() throws IOException {
        return readJsonData("user.json");
    }

    @DataProvider(name = "cartJsonDataProvider")
    public Object[][] cartJsonDataProvider() throws IOException {
        return readJsonData("cart.json");
    }

    // --- CSV Data Provider Wrappers ---

    @DataProvider(name = "productCsvDataProvider")
    public Object[][] productCsvDataProvider() throws IOException {
        return readCsvData("product.csv");
    }

    @DataProvider(name = "userCsvDataProvider")
    public Object[][] userCsvDataProvider() throws IOException {
        return readCsvData("user.csv");
    }

    @DataProvider(name = "cartCsvDataProvider")
    public Object[][] cartCsvDataProvider() throws IOException {
        return readCsvData("cart.csv");
    }
}
