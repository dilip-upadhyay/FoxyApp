/*
 * MD5.java
 *
 * Created on June 30, 2006, 8:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

/**
 *
 * @author eric
 */
public class MD5 {
    
    /** Creates a new instance of MD5 */
    public MD5() {
    }
    
    public static void main(String [] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    	System.out.println(messageDigest("password"));
    }
    
    public static String messageDigest(String text)
    throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        
        String tempStr = new String();
        String str = new String();
        
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes("8859_1"));
        
        byte[] b1 = md.digest();
        String result = new String(b1, "8859_1");
        
        for (int i = 0; i < result.length(); i++) {
            int x = (int) result.charAt(i);
            tempStr = Integer.toHexString(x);
            
            if (tempStr.length() < 2) {
                tempStr = "0"+tempStr;
            }
            str = str + tempStr;
        }
        return (str.toUpperCase());
    }
    public static String messageDecode(String text)
    	    throws NoSuchAlgorithmException,
    	            UnsupportedEncodingException {
    	        
    	        String tempStr = new String();
    	        String str = new String();
    	        
    	        MessageDigest md = MessageDigest.getInstance("MD5");
    	        md.update(text.getBytes("8859_1"));
    	        
    	        byte[] b1 = md.digest();
    	        String result = new String(b1, "8859_1");
    	        
    	        for (int i = 0; i < result.length(); i++) {
    	            int x = (int) result.charAt(i);
    	            tempStr = Integer.toHexString(x);
    	            
    	            if (tempStr.length() < 2) {
    	                tempStr = "0"+tempStr;
    	            }
    	            str = str + tempStr;
    	        }
    	        return (str.toUpperCase());
    	    }
}
