package de.uni_potsdam.hpi.bpt.resource_management.mail;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.Session;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;

@SuppressWarnings("rawtypes")
public class BPTMailProvider {
	
	private final Session session = BPTMailUtils.getGMailSession("bptresourcemanagement@gmail.com", "petrinet");
	private final BPTUserRepository userRepository = new BPTUserRepository();
	private final String newLine = System.getProperty("line.separator");
	private final String applicationURL = ResourceBundle.getBundle("de.uni_potsdam.hpi.bpt.resource_management.bptrm").getString("OPENID_RETURN_TO");
	private boolean enabled;
	
	public BPTMailProvider() {
		this.enabled = false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private void sendMail(String recipient, String subject, String content) {
		try {
			BPTMailUtils.sendMail(session, recipient, subject, content);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
//	private void sendMultipartTextAndHtmlMail(Session session, String recipient, String subject, String textContent, String htmlContent) {
//		try {
//			BPTMailUtils.sendMultipartTextAndHtmlMail(session, recipient, subject, textContent, htmlContent);
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}
//	}

	public void sendEmailForNewEntry(String toolName, String documentId, String userId) {
		
		if (enabled) {
			String subject = "[Tools for BPM] New entry: " + toolName + " (" + documentId + ")";
			
			List<Map> moderators = userRepository.getModerators();
			Map<String, Object> user = userRepository.getUser(userId);
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
				
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				content.append("A new entry has been submitted by " + user.get("name") + " <" + user.get("mail_address") + "> and awaits approval. " + newLine);
				content.append("As a moderator you may publish, reject or delete it on " + applicationURL + "." + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}
	
	public void sendEmailForUpdatedEntry(String toolName, String documentId, String userId) {
		// will not be sent if entry is (still) unpublished
		if (enabled) {
			String subject = "[Tools for BPM] Updated entry: " + toolName + " (" + documentId + ")";

			List<Map> moderators = userRepository.getModerators();
			Map<String, Object> user = userRepository.getUser(userId);
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
				
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				content.append("An entry has been updated by " + user.get("name") + " <" + user.get("mail_address") + ">. " + newLine);
				content.append("As a moderator you may have a look at it on " + applicationURL + "." + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}
	
	public void sendEmailForDeletedEntryToResourceProvider(String toolName, String userId) {
		
		if (enabled) {
			String subject = "[Tools for BPM] Deleted entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been deleted by one of the moderators." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
			
			sendMail(recipient, subject, content.toString());
		}	
	}

	public void sendEmailForDeletedEntryToModerator(String toolName, String documentId, String userId) {

		if (enabled) {
			String subject = "[Tools for BPM] Deleted entry: " + toolName + " (" + documentId + ")";

			List<Map> moderators = userRepository.getModerators();
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
				
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				content.append("The entry " + toolName + " has been deleted by its resource provider. " + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}

	public void sendEmailForPublishedEntry(String toolName, String userId) {
		
		if (enabled) {
			String subject = "[Tools for BPM] Published entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been published by one of the moderators and is now visible to everyone. " + newLine);
			content.append("As a resource provider you may unpublish, edit or delete it on " + applicationURL + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
			
			sendMail(recipient, subject, content.toString());
		}
	}
	
	public void sendEmailForRejectedEntry(String toolName, String userId) {
		
		if (enabled) {
			String subject = "[Tools for BPM] Rejected entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been rejected by one of the moderators." + newLine);
			content.append("As a resource provider you may edit (to request for approval again) or delete it on " + applicationURL + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
			
			sendMail(recipient, subject, content.toString());
		}
	}

	public void sendEmailForUnpublishedEntryFromPublishedToResourceProvider(String toolName, String userId) {
		
		if (enabled) {
			String subject = "[Tools for BPM] Unpublished entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been unpublished by one of the moderators." + newLine);
			content.append("As a resource provider you may edit (to request for approval again) or delete it on " + applicationURL + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
			
			sendMail(recipient, subject, content.toString());
		}		
	}
	
	public void sendEmailForUnpublishedEntryFromPublishedToModerator(String toolName, String documentId, String userId) {
		
		if (enabled) {
			String subject = "[Tools for BPM] Unpublished entry: " + " (" + documentId + ")";

			List<Map> moderators = userRepository.getModerators();
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
			
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				content.append("The entry '" + toolName + "' has been unpublished by its resource provider." + newLine);
				content.append("As a moderator you may publish, reject or delete it on " + applicationURL + "." + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}

	public void sendEmailForUnpublishedEntryFromRejected(String toolName, String userId) {
		
		if (enabled) {
			String subject = "[Tools for BPM] Proposed entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' was rejected by mistake but is now unpublished again." + newLine);
			content.append("As a resource provider you may edit or delete it on " + applicationURL + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			content.append("THIS IS AN AUTOMATICALLY GENERATED EMAIL. DO NOT REPLY!");
			
			sendMail(recipient, subject, content.toString());
		}
	}

}
