package com.jivecast.ctgdb.app;

import org.apache.commons.lang3.StringUtils;

public class Util {
	/**
	 * Clean content for CSV import
	 * 
	 * @param content
	 * @return
	 */
	public static String cleanAndQuoteContent(String content) {

		if (!StringUtils.isEmpty(content)) {
			// check for null entries
			if (content.toLowerCase().equals("null")) {
				return "";
			} else {
				content = content.replace("\"", "'");
				content = content.replace("\0", "");
				content = content.replace("\n", " ");
				content = content.replace("\r", " ");

				// replace multiple white space with single
				content = content.trim().replaceAll(" +", " ");
				return "\"" + content + "\"";
			}
		} else {
			return "";
		}
	}
}
