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

    private Object[][] readJsonData(String fileName) throws IOException {
        String filePath = "testdata/" + fileName;  // FIXED: was ".\\testdata\\"

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> dataList = objectMapper.readValue(new File(filePath),
                new TypeReference<List<Map<String, String>>>() {
                });

        Object[][] dataArray = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = new Object[] { dataList.get(i) };
        }

        return dataArray;
    }

    private Object[][] readCsvData(String fileName) throws IOException {
        String filePath = "testdata/" + fileName;  // FIXED: was ".\\testdata\\"

        List<String[]> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                dataList.add(data);
            }
        }

        Object[][] dataArray = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = dataList.get(i);
        }

        return dataArray;
    }

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