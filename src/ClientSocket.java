// This class extends the Socket class
// supports reading and writing to Socket
// J.Starcukova 2020
// modified Amir Shamaei october, 2020

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class ClientSocket extends Socket {

	static DataOutputStream out;
	static DataInputStream in;

	// creates a stream socket and connects it to the specified port number at the
	// specified IP address.
	ClientSocket(int port) throws UnknownHostException, IOException {
		super("", port);
		out = new DataOutputStream(super.getOutputStream());
		in = new DataInputStream(super.getInputStream());
	}

	// reading from a socket ---------------

	// reads Strings
	// first the length is read, followed by a string
	public String read_str() throws IOException {
		String str = "";
		int len = readInt();
		if (len > 0) {
			while (len > 0) {
				str += (char) (in.readByte());
				len--;
			}
		}
		return str;
	}

	// reads int
	public int readInt() throws IOException {
		return in.readInt();
	}

	// reads double
	public double readDouble() throws IOException {
		return in.readDouble();
	}

	// reads double array of known size
	public double[] readDoubleArray(int size) throws IOException {
		double[] x = new double[size];
		byte[] b = new byte[size * Double.SIZE / Byte.SIZE];

		in.readFully(b);
		ByteBuffer d = ByteBuffer.wrap(b);
		d.asDoubleBuffer().get(x);

		return x;
	}

	// writing to socket ------------------------

	// converts integer array to byte array
	public byte[] integerToBytes(int x[]) throws IOException {

		byte[] dest = new byte[x.length * Integer.SIZE / Byte.SIZE];
		ByteBuffer buf = ByteBuffer.wrap(dest);
		buf.asIntBuffer().put(x);
		return dest;
	}

	public byte[] floatToBytes(float x[]) throws IOException {
		byte[] dest = new byte[x.length * Float.SIZE / Byte.SIZE];
		ByteBuffer buf = ByteBuffer.wrap(dest);
		buf.asFloatBuffer().put(x);
		return dest;
	}

	// converts double array to byte array
	public byte[] doubleToBytes(double x[]) throws IOException {
		byte[] dest = new byte[x.length * Double.SIZE / Byte.SIZE];
		ByteBuffer buf = ByteBuffer.wrap(dest);
		buf.asDoubleBuffer().put(x);
		return dest;
	}

	// writes integer array to socket
	protected void write_intArray(int[] x) throws IOException {
		writeInt(x.length);
		byte[] p = integerToBytes(x);
		out.write(p, 0, p.length);
		out.flush();
	}

	// writes size of double array followed by the double array to socket
	public void write_doubleArray(double[] x) throws IOException {
		// writeInt(x.length);
		out.writeInt(x.length);
		out.flush();
		byte[] p = doubleToBytes(x);
		out.write(p, 0, p.length);
		out.flush();
	}

	// writes size of string followed by the string to socket
	public void write_str(String name) throws IOException {
		int size = name.length();
		out.writeInt(size);
		out.flush();
		out.writeBytes(name);
		out.flush();
	}

	// writes integer to socket
	public void writeInt(int x) throws IOException {
		int[] xa = { x };
		byte[] p = integerToBytes(xa);
		out.write(p, 0, p.length);
		out.flush();

	}

	public void writeFloat(float x) throws IOException {
		float[] xa = { x };
		byte[] p = floatToBytes(xa);
		out.write(p, 0, p.length);
		out.flush();

	}

	// close socket closes DataOutputStream
	public void closeConnection() {

		try {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			super.close(); // close the socket
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
