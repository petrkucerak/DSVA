import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
	private String address;
	private int port;
	private BufferedReader in;
	private PrintWriter out;
	
	/**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Enter in the
     * listener sends the textfield contents to the server.
     */
    public TCPClient(String address, int port) {
    	this.address = address;
    	this.port = port;
    }

	/**
	 * Implements the connection logic by prompting the end user for the
	 * server's IP address, connecting, setting up streams, and consuming the
	 * welcome messages from the server. The Capitalizer protocol says that the
	 * server sends three lines of text to the client immediately after
	 * establishing a connection.
	 */
	public void connectToServer() throws IOException {

		// Make connection and initialize streams
		Socket socket = new Socket(address, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		String message = null;
		String reply = null;
		Boolean reading = true;

		// Read initial welcome messages from the server
		for (int i = 0; i < 2; i++) {
			System.out.println(in.readLine());
		}

		// Write and read from socket
		while (reading == true) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		    System.out.print("\nPlease enter message for server : ");
		    try {
		        message = reader.readLine();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    	reading = false;
		    }
		    System.out.println("Message to server : " + message);
		    out.write(message + "\n");
		    out.flush();
		    reply = in.readLine();
		    if (reply == null || reply.equals("")) 
                reading = false;
            System.out.println("Reply from server : " + reply);
		}
		System.out.println("Closing communication to server.");
		socket.close();
	}

	/**
	 * Runs the client application.
	 */
	public static void main(String[] args) throws Exception {
		// handle commandline parameters (there should be 2 of them IP and port)
		String ipaddr = "127.0.0.1";
		String port = "12345";
		if (args.length == 2) {
			ipaddr = args[0];
			port = args[1];
		}
		
		TCPClient client = new TCPClient(ipaddr, new Integer(port));
		client.connectToServer();
	}

}
