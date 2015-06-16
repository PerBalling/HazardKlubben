package pbi.hazard.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTime {
	final static String dFormat = "dd. MMM yyyy HH:mm";
	final static String dsFormat = "yyyyMMdd";
	final static String tFormat = "HH:mm:ss";
	final static String csvFormat = "dd-MM-yyyy HH:mm:ss";
	final static String dtFormat = "ddHHmmss";
	final public static String searchFormat = "dd-MM-yyyy";
	final static String sqlFormat = "yyyy-MM-dd HH:mm:ss";

	public static String formatDate(Date d) {
		// SimpleDateFormat df = new SimpleDateFormat();
		// df.applyPattern(dFormat);
		// return (d == null ? "-" : df.format(d));
		return formatDate(d, "-");
	}

	public static String formatDate(Date d, String exept) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern(dFormat);
		return (d == null ? exept : df.format(d));
	}

	public static String formatTime(Date d) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern(tFormat);
		return (d == null ? "-" : df.format(d));
	}

	public static String formatDateToSort(Date d) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern(dsFormat);
		return (d == null ? "-" : df.format(d));
	}

	public static String formatDateToCsv(Date d) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern(csvFormat);
		return (d == null ? "" : df.format(d));
	}

	public static String formatDayTime(Date d) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern(dtFormat);
		return (d == null ? "" : df.format(d));
	}

	public static String formatDateSearch(Date d) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern(searchFormat);
		return (d == null ? "" : df.format(d));
	}

	public static String formatSqlDate(Date d) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern(sqlFormat);
		return (d == null ? "" : df.format(d));
	}

	/**
	 * Compare only Date part (HH, MM, etc. is ignored)
	 * 
	 * @param date1
	 * @param date2
	 * @return negative value if date1&lt;date2<br>
	 *         0 if date1=date2<br>
	 *         positive value if date1&gt;date2
	 * @see Calendar#compareTo(Calendar)
	 */
	public static int compareDates(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return -2;
		}
		Calendar c1 = resetTime(date1);
		// System.out.println(c1.getTimeInMillis() + "\t" + formatSqlDate(date1));

		Calendar c2 = resetTime(date2);
		// System.out.println(c2.getTimeInMillis() + "\t" + formatSqlDate(date2));

		// System.out.println(c1.compareTo(c2));

		return c1.compareTo(c2);
	}

	/**
	 * 
	 * @param date
	 * @return The date part (reset time part)
	 */
	public static Calendar resetTime(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0); // HOUR_OF_DAY is used for the 24-hour clock
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}
}
