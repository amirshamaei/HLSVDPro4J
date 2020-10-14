import java.io.IOException;

// written by Amir Shamaei - Bern, Switzerlan 14/10/2020
public class HLSVDPro {
//    Args:
//    realdata: an array of double numbers.
//
//    imagdata: an iterable of double numbers.
//
//    nsv_sought (int): The number of singular values sought. The function
//    will return a maximum of this many singular values.
//
//    dwell_time (float): Dwell time in milliseconds.
//    m (int): (optional) default=len(data)/2, Use to set the size of
//    the Hankel matrix used to compute the singular values. Hankel
//    matrix shape is (L+1,L) where L = len(data)-m-1
    double[] realdata;
    double[] imagdata;
    int nsv_sought;
    float dwell_time;
    int m;

    HLSVDProResult result;

    static int port = 42311;

    static ClientSocket socket = null;
    static ShutDownHook sdh = null;
    private int nsv_found;
    private double[] singvals;
    private double[] freq;
    private double[] damp;
    private double[] ampl;
    private double[] phas;


    public HLSVDPro(double[] realdata, double[] imagdata, int nsv_sought, float dwell_time, int m) {
        this.realdata = realdata;
        this.imagdata = imagdata;
        this.nsv_sought = nsv_sought;
        this.dwell_time = dwell_time;
        this.m = m;
    }

    public HLSVDProResult run() {
        try {
//            ServerSocket server = new ServerSocket(8080);
//            System.out.println("wait for connection on port 8080");
//            boolean run = true;
//            String fromClient;
//            while(run) {
//                Socket client = server.accept();
//                System.out.println("got connection on port 8080");
//                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                PrintWriter out = new PrintWriter(client.getOutputStream(),true);
//
//                fromClient = in.readLine();
//                System.out.println("received: " + fromClient);
//                out.println(Arrays.toString(data));
//            }
//            System.exit(0);
            if (socket == null)
                socket = new ClientSocket(port);
            socket.write_str("writeData");
            socket.write_doubleArray(realdata);
            socket.write_doubleArray(imagdata);
            socket.writeInt(nsv_sought);
            socket.writeFloat(dwell_time);
            socket.writeInt(m);
            socket.write_str("run");

            nsv_found = socket.readInt();
//            nsv_found = nsv_sought;
            singvals = socket.readDoubleArray(nsv_found);
            freq = socket.readDoubleArray(nsv_found);
            damp = socket.readDoubleArray(nsv_found);
            ampl = socket.readDoubleArray(nsv_found);
            phas = socket.readDoubleArray(nsv_found);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HLSVDProResult();
    }
    class ShutDownHook extends Thread {
        // executed when the JVM is shutting down
        public void run() {
            try {
                if (socket != null)
                    socket.write_str("FINISHED");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (socket != null)
                socket.closeConnection();

        }
    }
}