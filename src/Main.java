import au.com.bytecode.opencsv.CSVReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

                String fromClient;
                String toClient;
                int i = 0;
                ServerSocket server = new ServerSocket(8080);
                System.out.println("wait for connection on port 8080");
                double[] data = new double[2048];
                List<List<String>> records = new ArrayList<List<String>>();
                try (CSVReader csvReader = new CSVReader(new FileReader("data.csv"));) {
                    String[] values = null;
                    while ((values = csvReader.readNext()) != null) {
                        data[i] = Double.parseDouble(values[0]);
                        i += 1;
                    }
                }
//                double dob = 2;
                boolean run = true;
                while(run) {
                    Socket client = server.accept();
                    System.out.println("got connection on port 8080");
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter out = new PrintWriter(client.getOutputStream(),true);

                    fromClient = in.readLine();
                    System.out.println("received: " + fromClient);
                    out.println(Arrays.toString(data));


                }
                System.exit(0);


    }
}
