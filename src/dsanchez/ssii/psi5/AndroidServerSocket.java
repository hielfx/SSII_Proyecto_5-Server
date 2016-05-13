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
		while (true) {

			try {
				System.err.println("Waiting for client conections...");
				Socket socket = serverSocket.accept();
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Get the response
				String message = input.readLine();
				System.out.println(message);
				
				
				
				try{
					
					ObjectMapper mapper = new ObjectMapper();
					TransmitedMessage transmitedMessage = mapper.readValue(message, TransmitedMessage.class);
					
					checkMessageSign(transmitedMessage);
					
				}catch(Throwable oops){
					oops.printStackTrace();
				}
				
				

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

	private Boolean checkMessageSign(TransmitedMessage transmitedMessage) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException, Base64DecodingException {
	
		Boolean result=false;
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
		
		return result;
	}
}
