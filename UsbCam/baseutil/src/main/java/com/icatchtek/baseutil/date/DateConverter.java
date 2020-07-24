package com.icatchtek.baseutil.date;

import com.icatchtek.baseutil.log.AppLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by zengping on 2017/6/7.
 */

public class DateConverter {
    private static final String TAG = DateConverter.class.getSimpleName();
    private static final String TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private static final String DAY_FORMAT = "yyyy/MM/dd";
    private static final String MONTH_FORMAT = "yyyy/MM";
    //2018-05-31
//    private static final String MONTH_FORMAT = "yyyy-MM";
    private static final String ACCOUNT_SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String FILE_NAME_FORMAT = "yyyyMMdd_HHmmss";

    public static boolean areSameDay( Date dateA, Date dateB ) {
        Calendar calA = Calendar.getInstance();
        calA.setTime( dateA );

        Calendar calB = Calendar.getInstance();
        calB.setTime( dateB );

        return calA.get( Calendar.YEAR ) == calB.get( Calendar.YEAR ) &&
                calA.get( Calendar.MONTH ) == calB.get( Calendar.MONTH ) &&
                calA.get( Calendar.DAY_OF_MONTH) == calB.get( Calendar.DAY_OF_MONTH );
    }

    public static Date timeStr2Date(String datetimeStr ) {
        AppLog.d( TAG, "timeStr2Date: " + datetimeStr );
        SimpleDateFormat format = new SimpleDateFormat( TIME_FORMAT );
        try {
            Date date = format.parse(datetimeStr);
            return date;
        }catch( Exception e ) {
            AppLog.e( TAG, "timeStr2Date error" );
            return null;
        }
    }

    public static Date dayStr2Date( String dayStr ) {
        AppLog.d( TAG, "dayStr2Date: " + dayStr );
        SimpleDateFormat format = new SimpleDateFormat( DAY_FORMAT );
        try {
            Date date = format.parse(dayStr);
            return date;
        }catch( Exception e ) {
            AppLog.e( TAG, "dayStr2Date error" );
            return null;
        }
    }

    public static Date monthStr2Date( String monthStr ) {
        AppLog.d( TAG, "monthStr2Date: " + monthStr );
        SimpleDateFormat format = new SimpleDateFormat( MONTH_FORMAT );
        try {
            Date date = format.parse(monthStr);
            return date;
        }catch( Exception e ) {
            AppLog.e( TAG, "monthStr2Date error" );
            return null;
        }
    }

    public static String toTimeStr( Date date ) {
        SimpleDateFormat format = new SimpleDateFormat( TIME_FORMAT );
        return format.format( date );
    }

    public static String toDayStr( Date date ) {
        SimpleDateFormat format = new SimpleDateFormat( DAY_FORMAT );
        return format.format( date );
    }

    public static String toMonthStr( Date date ) {
        SimpleDateFormat format = new SimpleDateFormat( MONTH_FORMAT );
        return format.format( date );
    }

    public static  Date getDateBefore( Date date, int days ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        cal.add( Calendar.DAY_OF_YEAR, -( days - 1 ) );
        return cal.getTime();
    }
    public static String timeConvert(String time){
        //2018-03-13T06:37:53.294Z
        //yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
//        formatter.setTimeZone(tz);
        Date date;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return "error format";
        }

//        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format1 = new SimpleDateFormat(TIME_FORMAT);
        String time1 =  format1.format(date.getTime());
        System.out.println("格式化结果1：" + time1);
        return time1;
    }

    public static String timeFormatGMTString(long time) {
        SimpleDateFormat format = new SimpleDateFormat(ACCOUNT_SERVER_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(time));
    }

    public static String timeFormatFileNameString(long time) {
        SimpleDateFormat format = new SimpleDateFormat(FILE_NAME_FORMAT);
        return format.format(new Date(time));
    }
}
