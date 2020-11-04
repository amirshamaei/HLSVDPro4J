import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class testHLSVDPro4CSI {
        public static void main(String[] args) throws IOException {
            double[] datar = new double[2048];
            double[] datai = new double[2048];
            int i = 0;

            MatFileReader jMAT = new MatFileReader("MATCSI.mat");
            Map<String, MLArray> jMATcontent = jMAT.getContent();
            MLDouble MLfidReal = (MLDouble) jMATcontent.get("fidReal");
            double[][] realTD = MLfidReal.getArray();
            MLDouble MLfidImag = (MLDouble) jMATcontent.get("fidImag");
            double[][] imagTD = MLfidReal.getArray();


            ArrayList<HLSVDProResult> results = new ArrayList<>();
            for (int j = 0; j < 100 ; j++) {
                double[] finalDatar = realTD[j];
                double[] finalDatai = imagTD[j];
                int finalJ = j;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        HLSVDPro hlsvdObj = new HLSVDPro(finalDatar, finalDatai, 15, 2.5E-1f, 5);
                        HLSVDProResult hslvdResult = hlsvdObj.run();
                        System.out.println(finalJ + Arrays.toString(hslvdResult.amplitudes));
                        results.add(hslvdResult);
                    }
                });
                t.run();

            }

            System.out.println("finished");

        }
}


