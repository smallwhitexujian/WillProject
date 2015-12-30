package net.dev.mylib.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;

/**
 * shanli 2015-10-11  日期类
 */
public class DateUtil {

    public static String FormatString = "yyyy-MM-dd";

    //格式化
    public static String DateFormat(long value, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());

        return format.format(new Date(value));
    }

    //格式化
    public static String DateFormat(Date value, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString,
                Locale.getDefault());
        return format.format(value);
    }

    //获取当前的小时
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
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");

            Date Userdate = new Date(value); //new Date(value);

            //Date d;

           // d = format.parse(Userdate.getYear() + "-" + Userdate.getMonth()
            //        + "-" + Userdate.getDay());

            Date now=new Date();
            //Date nowLong = new Date(System.currentTimeMillis());

            //Date now = format.parse(nowLong.getYear() + "-"
            //        + nowLong.getMonth() + "-" + nowLong.getDay());
            //long datadiff = now.getTime() - Userdate.getTime();

            //int diff = (int) (datadiff / (1000 * 60 * 60 * 24));

            int diff=2;
            if(now.getYear()==Userdate.getYear()&&now.getMonth()==Userdate.getMonth()){
                if(now.getDay()==Userdate.getDay()){
                    diff=0;
                }else{
                    diff=1;
                }
            }
            if (diff < 1) {
                return DateFormat(Userdate, "H:mm");
//                if (getDate12_24(c) > 12) {
//                    return DateFormat(Userdate, "HH:mm");
//                } else {
//                    if (Userdate.getHours() >= 12) {
//                        return "PM " + DateFormat(Userdate, "hh:mm");
//                    } else {
//
//                        return "AM " + DateFormat(Userdate, "hh:mm");
//                    }
//                }
            } else if (diff == 1) {
                //"last "+
                return DateFormat(Userdate,"M-d H:mm");
            } else {
                return DateFormat(Userdate, "M-d H:mm");
            }

        } catch (Exception e) {
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
                        return "AM " + DateFormat(value, "h:mm");
                    } else {

                        return "PM " + DateFormat(value, "h:mm");
                    }
                }
            } else {
                if (getDate12_24(c) > 12) {
                    return DateFormat(value, "MM-dd H:mm");
                } else {

                    if (Userdate.getHours() >= 12) {
                        return DateFormat(value, "MM-dd") + "PM "
                                + DateFormat(value, "h:mm");
                    } else {

                        return DateFormat(value, "MM-dd") + "AM "
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

    //获取当前日期于时间
    public static String GetDateTimeNow() {
        return DateFormat(GetDateTimeNowlong());
    }

    //获取当前日期于时间
    public static String GetDateTimeNow(String fomat) {
        return DateFormat(GetDateTimeNowlong(), fomat);
    }

    //获取当前日期于时间
    public static long GetDateTimeNowlong() {
        return System.currentTimeMillis();
    }

    //把日期转为字符串
    public static String ConverToString(Date date) {
        DateFormat df = new SimpleDateFormat(FormatString);

        return df.format(date);
    }

    //把字符串转为日期
    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat(FormatString);
        return df.parse(strDate);
    }

    //把字符串转为日期
    public static Date ConverToDate(String strDate, String format) throws Exception {
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(strDate);
    }

    public static boolean overMinute(long datetime,int min){
        int rate = 60 * 1000;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(datetime);
        if(((cal1.getTimeInMillis() - cal2.getTimeInMillis())/rate) > min){
            return true;
        }
        return false;
    }

    public static long getCurrentUnixTime(){
        return System.currentTimeMillis()/1000;
    }
}
