package com.cnp.sdk;

import java.util.Calendar;

public class JAXBConverters {

    public static Calendar parseDate(String value){
        if(value == null){
            return null;
        }
        return (javax.xml.bind.DatatypeConverter.parseDate(value));
    }

    public static String printDate(Calendar value){
        if(value == null){
            return null;
        }
        return (CalendarPrinter.printDate(value));
    }

    public static String parseString(String value){
        if(value == null){
            return null;
        }
        return (String.valueOf(value));
    }

    public static String printString(String value){
        if(value == null){
            return null;
        }
        return (String.valueOf(value));
    }

    public static Long parseLong(String value){
        if(value == null){
            return null;
        }
        return (Long.valueOf(value));
    }

    public static String printLong(Long value){
        if(value == null){
            return null;
        }
        return (Long.toString(value));
    }
}
