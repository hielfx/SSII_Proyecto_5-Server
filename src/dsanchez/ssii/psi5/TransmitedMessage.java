package dsanchez.ssii.psi5;

import java.math.BigInteger;

public class TransmitedMessage {

	private String modulus;
	public String getModulus() {
		return modulus;
	}



	public void setModulus(String modulus) {
		this.modulus = modulus;
	}



	public String getExponent() {
		return exponent;
	}



	public void setExponent(String exponent) {
		this.exponent = exponent;
	}

	private String exponent;
	private String message;
	private String signedMessage;
	
	public TransmitedMessage() {
		// TODO Auto-generated constructor stub
	}

	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSignedMessage() {
		return signedMessage;
	}

	public void setSignedMessage(String signedMessage) {
		this.signedMessage = signedMessage;
	}
	
	
	
}
