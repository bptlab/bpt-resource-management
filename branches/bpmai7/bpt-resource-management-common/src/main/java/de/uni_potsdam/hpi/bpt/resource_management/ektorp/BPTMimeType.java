package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

/**
 * Enum for supported MIME types of documents for BPMAI
 * 
 * @author tw
 *
 */
public enum BPTMimeType {
	PDF ("application/pdf"),
	DOC ("application/msword"),
	DOCX ("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
	
	private final String mimeType;
	
	BPTMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
	
	/**
	 * 
	 * @return supported MIME types of documents for BPMAI
	 */
	public static String[] getMimeTypes() {
		String[] mimeTypes = new String[values().length];
		for (int i = 0; i < values().length; i++) {
			mimeTypes[i] = values()[i].toString();
		}
		return mimeTypes;
	}
	
	@Override
	public String toString() {
		return mimeType;
	}
}
