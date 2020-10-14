import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class testHSLVDPro {
    public static void main(String[] args) {
        double[] data = new double[2048];
        int i = 0;
        List<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new FileReader("data.csv"));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                data[i] = Double.parseDouble(values[0]);
                i += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        HLSVDPro hlsvdObj = new HLSVDPro(data, data, 10, 0.256f, 5);
        hlsvdObj.run();


    }
}
