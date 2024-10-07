import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

	public static void main(String[] args) throws Exception {
		System.out.println("The TCP server is running.");
		int clientNumber = 1;
		ServerSocket listener = new ServerSocket(12345);
		try {
			while (true) {
				new TCPServerThread(listener.accept(), clientNumber++).start();
			}
		} finally {
			listener.close();
		}
	}

	private static class TCPServerThread extends Thread {
		private Socket socket;
		private int clientNumber;
		private int sessionLineNumber = 1;
		private static int globalLineNumber = 1;

		public TCPServerThread(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			log("New connection with client# " + clientNumber + " at " + socket);
		}

		/**
		 * Services this thread's client by first sending the client a welcome
		 * message then repeatedly reading strings and sending back the altered
		 * version of the string.
		 */
		public void run() {
			try {

				// Use characters instead of bytes.
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				// Send a welcome message to the client.
				out.println("Hello, you are client #" + clientNumber + ".");
				out.println("Enter a line with only a period to quit");

				// Get messages from the client, line by line
				// log them and alter them
				while (true) {
					String input = in.readLine();
					if (input == null || input.equals(".")) {
						break;
					}
					String output = "(" + sessionLineNumber++ + ":" + globalLineNumber++ + ") - "
							+ input.replaceAll("DSV", "awesome DSV");
					log("cli #" + clientNumber + output);
					out.println(output);
				}
			} catch (IOException e) {
				log("Error handling client# " + clientNumber + ": " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					log("Couldn't close a socket, what's going on?");
				}
				log("Connection with client# " + clientNumber + " closed");
			}
		}

		/**
		 * Logs a simple message.
		 */
		private void log(String message) {
			System.out.println(message);
		}
	}

}
