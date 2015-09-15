package net.dev.mylib.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.Context;

public class DateUtil {

	public static String DateFormat(long value, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString,Locale.getDefault());

		return format.format(new Date(value));
	}

	public static String DateFormat(Date value, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString,
				Locale.getDefault());
		return format.format(value);
	}

	public static int GetNowHours() {
		return new Date(GetDateTimeNowlong()).getHours();
	}

	public static String DateFormat(long value) {
		return DateFormat(value, "yyyy-MM-dd HH:mm:ss");
	}

	// 获得当前系统时间制式
	public static int getDate12_24(Context c) {
		try {
			ContentResolver cv = c.getContentResolver();
			// 获取当前系统设置
			String strTimeFormat = android.provider.Settings.System.getString(
					cv, android.provider.Settings.System.TIME_12_24);
			if (strTimeFormat.equals("24")) {
				return 24;
			}
			if (strTimeFormat.equals("12")) {
				return 12;
			}
			return 12;
		} catch (Exception ex) {
			return 12;
		}
	}

	// 根据日期格式化为近期
	public static String DateFormat2(Context c, long value) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");

			Date Userdate = new Date(value);

			Date d;

			d = format.parse(Userdate.getYear() + "-" + Userdate.getMonth()
					+ "-" + Userdate.getDay());

			Date nowLong = new Date(System.currentTimeMillis());

			Date now = format.parse(nowLong.getYear() + "-"
					+ nowLong.getMonth() + "-" + nowLong.getDay());

			long datadiff = now.getTime() - d.getTime();

			int diff = (int) (datadiff / (1000 * 60 * 60 * 24));
			if (diff < 1) {

				if (getDate12_24(c) > 12) {
					return DateFormat(value, "H:mm");
				} else {
					if (Userdate.getHours() >= 12) {
						return "下午 " + DateFormat(value, "h:mm");
					} else {

						return "上午 " + DateFormat(value, "h:mm");
					}
				}
			} else if (diff == 1) {
				return "昨天";
			} else {
				return DateFormat(value, "MM-dd");
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// 根据日期格式化为近期
	public static String DateFormat3(Context c, long value) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");

			Date Userdate = new Date(value);

			Date d;

			d = format.parse(Userdate.getYear() + "-" + Userdate.getMonth()
					+ "-" + Userdate.getDay());

			Date nowLong = new Date(System.currentTimeMillis());

			Date now = format.parse(nowLong.getYear() + "-"
					+ nowLong.getMonth() + "-" + nowLong.getDay());

			long datadiff = now.getTime() - d.getTime();

			int diff = (int) (datadiff / (1000 * 60 * 60 * 24));
			if (diff < 1) {

				if (getDate12_24(c) > 12) {
					return DateFormat(value, "H:mm");
				} else {

					if (Userdate.getHours() >= 12) {
						return "下午 " + DateFormat(value, "h:mm");
					} else {

						return "上午 " + DateFormat(value, "h:mm");
					}
				}
			}

			else {
				if (getDate12_24(c) > 12) {
					return DateFormat(value, "MM-dd H:mm");
				} else {

					if (Userdate.getHours() >= 12) {
						return DateFormat(value, "MM-dd") + "下午 "
								+ DateFormat(value, "h:mm");
					} else {

						return DateFormat(value, "MM-dd") + "上午 "
								+ DateFormat(value, "h:mm");
					}
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String GetDateTimeNow() {
		return DateFormat(GetDateTimeNowlong());
	}	
	
	public static String GetDateTimeNow(String fomat) {
		return DateFormat(GetDateTimeNowlong(),fomat);
	}	

	public static long GetDateTimeNowlong() {
		return System.currentTimeMillis();
	}
}
