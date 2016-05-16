package dsanchez.ssii.psi5;

import java.io.FileInputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

	//private final static String PROPERTIES_PATH = "mail.properties";
	
	//<-------------- Use this private instead the previous one to run as java apPlication instead of *.jar ------------>
	private final static String PROPERTIES_PATH = "src/dsanchez/ssii/psi5/mail.properties";
	//<-----------------------------------------------------------------------------------------------------------------

	@SuppressWarnings("static-access")
	public static void sendMessage() {
		Properties mailProperties = new Properties();
		Properties userProperties = new Properties();
		FileInputStream file;
		String recipient;
		String from;
		String subject;
		String password; // Highly recommended to use an Application password
							// and not the real one
		String content =null;

		// Set the mail properties
		try {
			LoggerUtil.getMailLogger().info("Setting the message properties...");

			// Gmail host
			mailProperties.put("mail.smtp.host", "smtp.gmail.com");
			LoggerUtil.getMailLogger().info("Host set.");

			// Enable STARTTLS
			mailProperties.setProperty("mail.smtp.starttls.enable", "true");
			LoggerUtil.getMailLogger().info("STARTTLS enabled.");

			// Gmail port
			mailProperties.setProperty("mail.smtp.port", "587");
			LoggerUtil.getMailLogger().info("Port set.");

			// Enable authentication
			mailProperties.setProperty("mail.smtp.auth", "true");
			LoggerUtil.getMailLogger().info("Authentication enabled.");

			LoggerUtil.getMailLogger().info("Mail properties set successfully.");
		} catch (Throwable oops) {
			LoggerUtil.getMailLogger().error("An error orcurred while setting the mail properties", oops);

		}

		// Obtaining the user properties
		try {
			LoggerUtil.getMailLogger().info("Obtaining mail data...");
			file = new FileInputStream(PROPERTIES_PATH);

			// We load the properties in the properties file
			userProperties.load(file);

			// Once the properties are loaded we close the file
			file.close();

			LoggerUtil.getMailLogger().info("Mail data obtained successfully.");
		} catch (Throwable oops) {
			LoggerUtil.getMailLogger().error("An error ocurred while obtaining the mail data.", oops);
		}

		// Setting the user data for the email
		try {
			LoggerUtil.getMailLogger().info("Loading mail data...");
			recipient = userProperties.getProperty("mail.recipient");
			from = userProperties.getProperty("mail.from");
			password = userProperties.getProperty("mail.password");
			subject = userProperties.getProperty("mail.subject");

			content = getContent();
			
			LoggerUtil.getMailLogger().info("Mail data loaded successfully.");

			// Sending the message
			try {
				LoggerUtil.getMailLogger().info("Setting mail data....");

				LoggerUtil.getMailLogger().info("Setting the session...");
				//Setting the session
				Session session = Session.getDefaultInstance(mailProperties, new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(from, password);
					}

				});

				LoggerUtil.getMailLogger().info("Session set.");

//				Creating the message
				LoggerUtil.getMailLogger().info("Creating the message...");
				MimeMessage email = new MimeMessage(session);
				email.setFrom(new InternetAddress(from));
				email.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
				email.setSubject(subject);
				email.setContent(content,"text/html");
				LoggerUtil.getMailLogger().info("Message created successfully.");
				
				//Sending the message
				LoggerUtil.getMailLogger().info("Sending the message...");
				Transport transport = session.getTransport("smtp");
				transport.send(email);
				LoggerUtil.getMailLogger().info(String.format("Mail sent successfully from %s to %s", from, recipient));
				
			} catch (Throwable oops) {
				LoggerUtil.getMailLogger().error("An error ocurred while sending the message", oops);
			}

		} catch (Throwable oops) {
			LoggerUtil.getMailLogger().error("An error ocurred while setting the mail data", oops);
		}

	}

	private static String getContent() {
		String result;
		Object[] percentages = SQLiteUtil.getPercentages();//0 -> Current month; 1 -> Previous month -> 2 Number of previous months
		String tendency = "NEUTRAL";
		String colour = "blue";
		
		if((double)percentages[0]>(double)percentages[1]){
			tendency= "POSSITIVE";
			colour = "green";
		}else if((double)percentages[0]<(double)percentages[1]){
			tendency = "NEGATIVE";
			colour="red";
		}
		
		result = String.format("<b>Previous %d months:</b> %.2f%% <br/>"
				+ "<b>Current month percentage:</b> %.2f%% <br/>"
				+ "<b>Tendency:</b> <span style=\"color:%s\">%s</span>.",percentages[2],percentages[1], percentages[0],colour,tendency);
		
		return result;
	}

}
