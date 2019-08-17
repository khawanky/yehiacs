package com.yehia.c3s.bullyalgorithm.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public static String getFormattedTime (long time) {
		DateFormat simple = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss:SSS"); 
        Date result = new Date(time); 
        return simple.format(result);
	}
}
