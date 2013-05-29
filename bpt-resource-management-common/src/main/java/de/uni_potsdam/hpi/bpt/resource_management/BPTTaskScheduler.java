package de.uni_potsdam.hpi.bpt.resource_management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentTypes;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.mail.BPTMailProvider;

/**
 * 
 * Scheduler for tasks to be executed on entries stored in CouchDB.
 * 
 * @author tw
 *
 */
@SuppressWarnings({ "rawtypes" })
public class BPTTaskScheduler {
	
	Timer timer = new Timer();
	BPTToolRepository toolRepository = BPTToolRepository.getInstance();
	BPTMailProvider mailProvider = BPTMailProvider.getInstance();
	public static final int DAYS_AFTER_FIRST_NOTIFICATION_TO_UNPUBLISH = 14;
	public static final int EXPIRY_PERIOD_FOR_LAST_UPDATE_IN_DAYS = 90;
	public static final int MAXIMUM_PERIOD_OF_FIRST_EMAIL_FOR_LAST_UPDATE_IN_DAYS = 7;
	public static final int MAXIMUM_PERIOD_OF_SECOND_EMAIL_FOR_LAST_UPDATE_IN_DAYS = 13;
	public static final int MAXIMUM_PERIOD_OF_THIRD_EMAIL_FOR_LAST_UPDATE_IN_DAYS = 14;
	public static final int DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;
	
	/**
	 * Constructor. Two tasks are scheduled for execution:
	 * 
	 * CheckURLsTask - first execution after one hour, repeated every day
	 * CheckForOldEntriesTask - first execution after one hour, repeated every day
	 * 
	 */
	public BPTTaskScheduler() {
		System.out.println("About to schedule tasks ...");
		timer.schedule(new CheckURLsTask(), DAY_IN_MILLISECONDS / 24, DAY_IN_MILLISECONDS);
		timer.schedule(new CheckForOldEntriesTask(), DAY_IN_MILLISECONDS / 24, DAY_IN_MILLISECONDS);
		System.out.println("Tasks scheduled!");
	}
	
	/**
	 * Checks if the URLs in the entries pointing to external resources are valid.
	 * If an entry contains at least one broken URL, the number of unsuccessful URL validations of this entry is increased by one.
	 * If an entry contains at least one broken URL and if it is the third time in a row that at least one broken URL has detected, the entry is marked.
	 * If an entry contains at least one broken URL and the entry has been marked two or more weeks ago, the entry is unpublished automatically.
	 * If an entry does not contain broken URLs, the entry is unmarked and its number of unsuccessful URL validations is set to zero if possible.
	 * A summary listing all marked entries and their broken URLs is sent to the moderators afterwards.
	 * 
	 * @author tw
	 *
	 */
	class CheckURLsTask extends TimerTask {
		public void run() {
			System.out.println(new Date() + " - URL check started ...");
			Map<String, Set<String>> documentsWithUnavailableURLs = new HashMap<String, Set<String>>();
			List<Map> documents = toolRepository.getDocuments("published");
			String[] keys = BPTDocumentTypes.getDocumentKeysStoringURLs("bpt_resources_tools");
			for (Map<String, Object> document : documents) {
				Set<String> unavailableURLs = new HashSet<String>();
				for (String key : keys) {
					String url = (String) document.get(key);
					if (!url.isEmpty() && !BPTValidator.isValidUrl(url)) {
						unavailableURLs.add(url);
					}
				}
				if (!unavailableURLs.isEmpty()) {
					String documentName = (String)document.get("name");
					String documentId = (String)document.get("_id");
					if ((Integer) document.get("number_of_url_validation_fails") < 3) {
						document.put("number_of_url_validation_fails", (Integer) document.get("number_of_url_validation_fails") + 1);
						toolRepository.updateDocument(document);
					} else {
						if (document.get("notification_date") == null) {
							document.put("notification_date", new Date());
							mailProvider.sendEmailForPublishedEntryWithUnavailableUrls((String)document.get("name"), (String)document.get("_id"), (String)document.get("user_id"), unavailableURLs);
							toolRepository.updateDocument(document);
							documentsWithUnavailableURLs.put(documentName + " (" + documentId + ")", unavailableURLs);
						} else {
							try {
								Date now = new Date();
								Date notificationDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse((String) document.get("notification_date"));
								int differenceInDays = (int) ((now.getTime() - notificationDate.getTime()) / DAY_IN_MILLISECONDS);
								if (differenceInDays >= DAYS_AFTER_FIRST_NOTIFICATION_TO_UNPUBLISH) {
									toolRepository.unpublishDocument(documentId, true);
									document.put("number_of_url_validation_fails", 0);
									document.put("notification_date", null);
									toolRepository.updateDocument(document);
									System.out.println("--- Document " + documentId + " unpublished ---");
								} else {
									documentsWithUnavailableURLs.put(documentName + " (" + documentId + ")", unavailableURLs);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					if ((Integer) document.get("number_of_url_validation_fails") > 0) {
						document.put("number_of_url_validation_fails", 0);
						if (document.get("notification_date") != null) {
							document.put("notification_date", null);
						}
						toolRepository.updateDocument(document);
					}
				}
			}
			System.out.println(new Date() + " - URL check finished!");
			if (!documentsWithUnavailableURLs.isEmpty()) {
				System.out.println(new Date() + " - Sending summary of URL check ...");
				mailProvider.sendSummaryForURLCheck(documentsWithUnavailableURLs);
				System.out.println(new Date() + " - Summary of URL check is sent!");
			} else {
				System.out.println(new Date() + " - No broken URLs found.");
			}
		}
	}
	
	/**
	 * Checks when the entries have been last updated.
	 * If an entry has been updated 90 days ago, a first notification is sent to the resource provider.
	 * If an entry has been updated 97 days ago, a second notification is sent to the resource provider.
	 * If an entry has been updated 103 days ago, a third and last notification is sent to the resource provider.
	 * If an entry has been updated 104 days ago, the entry is unpublished automatically.
	 * A summary listing all entries that have been last updated 90 or more days ago is sent to the moderators after the check.
	 * 
	 * @author tw
	 *
	 */
	class CheckForOldEntriesTask extends TimerTask {

		public void run() {
			System.out.println(new Date() + " - Check for old entries started ...");
			List<String> namesOfOldDocuments = new ArrayList<String>();
			List<Map> documents = toolRepository.getDocuments("published");
			try {
				for (Map<String, Object> document : documents) {
					Date now = new Date();
					Date lastUpdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse((String) document.get("last_update"));
					int differenceInDays = (int) ((now.getTime() - lastUpdate.getTime()) / DAY_IN_MILLISECONDS);
					if (differenceInDays >= EXPIRY_PERIOD_FOR_LAST_UPDATE_IN_DAYS) {
						String documentName = (String)document.get("name");
						String documentId = (String)document.get("_id");
						if (differenceInDays < EXPIRY_PERIOD_FOR_LAST_UPDATE_IN_DAYS + MAXIMUM_PERIOD_OF_FIRST_EMAIL_FOR_LAST_UPDATE_IN_DAYS) {
							if ((Integer) document.get("number_of_mails_for_expiry") == 0) {
								mailProvider.sendFirstEmailForOldEntry((String)document.get("name"), (String)document.get("_id"), (String)document.get("user_id"));
								document.put("number_of_mails_for_expiry", 1);
								namesOfOldDocuments.add(documentName + " (" + documentId + ")");
							}
						} else if (differenceInDays < EXPIRY_PERIOD_FOR_LAST_UPDATE_IN_DAYS + MAXIMUM_PERIOD_OF_SECOND_EMAIL_FOR_LAST_UPDATE_IN_DAYS) {
							if ((Integer) document.get("number_of_mails_for_expiry") <= 1) {
								mailProvider.sendSecondEmailForOldEntry((String)document.get("name"), (String)document.get("_id"), (String)document.get("user_id"));
								document.put("number_of_mails_for_expiry", 2);
								namesOfOldDocuments.add(documentName + " (" + documentId + ")");
							}
						} else if (differenceInDays < EXPIRY_PERIOD_FOR_LAST_UPDATE_IN_DAYS + MAXIMUM_PERIOD_OF_THIRD_EMAIL_FOR_LAST_UPDATE_IN_DAYS) {
							if ((Integer) document.get("number_of_mails_for_expiry") <= 2) {
								mailProvider.sendThirdEmailForOldEntry((String)document.get("name"), (String)document.get("_id"), (String)document.get("user_id"));
								document.put("number_of_mails_for_expiry", 3);
								namesOfOldDocuments.add(documentName + " (" + documentId + ")");
							}
						} else if (differenceInDays >= EXPIRY_PERIOD_FOR_LAST_UPDATE_IN_DAYS + MAXIMUM_PERIOD_OF_THIRD_EMAIL_FOR_LAST_UPDATE_IN_DAYS) {
							toolRepository.unpublishDocument(documentId, true);
							document.put("number_of_mails_for_expiry", 0);
							System.out.println("--- Document " + documentId + " unpublished ---");
						}
						toolRepository.updateDocument(document);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			} finally {
				System.out.println(new Date() + " - Check for old entries finished!");
				if (!namesOfOldDocuments.isEmpty()) {
					System.out.println(new Date() + " - Sending summary of check for old entries ...");
					mailProvider.sendSummaryForOldEntriesCheck(namesOfOldDocuments);
					System.out.println(new Date() + " - Summary of check for old entries is sent!");
				} else {
					System.out.println(new Date() + " - All entries are up to date!");
				}
			}
		}
	}
	
//	public static void main(String args[]) {
//		new BPTTaskScheduler();
//	}
}