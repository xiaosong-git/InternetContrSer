package com.uppermac.utils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.uppermac.data.Constants;


/**
 * @Author:Liaofx
 * @Description:
 * @Date:2018-08-11
 */
public class ControlDeviceUtil {
    private static final byte OUT1[] = {(byte) 0xFE, 0x10, 0x00, 0x03, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x00, (byte) 0xAC}; //闪开关 0.1s
    private static final byte OUT2[] = {(byte) 0xFE, 0x10, 0x00, 0x08, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x41, (byte) 0x1F};
    private static final byte OUT3[] = {(byte) 0xFE, 0x10, 0x00, (byte) 0x0D, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, (byte) 0x81, 0x20};
    private static final byte OUT4[] = {(byte) 0xFE, 0x10, 0x00, 0x12, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, (byte) 0xC0, (byte) 0x6C};
   
    private static final byte flashoff[] = {(byte) 0xFE, 0x10, 0x00, 0x03, 0x00, 0x02, 0x04, 0x00, 0x02, 0x00, 0x014, 0x21, 0x62};

    //FE 10 00 03 00 02 04 00 04 00 0A 00 D8
    private static final byte OUT5[] = {(byte) 0xFE, 0x10, 0x00, 0x03, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00,(byte) 0x0A, 0x00,(byte) 0xD8 };
    
 
    
    public static void controlDevice(String deviceIp, int devicePort, String deviceName,String orgCode) {
        // 1、创建客户端Socket，指定服务器地址和端口
        Socket socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(deviceIp, devicePort);
        try {
            socket.connect(socketAddress, 2000);
        } catch (Throwable throwable) {
        	
            Tracker.eLogFile(throwable);
        }

        // 2、获取输出流，向服务器端发送信息
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            if (deviceName.isEmpty()) {
                return;
            }
            byte OUT[] = new byte[13];
            switch (deviceName) {
                case "OUT1":
                    OUT = ControlDeviceUtil.OUT1;
                    break;
                case "OUT2":
                    OUT = ControlDeviceUtil.OUT2;
                    break;
                case "OUT3":
                    OUT = ControlDeviceUtil.OUT3;
                    break;
                case "OUT4":
                    OUT = ControlDeviceUtil.OUT4;
                    break;
                case "OUT5":
                    OUT = ControlDeviceUtil.OUT5;
                    break;
            }
            onff(OUT, socket, dos);
            System.out.println("开启继电器："+System.currentTimeMillis());
            socket.close(); // 关闭Socket
        } catch (Throwable throwable) {
            Tracker.eLogFile(throwable);
        }finally {
        	if(socket!=null) {
        		try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	if(dos!=null) {
        		try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	
        }
    }

    private static void onff(byte[] cmd, Socket socket, DataOutputStream dos) throws InterruptedException, IOException {
        try {
            dos.write(cmd);
            dos.flush();
            Thread.sleep(500);
        } catch (Throwable throwable) {
            Tracker.eLogFile(throwable);
        }
    }
}