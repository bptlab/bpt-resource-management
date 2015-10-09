package de.uni_potsdam.hpi.bpt.resource_management;

import java.io.File;

public class BPTUtils {

	public static String getTempFolder() {
		return System.getProperty("user.home") + File.separator + "bpmToolsTemp";
//		return "C:" + File.separator + "temp";
	}
}
