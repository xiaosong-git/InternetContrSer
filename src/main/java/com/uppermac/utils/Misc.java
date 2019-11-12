package com.uppermac.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Misc {

	 public static boolean compareDate(String DATE1, String DATE2) throws ParseException {
		 
		 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		 
		 Date dt1 = df.parse(DATE1);
         Date dt2 = df.parse(DATE2);
         
         if (dt1.getTime() > dt2.getTime()) {
             return true;
         } else if (dt1.getTime() < dt2.getTime()) {
             return false;
         } else {
             return true;
         }
		 
	 }
	 
}
