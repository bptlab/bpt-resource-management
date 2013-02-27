package de.uni_potsdam.hpi.bpt.resource_management.mail;

import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.Session;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;

public class BPTMailProvider {
	
	private static final Session session = BPTMailUtils.getGMailSession("bptresourcemanagement@gmail.com", "petrinet");
	private static final BPTUserRepository userRepository = new BPTUserRepository();
	private static final String newLine = System.getProperty("line.separator");
	private static final String applicationURL = ResourceBundle.getBundle("de.uni_potsdam.hpi.bpt.resource_management.bptrm").getString("OPENID_RETURN_TO");
	
	private static void sendMail(String recipient, String subject, String content) {
		try {
			BPTMailUtils.sendMail(session, recipient, subject, content);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	private static void sendMultipartTextAndHtmlMail(Session session, String recipient, String subject, String textContent, String htmlContent) {
		try {
			BPTMailUtils.sendMultipartTextAndHtmlMail(session, recipient, subject, textContent, htmlContent);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	

	public static void sendEmailForNewEntry(String toolName, String documentId, String userId) {
		
		String subject = "[BPTrm] New entry: " + toolName + " (" + documentId + ")";
		
		List<Map> moderators = userRepository.getModerators();
		Map<String, Object> user = userRepository.getUser(userId);
		
		for (Map<String, Object> moderator : moderators) {
			
			String recipient = (String) moderator.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
			content.append("A new entry has been submitted by " + user.get("name") + " <" + user.get("mail_address") + ">. " + newLine);
			content.append("As a moderator you may publish or reject it on " + applicationURL + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
			
			sendMail(recipient, subject, content.toString());
		}
		
	}
	
	public static void sendEmailForUpdatedEntry(String toolName, String documentId, String userId) {
		
		String subject = "[BPTrm] Updated entry: " + toolName + " (" + documentId + ")";

		List<Map> moderators = userRepository.getModerators();
		Map<String, Object> user = userRepository.getUser(userId);
		
		for (Map<String, Object> moderator : moderators) {
			
			String recipient = (String) moderator.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
			content.append("An entry has been updated by " + user.get("name") + " <" + user.get("mail_address") + ">. " + newLine);
			content.append("As a moderator you may publish or reject it on " + applicationURL + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
			
			BPTMailProvider.sendMail(recipient, subject, content.toString());
		}
		
	}
	


	public static void sendEmailForDeletedEntryToResourceProvider(String toolName, String userId) {
		
		String subject = "[BPTrm] Deleted entry: " + toolName;

		Map<String, Object> resourceProvider = userRepository.getUser(userId);
			
		String recipient = (String) resourceProvider.get("mail_address");
		
		StringBuilder content = new StringBuilder();
		content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
		content.append("Your entry '" + toolName + "' has been deleted by one of the moderators." + newLine + newLine);
		content.append("Regards" + newLine);
		content.append("-- bpm-conference.org" + newLine + newLine);
		content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
		
		BPTMailProvider.sendMail(recipient, subject, content.toString());
		
	}

	public static void sendEmailForDeletedEntryToModerator(String toolName, String documentId, String userId) {

		String subject = "[BPTrm] Deleted entry: " + toolName + " (" + documentId + ")";

		List<Map> moderators = userRepository.getModerators();
		Map<String, Object> user = userRepository.getUser(userId);
		
		for (Map<String, Object> moderator : moderators) {
			
			String recipient = (String) moderator.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
			content.append("The entry " + toolName + " has been deleted by its resource provider. " + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
			
			BPTMailProvider.sendMail(recipient, subject, content.toString());
		}
		
	}

	public static void sendEmailForPublishedEntry(String toolName, String userId) {
		
		String subject = "[BPTrm] Published entry: " + toolName;

		Map<String, Object> resourceProvider = userRepository.getUser(userId);
			
		String recipient = (String) resourceProvider.get("mail_address");
		
		StringBuilder content = new StringBuilder();
		content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
		content.append("Your entry '" + toolName + "' has been published by one of the moderators and is now visible to everyone. " + newLine);
		content.append("As a resource provider you may unpublish, edit or delete it on " + applicationURL + "." + newLine + newLine);
		content.append("Regards" + newLine);
		content.append("-- bpm-conference.org" + newLine + newLine);
		content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
		
		BPTMailProvider.sendMail(recipient, subject, content.toString());
		
	}
	
	public static void sendEmailForRejectedEntry(String toolName, String userId) {
		
		String subject = "[BPTrm] Rejected entry: " + toolName;

		Map<String, Object> resourceProvider = userRepository.getUser(userId);
			
		String recipient = (String) resourceProvider.get("mail_address");
		
		StringBuilder content = new StringBuilder();
		content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
		content.append("Your entry '" + toolName + "' has been rejected by one of the moderators." + newLine);
		content.append("As a resource provider you may edit (to request for approval again) or delete it on " + applicationURL + "." + newLine + newLine);
		content.append("Regards" + newLine);
		content.append("-- bpm-conference.org" + newLine + newLine);
		content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
		
		BPTMailProvider.sendMail(recipient, subject, content.toString());
		
	}

	public static void sendEmailForUnpublishedEntryFromPublished(String toolName, String userId) {
		
		String subject = "[BPTrm] Unpublished entry: " + toolName;

		Map<String, Object> resourceProvider = userRepository.getUser(userId);
			
		String recipient = (String) resourceProvider.get("mail_address");
		
		StringBuilder content = new StringBuilder();
		content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
		content.append("Your entry '" + toolName + "' has been unpublished by one of the moderators." + newLine);
		content.append("As a resource provider you may edit or delete it on " + applicationURL + "." + newLine + newLine);
		content.append("Regards" + newLine);
		content.append("-- bpm-conference.org" + newLine + newLine);
		content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
		
		BPTMailProvider.sendMail(recipient, subject, content.toString());
		
	}

	public static void sendEmailForUnpublishedEntryFromRejected(String toolName, String userId) {
		
		String subject = "[BPTrm] Proposed entry: " + toolName;

		Map<String, Object> resourceProvider = userRepository.getUser(userId);
			
		String recipient = (String) resourceProvider.get("mail_address");
		
		StringBuilder content = new StringBuilder();
		content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
		content.append("Your entry '" + toolName + "' was rejected by mistake but is now unpublished again." + newLine);
		content.append("As a resource provider you may edit or delete it on " + applicationURL + "." + newLine + newLine);
		content.append("Regards" + newLine);
		content.append("-- bpm-conference.org" + newLine + newLine);
		content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
		
		BPTMailProvider.sendMail(recipient, subject, content.toString());
		
	}

}
