package com.uppermac.utils;

import java.security.MessageDigest;

/**
 * @Author linyb
 * @Date 2016/12/8 13:36
 */
public class MD5Util {

    public final static String MD5(String s) throws Exception {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
//        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
       /* } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
    }

    public MD5Util()
    {
    }

    private static String byteArrayToHexString(byte b[])
    {
        StringBuffer resultSb = new StringBuffer();
        for(int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b)
    {
        int n = b;
        if(n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin)
    {
        String resultString = null;
        try
        {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes())).toUpperCase();
        }
        catch(Exception exception) { }
        return resultString;
    }
    public static String MD5Encode(String origin,String charset)
    {
        String resultString = null;
        try
        {
            //resultString = new String(origin.getBytes(charset));
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin.getBytes(charset))).toUpperCase();
        }
        catch(Exception exception) { }
        return resultString;
    }

    private static final String hexDigits[] = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f"
    };

   
}
