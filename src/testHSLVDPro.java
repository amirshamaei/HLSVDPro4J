import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class testHSLVDPro {
    public static void main(String[] args) {
        double[] datar = new double[2048];
        double[] datai = new double[2048];
        int i = 0;
        List<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new FileReader("data1.csv"));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                datar[i] = Double.parseDouble(values[0]);
                datai[i] = Double.parseDouble(values[1]);
                i += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int[] nsv_arr = new int[]{13,14,15,16};
        ArrayList<HLSVDProResult> results = new ArrayList<>();
        for (int j = 0; j < 4 ; j++) {

            int finalJ = j;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    HLSVDPro hlsvdObj = new HLSVDPro(datar, datai, nsv_arr[finalJ], 2.5E-1f, 5);
                    results.add(hlsvdObj.run());
                }
            });
            t.run();

        }

        System.out.println("finished");

    }
}
