package de.uni_potsdam.hpi.bpt.resource_management.mail;

import javax.mail.MessagingException;
import javax.mail.Session;

public class BPTMailProvider {
	
	private static Session session = BPTMailUtils.getGMailSession("bptresourcemanagement@gmail.com", "petrinet");
	
	public static void sendMail(String recipient, String subject, String content) {
		try {
			BPTMailUtils.sendMail(session, recipient, subject, content);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMultipartTextAndHtmlMail(Session session, String recipient, String subject, String textContent, String htmlContent) {
		try {
			BPTMailUtils.sendMultipartTextAndHtmlMail(session, recipient, subject, textContent, htmlContent);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
