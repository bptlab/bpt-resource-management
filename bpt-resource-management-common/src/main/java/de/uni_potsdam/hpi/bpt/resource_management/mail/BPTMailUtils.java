package de.uni_potsdam.hpi.bpt.resource_management.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class BPTMailUtils {

	public static Session getGMailSession(String username, String password) {
		final Properties properties = new Properties();
		
		// receive
		properties.setProperty("mail.pop3.host", "pop.gmail.com");
	    properties.setProperty("mail.pop3.user", username);
	    properties.setProperty("mail.pop3.password", password);
	    properties.setProperty("mail.pop3.port", "995");
	    properties.setProperty("mail.pop3.auth", "true");
	    properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    
	    // send
	    properties.setProperty("mail.smtp.host", "smtp.gmail.com");
	    properties.setProperty("mail.smtp.auth", "true");
	    properties.setProperty("mail.smtp.port", "465");
	    properties.setProperty("mail.smtp.socketFactory.port", "465");
	    properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    properties.setProperty("mail.smtp.socketFactory.fallback", "false");

	    return Session.getInstance(properties, new javax.mail.Authenticator() {
	    	
	    	@Override
	    	protected PasswordAuthentication getPasswordAuthentication() {
	    		return new PasswordAuthentication(properties.getProperty("mail.pop3.user"), properties.getProperty("mail.pop3.password"));
	    	}
	    	
	    });
	}
	
	public static void sendMail(Session session, String recipient, String subject, String content) throws MessagingException {
		Message message = new MimeMessage(session);

		InternetAddress addressTo = new InternetAddress(recipient);
		message.setRecipient(Message.RecipientType.TO, addressTo);

		message.setSubject(subject);
		message.setContent(content, "text/plain");
		Transport.send(message);
	}
	
	public static void sendMultipartTextAndHtmlMail(Session session, String recipient, String subject, String textContent, String htmlContent) throws MessagingException {
		MimeMultipart content = new MimeMultipart("alternative");

		MimeBodyPart text = new MimeBodyPart();
		text.setContent(textContent, "text/text");
		content.addBodyPart(text);

		MimeBodyPart html = new MimeBodyPart();
		html.setContent(htmlContent, "text/html");
		content.addBodyPart(html);

		Message message = new MimeMessage(session);

		InternetAddress addressTo = new InternetAddress(recipient);
		message.setRecipient(Message.RecipientType.TO, addressTo);

		message.setSubject(subject);
		message.setContent(content);
		Transport.send(message);
	}
}
