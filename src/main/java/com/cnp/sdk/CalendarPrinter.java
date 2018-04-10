package com.cnp.sdk;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarPrinter {
	private static final SimpleDateFormat XS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat EXP_DATE_FORMAT = new SimpleDateFormat("MM-yy");

	public static String printDate(Calendar val) {
		return XS_DATE_FORMAT.format(val.getTime());
	}

	public static String printExpDate(Calendar val) {
		return EXP_DATE_FORMAT.format(val.getTime());
	}
}
