package com.uppermac.utils;

import javax.servlet.http.HttpServletRequest;

public class IPUtil {
	
	
	
	
	public static String getIp(HttpServletRequest request) throws Exception {  
		String ip = request.getHeader("X-Forwarded-For");   
		if (ip != null) {     if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {    
			int index = ip.indexOf(",");       
			if (index != -1) {       
				return ip.substring(0, index);      
				} 
			else {         
				return ip;     
				}    
			}   
		}   
		ip = request.getHeader("X-Real-IP");  
		if (ip != null) { 
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {     
				return ip;     }  
			}   
		ip = request.getHeader("Proxy-Client-IP");   
		if (ip != null) {    
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {   
				return ip;     }   } 
		ip = request.getHeader("WL-Proxy-Client-IP");   
		if (ip != null) {   
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {   
				return ip;     }   }   ip = request.getRemoteAddr();  
				return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
				
	} 

}
