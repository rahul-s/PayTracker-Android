package com.creativecapsule.paytracker.Utility;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by soniya on 3/30/15.
 */
public class Common {

    private static final String DEBUG_TAG = "Common";
    private static int statusUnauthorisedRequest  =  401;
    private static int statusUnauthorisedAccess  =  403;

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String MESSAGES_HEADER_DATE_FORMAT_DATE = "MMM/dd/yyyy";
    private final static String MESSAGES_HEADER_DATE_FORMAT_DAY = "cccc";
    private final static String MESSAGES_HEADER_DATE_FORMAT_TIME = "hh:mm aa";

    private final static long SEVEN_DAYS_MILLISEC = 7 * 24 * 60 * 60 * 1000;
    public static String filePathInDirectory = "/downloadedFiles";

    private static Dialog loadingIndicatorDialog;

    /**
     * Gives current date time in string format
     *
     * @return current date time
     */
    public static String getDateTimeString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Gives date in string format
     *
     * @return string date
     */
    public static String getDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     *Returns date in text format
     *
     * @param date in string format
     * @return formatted date
     */
    public static String getDateInHeaderFormat(Date date){
        Calendar currentDateCal = Calendar.getInstance();
        currentDateCal.setTime(new Date());
        Calendar dateTimeCal = Calendar.getInstance();
        dateTimeCal.setTime(date);
        Calendar midnightCal = Calendar.getInstance();
        midnightCal.setTime(new Date());
        midnightCal.set(Calendar.HOUR, 0);
        midnightCal.set(Calendar.MINUTE, 0);
        midnightCal.set(Calendar.SECOND, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat(MESSAGES_HEADER_DATE_FORMAT_TIME, Locale.getDefault());
        //Log.d(DEBUG_TAG, "midnight:" + midnightCal.getTimeInMillis() + " date:" + dateTimeCal.getTimeInMillis());
        if (dateTimeCal.getTimeInMillis() > midnightCal.getTimeInMillis()) {
            //date after mid night
            //Log.d(DEBUG_TAG, "todays message");
            dateFormat = new SimpleDateFormat(MESSAGES_HEADER_DATE_FORMAT_TIME, Locale.getDefault());
        }
        else {
            long diff = currentDateCal.getTimeInMillis() - dateTimeCal.getTimeInMillis();
            if (diff < SEVEN_DAYS_MILLISEC) {
                // date more than a week ago
                //Log.d(DEBUG_TAG, "message from a week ago");
                dateFormat = new SimpleDateFormat(MESSAGES_HEADER_DATE_FORMAT_DAY, Locale.getDefault());
            }
            else {
                // date less than a week ago
                //Log.d(DEBUG_TAG, "message less than a week ago");
                dateFormat = new SimpleDateFormat(MESSAGES_HEADER_DATE_FORMAT_DATE, Locale.getDefault());
            }
        }
        return dateFormat.format(date);
    }

    /**
     * Creates an empty object for specified class
     *
     * @param queryingClass - Name of the class to create object
     * @return object for the class
     */
    public static Object createObjectForClass(Class queryingClass) {
        Class theClass = null;
        Object object = null;

        try {
            theClass = Class.forName(queryingClass.getName());
            object = theClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return object;
    }

    /**
     * Concatenate arrays to form a single array
     *
     * @param first - first array
     * @param rest  - Remaining arras
     * @return a single array
     */
    public static <T> T[] concatArrays(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * removes the time components by setting Hours,Minutes and Seconds to 0
     *
     * @param date
     * @return Date object
     */
    public static Date getDateWithoutSeconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * returns the Next Date from the passed Date
     *
     * @param date
     * @return Date object
     */
    public static Date nextDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * returns the Time in milliseconds for given Date
     *
     * @param date
     * @return time in milliseconds (long)
     */
    public static long getTimeMillis(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    }

    /**
     * returns the Date adjusted to local time zone
     *
     * @param date
     * @return Date object
     */
    public static Date getLocalTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Date localTime = new Date(date.getTime() + TimeZone.getDefault().getOffset(date.getTime()));
        return localTime;
    }

    /**
     * Returns current date as String
     * @return String Current Date
     */
    public static String getCurrentDate() {
        Date currentDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        return simpleDateFormat.format(currentDate);

    }

    /*
    *Validate an email Id
    *@return email valid boolean
    *@param String emal Id
    */
    public static Boolean isValidEmail(String emailId){

        return Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
    }

    /*
    *Validate a password
    *@return password valid boolean
    *@param String password
    */
    public static Boolean isValidPassword(String password){
        //ToDO password validation
        return true;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static void showLoadingDialog(Context context) {
        loadingIndicatorDialog = new Dialog(context);
        loadingIndicatorDialog.setTitle("Please wait...");
        loadingIndicatorDialog.show();
    }

    public static void hideLoadingDialog() {
        loadingIndicatorDialog.dismiss();
    }

}
