package dsanchez.ssii.psi5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.net.ServerSocketFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

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
		Integer verified; // 1 if verified 0 if not
		LoggerUtil.getLogger().info("Server started successfully");
		Integer returned = null;
		Socket socket=null;
		BufferedReader input=null;
		BufferedWriter output=null;
		String message=null;
		while (true) {

			try {
				System.err.println("Waiting for client conections...");
				LoggerUtil.getLogger().info("Waiting for client conections...");
				socket = serverSocket.accept();
				LoggerUtil.getLogger().info("Client connected. Client address: " + socket.getLocalSocketAddress());
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Get the response
				LoggerUtil.getLogger().debug("Retrieving the client data...");
				message = input.readLine();
				System.out.println(message);
				LoggerUtil.getLogger().debug("Client data retrieved:\n	-> " + message);
			} catch (Throwable oops) {
				LoggerUtil.getLogger().error("Error when waiting for connections", oops);
			}

			try {

				LoggerUtil.getLogger().debug("Parsing client data...");
				ObjectMapper mapper = new ObjectMapper();
				TransmitedMessage transmitedMessage = mapper.readValue(message, TransmitedMessage.class);
				LoggerUtil.getLogger().debug("Data parsed correctly");
				
				Object[] check = checkMessageSign(transmitedMessage);

				verified = (Integer) check[0];
				RSAPublicKey publicKey = (RSAPublicKey) check[1];

				LoggerUtil.getLogger().info("Preparing to insert data in the Data Base...");
				returned = SQLiteUtil.insertIntoTable(transmitedMessage.getMessage(),
						transmitedMessage.getSignedMessage(), publicKey.getEncoded(), verified);

			} catch (Throwable oops) {
				oops.printStackTrace();
				LoggerUtil.getLogger().error("There was an error during the execution", oops);
			}

			try {
				LoggerUtil.getLogger().info("Sending the data to the client...");
				// Send the response
				output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

				if (returned == null) {
					// The "\n" is necessary because it tells the client when a
					// line is read and receives the message
					output.write("Server error\n");
				} else if (returned == 1) {
					output.write("Stored correctly\n");
				}
				output.flush();

				// Closing
				output.close();
				input.close();
				socket.close();
				LoggerUtil.getLogger().info("Data sent correctly");
			} catch (Throwable oops) {
				LoggerUtil.getLogger().error("There was a problem while sending the data back to the client", oops);
			}

		}

	}

	private Object[] checkMessageSign(TransmitedMessage transmitedMessage) throws NoSuchAlgorithmException,
			SignatureException, InvalidKeyException, InvalidKeySpecException, Base64DecodingException {

		Object[] tupple = new Object[2];// 1st value = verified; 2nd value =
										// RSAPublicKey
		Boolean result = false;
		Signature sg = Signature.getInstance("SHA256WithRSA");
		KeyFactory factory = KeyFactory.getInstance("RSA");
		BigInteger modulus = new BigInteger(Base64.decode(transmitedMessage.getModulus().getBytes()));
		BigInteger exponent = new BigInteger(Base64.decode(transmitedMessage.getExponent().getBytes()));
		RSAPublicKeySpec ks = new RSAPublicKeySpec(modulus, exponent);
		RSAPublicKey publicKey = (RSAPublicKey) factory.generatePublic(ks);

		sg.initVerify(publicKey);

		sg.update(transmitedMessage.getMessage().getBytes());

		byte[] signedMessage = Base64.decode(transmitedMessage.getSignedMessage().getBytes());

		result = sg.verify(signedMessage);

		tupple[0] = result ? 1 : 0; // If result is true -> return 1; else
									// return 0
		tupple[1] = publicKey;

		return tupple;
	}
}
