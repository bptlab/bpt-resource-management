package de.uni_potsdam.hpi.bpt.resource_management;

import java.io.File;

public class BPTUtils {

	public static String getTempFolder() {
		return File.separator + "usr" + File.separator + "share" + File.separator + "tomcat7" + File.separator + "webapps" + File.separator + "temp";
//		return "C:" + File.separator + "temp";
	}
}
