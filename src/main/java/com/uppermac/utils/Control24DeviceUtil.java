package com.uppermac.utils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.springframework.beans.factory.annotation.Autowired;

import com.uppermac.data.Constants;
import com.uppermac.service.TowerInforService;


/**
 * @Author:Liaofx
 * @Description:
 * @Date:2018-08-11
 */
public class Control24DeviceUtil {
	
	
    private static final byte OUT1[] = {(byte) 0xFE, 0x10, 0x00, 0x03, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x00, (byte) 0xAC}; //闪开关 0.1s
    private static final byte OUT2[] = {(byte) 0xFE, 0x10, 0x00, 0x08, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x41, (byte) 0x1F}; 
    private static final byte OUT3[] = {(byte) 0xFE, 0x10, 0x00,(byte) 0x0D, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0x81, 0x20}; 
    private static final byte OUT4[] = {(byte) 0xFE, 0x10, 0x00, 0x12, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0xC0,(byte) 0x6C}; 
    private static final byte OUT5[] = {(byte) 0xFE, 0x10, 0x00, 0x17, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x00, 0x53}; 
    private static final byte OUT6[] = {(byte) 0xFE, 0x10, 0x00,(byte) 0x1C, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x41,(byte) 0xE0}; 
    private static final byte OUT7[] = {(byte) 0xFE, 0x10, 0x00, 0x21, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0x83,(byte) 0x6D}; 
    private static final byte OUT8[] = {(byte) 0xFE, 0x10, 0x00, 0x26, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0xC2,(byte) 0x8B}; 
    private static final byte OUT9[] = {(byte) 0xFE, 0x10, 0x00,(byte) 0x2B, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x03, 0x12}; 
    private static final byte OUT10[] = {(byte) 0xFE, 0x10, 0x00, 0x30, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x43,(byte) 0xAD}; 
    private static final byte OUT11[] = {(byte) 0xFE, 0x10, 0x00, 0x35, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0x83,(byte) 0x92}; 
    private static final byte OUT12[] = {(byte) 0xFE, 0x10, 0x00,(byte) 0x3A, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0xC3,(byte) 0xD2}; 
    private static final byte OUT13[] = {(byte) 0xFE, 0x10, 0x00,(byte) 0x3F, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x03,(byte) 0xED}; 
    private static final byte OUT14[] = {(byte) 0xFE, 0x10, 0x00, 0x44, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x45,(byte) 0x7A}; 
    private static final byte OUT15[] = {(byte) 0xFE, 0x10, 0x00, 0x49, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0x84,(byte) 0xE3}; 
    private static final byte OUT16[] = {(byte) 0xFE, 0x10, 0x00,(byte) 0x4E, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0xC5, 0x05}; 
    private static final byte OUT17[] = {(byte) 0xFE, 0x10, 0x00, 0x53, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x05,(byte) 0x90}; 
    private static final byte OUT18[] = {(byte) 0xFE, 0x10, 0x00, 0x58, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x44, 0x23}; 
    private static final byte OUT19[] = {(byte) 0xFE, 0x10, 0x00,(byte) 0x5D, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0x84,(byte) 0x1C}; 
    private static final byte OUT20[] = {(byte) 0xFE, 0x10, 0x00, 0x62, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0xC7, 0x48};
    private static final byte OUT21[] = {(byte) 0xFE, 0x10, 0x00, 0x67, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x07, 0x77};
    private static final byte OUT22[] = {(byte) 0xFE, 0x10, 0x00,(byte) 0x6C, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01, 0x46,(byte) 0xC4}; 
    private static final byte OUT23[] = {(byte) 0xFE, 0x10, 0x00, 0x71, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0x86, 0x51}; 
    private static final byte OUT24[] = {(byte) 0xFE, 0x10, 0x00, 0x76, 0x00, 0x02, 0x04, 0x00, 0x04, 0x00, 0x01,(byte) 0xC7,(byte) 0xB7}; 
    
    
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
                    OUT = Control24DeviceUtil.OUT1;
                    break;
                case "OUT2":
                    OUT = Control24DeviceUtil.OUT2;
                    break;
                case "OUT3":
                    OUT = Control24DeviceUtil.OUT3;
                    break;
                case "OUT4":
                    OUT = Control24DeviceUtil.OUT4;
                    break;
                case "OUT5":
                    OUT = Control24DeviceUtil.OUT5;
                    break;
                case "OUT6":
                    OUT = Control24DeviceUtil.OUT6;
                    break;
                case "OUT7":
                    OUT = Control24DeviceUtil.OUT7;
                    break;
                case "OUT8":
                    OUT = Control24DeviceUtil.OUT8;
                    break;
                case "OUT9":
                    OUT = Control24DeviceUtil.OUT9;
                    break;
                case "OUT10":
                    OUT = Control24DeviceUtil.OUT10;
                    break;
                case "OUT11":
                    OUT = Control24DeviceUtil.OUT11;
                    break;
                case "OUT12":
                    OUT = Control24DeviceUtil.OUT12;
                    break;
                case "OUT13":
                    OUT = Control24DeviceUtil.OUT13;
                    break;
                case "OUT14":
                    OUT = Control24DeviceUtil.OUT14;
                    break;
                case "OUT15":
                    OUT = Control24DeviceUtil.OUT15;
                    break;
                case "OUT16":
                    OUT = Control24DeviceUtil.OUT16;
                    break;
                case "OUT17":
                    OUT = Control24DeviceUtil.OUT17;
                    break;
                case "OUT18":
                    OUT = Control24DeviceUtil.OUT18;
                    break;
                case "OUT19":
                    OUT = Control24DeviceUtil.OUT19;
                    break;
                case "OUT20":
                    OUT = Control24DeviceUtil.OUT20;
                    break;
                case "OUT21":
                    OUT = Control24DeviceUtil.OUT21;
                    break;
                case "OUT22":
                    OUT = Control24DeviceUtil.OUT22;
                    break;
                case "OUT23":
                    OUT = Control24DeviceUtil.OUT23;
                    break;
                case "OUT24":
                    OUT = Control24DeviceUtil.OUT24;
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