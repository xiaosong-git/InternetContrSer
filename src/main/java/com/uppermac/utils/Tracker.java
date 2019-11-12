package com.uppermac.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Administrator on 2017/7/10 0010.
 */
public class Tracker {
    /**
     * 默认日志
     */
    private static final Logger sLog = LoggerFactory.getLogger(Tracker.class);

    private static final Logger sLogDB = LoggerFactory.getLogger("database");
    /**
     * 网络日志
     */
    private static final Logger sLogNW = LoggerFactory.getLogger("network");
    private static boolean sLOG_DEBUG_CONTROL = false;
    private static boolean sLOG_DEBUG_FILE = true;
    private static boolean sLOG_SECOND_LOG_CONTROL = false;
    private static String sTAG = "test";

    public static void setDebugLogControl(boolean isDebugLogControl) {
        sLOG_DEBUG_CONTROL = isDebugLogControl;
    }

    public static void setSecondLog(boolean isSecondLog){
        sLOG_SECOND_LOG_CONTROL = isSecondLog;
    }

    public static boolean getSecondLog(){
        return sLOG_SECOND_LOG_CONTROL;
    }

    public static void setDebugLogFile(boolean isDebugLogFile) {
        sLOG_DEBUG_FILE = isDebugLogFile;
    }

    public static void setTag(String tag) {
        sTAG = tag;
    }

    public static void iLogFile(String content) {
        sLog.info(MakeLog(content));
    }

    public static void dLogFile(String content) {
        sLog.debug(MakeLog(content));
    }

    public static void eLogFile(String content) {
        sLog.error(MakeLog(content));
    }

    public static void iLogFile(String content, Object... objects) {
        sLog.info(MakeLog(content), objects);
    }

    public static void dLogFile(String content, Object... objects) {
        sLog.debug(MakeLog(content), objects);
    }

    public static void eLogFile(String content, Object... objects) {
        sLog.error(MakeLog(content), objects);
    }

    public static void eLogFile(Throwable throwable) {
        if (throwable == null) {
            return;
        }
        sLog.error(MakeLog(""), throwable);
    }

    public static void eLogFile(String content, Throwable throwable) {
        sLog.error(MakeLog(content), throwable);
    }

    public static void dbLogFile(String content) {
        sLogDB.info(MakeLog(content));
    }

    public static void netLogFile(String content) {
        sLogNW.info(MakeLog(content));
    }

    private static String MakeLog(String content) {
        StackTraceElement elem = new Throwable().getStackTrace()[2];
        return String.format("%s(%d): %s", elem.getFileName(), elem.getLineNumber(), content);
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }
}
