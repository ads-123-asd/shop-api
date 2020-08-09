package com.fh.shop.api.common;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String Y_M_D = "yyyy-MM-dd";

    public static final String FULL_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final String FULLTIME = "yyyyMMddHHmmss";

    public static String addMinutes(Date date,int minute,String patten) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.MINUTE,minute);
        Date time = instance.getTime();
        return se2te(time, patten);
    }


    public static String se2te(Date date, String patten) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sim = new SimpleDateFormat(patten);
        String format = sim.format(date);
        return format;
    }
    public static  Date se2teDate(String date,String partem){
        if (StringUtils.isEmpty(date)) {
            throw new RuntimeException("字符串为空");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(partem);
        Date date2 =null;
        try {
             date2 = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
              return date2;
    }
}