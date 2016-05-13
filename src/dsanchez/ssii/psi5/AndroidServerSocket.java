package dsanchez.ssii.psi5;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Daniel Sánchez on 11/05/2016.
 */
public class AndroidServerSocket {

	private ServerSocket serverSocket;
	private final static Integer PORT = 7070;

	public AndroidServerSocket() throws IOException {
		ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();

		serverSocket = serverSocketFactory.createServerSocket(PORT);
	}

	public void runServer() {
		while (true) {

			try {
				System.err.println("Waiting for client conections...");
				Socket socket = serverSocket.accept();
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Get the response
				String message = input.readLine();
				System.out.println(message);

				// Send the response
				BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

				// The "\n" is necessary because it tells the client when a line is read and receives the message
				output.write("Hello world\n");
				output.flush();

				// Closing
				output.close();
				input.close();
				socket.close();

			} catch (Throwable oops) {
				// TODO: Change to error message
				oops.printStackTrace();
			}
		}

	}
}
