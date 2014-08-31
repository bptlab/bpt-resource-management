package de.uni_potsdam.hpi.bpt.resource_management.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.Session;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;

/**
 * Defines all mails to be sent.
 * 
 * @author tw
 *
 */
@SuppressWarnings("rawtypes")
public class BPTMailProvider {
	
	private static BPTMailProvider instance = null;
	private final Session session = BPTMailUtils.getGMailSession("bptresourcemanagement@gmail.com", "petrinet");
	private final BPTUserRepository userRepository = BPTUserRepository.getInstance();
	private final String newLine = System.getProperty("line.separator");
	private final String applicationURL = ResourceBundle.getBundle("de.uni_potsdam.hpi.bpt.resource_management.bptrm").getString("OPENID_RETURN_TO");
	private boolean enabled;
	
	public BPTMailProvider() {
		this.enabled = false;
	}
	
	public static BPTMailProvider getInstance() {
		if (instance == null) {
                instance = new BPTMailProvider();
            }		
		return instance;
	}
	
	public static boolean instanceIsCleared() {
		return instance == null;
	}
	
	public static void clearInstance() {
		instance = null;
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
	
	/**
	 * Notifies the moderators about a new entry.
	 * 
	 * @param toolName name of the new entry
	 * @param documentId id of the new entry
	 * @param userId id of the user submitting the new entry
	 */
	public void sendEmailForNewEntry(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] New entry: " + toolName + " (" + documentId + ")";
			
			List<Map> moderators = userRepository.getModerators();
			Map<String, Object> user = userRepository.getUser(userId);
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
				
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				content.append("A new entry has been submitted by " + user.get("name") + " <" + user.get("mail_address") + "> and awaits approval: " + newLine + newLine);
				content.append(toolName + " (" + documentId + ")" + newLine + newLine);
				content.append("As a moderator you may publish, reject or delete it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}
	
	/**
	 * Notifies the moderators about an entry update.
	 * 
	 * @param toolName name of the entry that has been updated
	 * @param documentId id of the entry that has been updated
	 * @param userId id of the entry that has been updated
	 */
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
				content.append("An entry has been updated by " + user.get("name") + " <" + user.get("mail_address") + ">. " + newLine + newLine);
				content.append(toolName + " (" + documentId + ")" + newLine + newLine);
				content.append("As a moderator you may have a look at it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}
	
	/**
	 * Notifies the user about the deletion of his entry by a moderator.
	 * 
	 * @param toolName name of the entry that has been deleted
	 * @param userId id of the user who submitted the entry
	 */
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
			
			sendMail(recipient, subject, content.toString());
		}	
	}
	
	/**
	 * Notifies the moderators about the deletion of an entry by its provider.
	 * 
	 * @param toolName name of the entry that has been deleted
	 * @param documentId id of the entry that has been deleted
	 * @param userId id of the provider
	 */
	public void sendEmailForDeletedEntryToModerator(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] Deleted entry: " + toolName + " (" + documentId + ")";

			List<Map> moderators = userRepository.getModerators();
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
				
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				content.append("An entry has been deleted by its resource provider: " + newLine + newLine);
				content.append(toolName + " (" + documentId + ")" + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}
	
	/**
	 * Notifies a user that his entry has been published by a moderator.
	 * 
	 * @param toolName name of the entry that has been published
	 * @param documentId id of the entry that has been deleted
	 * @param userId id of the user whose entry has been published
	 */
	public void sendEmailForPublishedEntry(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] Published entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been published by one of the moderators and is now visible to everyone. " + newLine);
			content.append("As a resource provider you may unpublish, edit or delete it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			
			sendMail(recipient, subject, content.toString());
		}
	}

	/**
	 * Notifies a user that his entry has been rejected by a moderator.
	 * 
	 * @param toolName name of the entry that has been rejected
	 * @param documentId id of the entry that has been rejected
	 * @param userId id of the user whose entry has been rejected
	 * @param reason reason for rejection
	 */
	public void sendEmailForRejectedEntry(String toolName, String documentId, String userId, String reason) {
		if (enabled) {
			String subject = "[Tools for BPM] Rejected entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been rejected by one of the moderators." + newLine + newLine);
			content.append("Reason: " + reason + newLine + newLine);
			content.append("As a resource provider you may edit (to request for approval again) or delete it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			
			sendMail(recipient, subject, content.toString());
		}
	}
	
	/**
	 * Notifies a user that his entry has moved from published to unpublished by a moderator.
	 * 
	 * @param toolName name of the entry that has been unpublished
	 * @param userId id of the user whose entry has been unpublished
	 */
	public void sendEmailForUnpublishedEntryFromPublishedToResourceProvider(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] Unpublished entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been unpublished by one of the moderators." + newLine);
			content.append("As a resource provider you may edit (to request for approval again) or delete it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			
			sendMail(recipient, subject, content.toString());
		}		
	}
	
	/**
	 * Notifies the moderators that an user has moved his entry from published to unpublished.
	 * 
	 * @param toolName name of the entry that has been unpublished
	 * @param documentId id of the entry that has been unpublished
	 */
	public void sendEmailForUnpublishedEntryFromPublishedToModerator(String toolName, String documentId) {
		if (enabled) {
			String subject = "[Tools for BPM] Unpublished entry: " + " (" + documentId + ")";

			List<Map> moderators = userRepository.getModerators();
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
			
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				content.append("An entry has been unpublished by its resource provider: " + newLine + newLine);
				content.append(toolName + " (" + documentId + ")" + newLine + newLine);
				content.append("As a moderator you may publish, reject or delete it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}
	
	/**
	 * Notifies a user that his entry has moved from rejected to unpublished by a moderator.
	 * 
	 * @param toolName name of the entry that has been unpublished
	 * @param documentId id of the entry that has been unpublished
	 * @param userId id of the user whose entry has been unpublished
	 */
	public void sendEmailForUnpublishedEntryFromRejected(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] Proposed entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' was rejected by mistake but is now unpublished again." + newLine);
			content.append("As a resource provider you may edit or delete it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			
			sendMail(recipient, subject, content.toString());
		}
	}
	
	/**
	 * Notifies a user that his entry contains broken URLs.
	 * 
	 * @param toolName name of the entry containing broken URLs
	 * @param documentId id of the entry containing broken URLs
	 * @param userId id of the user whose entry contains broken URLs
	 */
	public void sendEmailForPublishedEntryWithUnavailableUrls(String toolName, String documentId, String userId, Set<String> unavailableURLs) {
		if (enabled) {
			String subject = "[Tools for BPM] Entry with broken URLs: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
			
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' contains one or several URLs pointing to resources that are currently not available:" + newLine + newLine);
			for (String url : unavailableURLs) {
				content.append(url + newLine);
			}
			content.append(newLine);
			content.append("As a resource provider you may have a look at it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine);
			content.append("Please note that your entry will be unpublished automatically if the URLs are still unavailable in the next two weeks." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			
			sendMail(recipient, subject, content.toString());
		}
	}
	
	/**
	 * Notifies a user that his entry has been unpublished since it contains broken URLs.
	 * 
	 * @param toolName name of the entry containing broken URLs
	 * @param documentId id of the entry containing broken URLs
	 * @param userId id of the user whose entry contains broken URLs
	 */
	public void sendEmailForUnpublishedEntryWithUnavailableURLsToResourceProvider(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] Unpublished entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
				
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been unpublished automatically since it contains one or several URLs pointing to resources that are currently not available." + newLine);
			content.append("As a resource provider you may have a look at it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine);
			content.append("We would like to keep our site up to date and kindly ask you to update your entry. It will be republished then by one of the moderators as soon as possible." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			
			sendMail(recipient, subject, content.toString());
		}	
	}
	
	/**
	 * Notifies the moderators that an entry has been unpublished since it contains broken URLs.
	 * 
	 * @param toolName name of the entry containing broken URLs
	 * @param documentId id of the entry containing broken URLs
	 * @param userId id of the user whose entry contains broken URLs
	 */
	public void sendEmailForUnpublishedEntryWithUnavailableURLsToModerator(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] Unpublished entry: " + " (" + documentId + ")";

			List<Map> moderators = userRepository.getModerators();
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
			
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				content.append("An entry has been unpublished automatically since it contains one or several URLs pointing to resources that are currently not available: " + newLine + newLine);
				content.append(toolName + " (" + documentId + ")" + newLine + newLine);
				content.append("As a moderator you may publish, reject or delete it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}
	
	/**
	 * Notifies the moderators about entries containing broken URLs.
	 * 
	 * @param documentsWithUnavailableURLs java.util.Map mapping the names of the entries to lists of broken URLs
	 */
	public void sendSummaryForURLCheck(Map<String, Set<String>> documentsWithUnavailableURLs) {
		if (enabled) {
			String subject = "[Tools for BPM] One or several entries contain broken URLs";

			List<Map> moderators = userRepository.getModerators();
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
				
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				content.append("One or several entries contain URLs pointing to resources that are currently not available:" + newLine + newLine);
				for (String documentNameAndId : documentsWithUnavailableURLs.keySet()) {
					int separator = documentNameAndId.indexOf("(");
					String toolName = documentNameAndId.substring(0, separator - 1);
					String documentId = documentNameAndId.substring(separator + 1, documentNameAndId.length() - 1);
					content.append(documentNameAndId + " | " + applicationURL + getFragmentPart(documentId, toolName) + newLine);
					for (String url : documentsWithUnavailableURLs.get(documentNameAndId)) {
						content.append("* " + url + newLine);
					}
					content.append(newLine);
				}
				content.append("As a moderator you may have a look at it on " + applicationURL + "." + newLine + newLine);
				content.append("Please note that an entry will be unpublished automatically " +
						"after two weeks of the first dectection of an URL pointing to unavailable resources." + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}
	
	/**
	 * Notifies a user that his entry has been updated 365 days ago.
	 * 
	 * @param toolName name of the entry that has been updated 365 days ago
	 * @param documentId id of the entry that has been updated 365 days ago
	 * @param userId id of the user whose entry has been updated 365 days ago
	 */
	public void sendFirstEmailForOldEntry(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] Your entry might be out of date: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
			
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been last updated one year ago. " + newLine);
			content.append("We would like to keep our site up to date and encourage you to have a look at your entry on " + applicationURL + getFragmentPart(documentId, toolName) + " and update its content if necessary. Thank you for your cooperation." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			
			sendMail(recipient, subject, content.toString());
		}
	}
	
	/**
	 * Notifies a user that his entry has been updated 700 days ago.
	 * 
	 * @param toolName name of the entry that has been updated 700 days ago
	 * @param documentId id of the entry that has been updated 700 days ago
	 * @param userId id of the user whose entry has been updated 700 days ago
	 */
	public void sendSecondEmailForOldEntry(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] Your entry might be out of date: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
			
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been last updated 23 months ago" + newLine);
			content.append("We would like to keep our site up to date and encourage you to have a look at your entry on " + applicationURL + getFragmentPart(documentId, toolName) + " and update its content if necessary." + newLine);
			content.append("If no content update is necessary, we kindly ask you to open the entry for editing and click on 'Submit' to prevent your entry from becoming unpublished automatically in one month. Thank you for your cooperation." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			
			sendMail(recipient, subject, content.toString());
		}
	}
	
	/**
	 * Notifies a user that his entry has been unpublished since it has been updated more than 730 days ago.
	 * 
	 * @param toolName name of the entry that has been updated more than 730 days ago
	 * @param documentId id of the entry that has been updated more than 730 days ago
	 * @param userId id of the user whose entry has been updated more than 730 days ago
	 */
	public void sendEmailForUnpublishedEntrySinceItIsTooOld(String toolName, String documentId, String userId) {
		if (enabled) {
			String subject = "[Tools for BPM] Unpublished entry: " + toolName;

			Map<String, Object> resourceProvider = userRepository.getUser(userId);
			
			String recipient = (String) resourceProvider.get("mail_address");
			
			StringBuilder content = new StringBuilder();
			content.append("Hello " + resourceProvider.get("name") + "!" + newLine + newLine);
			content.append("Your entry '" + toolName + "' has been unpublished automatically since it has been last updated two years ago." + newLine);
			content.append("As a resource provider you may have a look at it on " + applicationURL + getFragmentPart(documentId, toolName) + "." + newLine);
			content.append("We would like to keep our site up to date and kindly ask you to open the entry for editing, check if its content is still up to date and click on 'Submit'. The entry will be republished then by one of the moderators as soon as possible." + newLine + newLine);
			content.append("Regards" + newLine);
			content.append("-- bpm-conference.org" + newLine + newLine);
			
			sendMail(recipient, subject, content.toString());
		}
	}
	
	/**
	 * Notifies the moderators about entries that have been updated a long time ago.
	 * 
	 * @param namesOfOldDocuments list of names of entries that have been updated a long time ago
	 */
	public void sendSummaryForOldEntriesCheck(Map<String, Integer> namesOfOldDocuments) {
		if (enabled) {
			
			List<String> namesOfDocumentsWithFirstNotification = new ArrayList<String>();
			List<String> namesOfDocumentsWithSecondNotification = new ArrayList<String>();
			List<String> namesOfDocumentsWithThirdNotification = new ArrayList<String>();
			
			for (String documentNameAndId : namesOfOldDocuments.keySet()) {
				if (namesOfOldDocuments.get(documentNameAndId) == 1) {
					namesOfDocumentsWithFirstNotification.add(documentNameAndId);
				} else if (namesOfOldDocuments.get(documentNameAndId) == 2) {
					namesOfDocumentsWithSecondNotification.add(documentNameAndId);
				} else if (namesOfOldDocuments.get(documentNameAndId) == 3) {
					namesOfDocumentsWithThirdNotification.add(documentNameAndId);
				}
			}
			
			String subject = "[Tools for BPM] One or several entries are out of date";

			List<Map> moderators = userRepository.getModerators();
			
			for (Map<String, Object> moderator : moderators) {
				
				String recipient = (String) moderator.get("mail_address");
				
				StringBuilder content = new StringBuilder();
				content.append("Hello " + moderator.get("name") + "!" + newLine + newLine);
				if (!namesOfDocumentsWithFirstNotification.isEmpty()) {
					content.append("A first notification has been sent to the providers of the following published entries that have been last updated more than one year ago:" + newLine + newLine);
					for (String documentNameAndId : namesOfDocumentsWithFirstNotification) {
						content.append(documentNameAndId + newLine);
					}
					content.append(newLine);
				}
				if (!namesOfDocumentsWithSecondNotification.isEmpty()) {
					content.append("A second notification has been sent to the providers of the following published entries that have been last updated more than 23 months ago:" + newLine + newLine);
					for (String documentNameAndId : namesOfDocumentsWithFirstNotification) {
						content.append(documentNameAndId + newLine);
					}
					content.append(newLine);
				}
				if (!namesOfDocumentsWithThirdNotification.isEmpty()) {
					content.append("A third notification has been sent to the providers of the following published entries that have been last updated more than 2 years ago:" + newLine + newLine);
					for (String documentNameAndId : namesOfDocumentsWithFirstNotification) {
						content.append(documentNameAndId + newLine);
					}
					content.append(newLine);
				}
				content.append("Please note that an entry will be unpublished automatically after two years if the entry is not updated." + newLine + newLine);
				content.append("Regards" + newLine);
				content.append("-- bpm-conference.org" + newLine + newLine);
				
				sendMail(recipient, subject, content.toString());
			}
		}
	}
	
	private String getFragmentPart(String documentId, String toolName) {
		return  "#!" + documentId + "-" + toolName.replaceAll("[^\\w]", "-").toLowerCase();
	}
}
