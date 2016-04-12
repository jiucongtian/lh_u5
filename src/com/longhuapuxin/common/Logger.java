package com.longhuapuxin.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.code.microlog4android.LoggerFactory;
import com.longhuapuxin.u5.BuildConfig;

import android.util.Log;

/**
 * @author zh
 * @date 2015-8-26
 */
public class Logger {
	public final static boolean DEBUG = BuildConfig.U5_DEBUG;
	public final static boolean WRITE_FILE = BuildConfig.U5_DEBUG;

	private final static String customTagPrefix = "U5";
	private static final com.google.code.microlog4android.Logger fileLogger = LoggerFactory
			.getLogger();

	private static String generateTag(StackTraceElement stack) {
		String tag = "%s.%s(L:%d)";
		String className = stack.getClassName();
		className = className.substring(className.lastIndexOf(".") + 1);
		tag = String.format(tag, stack.getClassName(), className,
				stack.getLineNumber());
		tag = customTagPrefix == null ? tag : customTagPrefix + ":" + tag;
		return tag;
	}

	public static void debug(String log) {
		if (DEBUG) {
			StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
			String tag = generateTag(caller);

			Log.d(tag, log);
			if (WRITE_FILE) {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"MM/dd HH:mm:ss");
				Date currentDate = new Date(System.currentTimeMillis());
				String strDate = formatter.format(currentDate);
				fileLogger.debug(strDate + " : " + tag + " : " + log);
			}

		}
	}

	public static void info(String log) {
		if (DEBUG) {

			StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
			String tag = generateTag(caller);

			Log.i(tag, log);
			if (WRITE_FILE) {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"MM/dd HH:mm:ss");
				Date currentDate = new Date(System.currentTimeMillis());
				String strDate = formatter.format(currentDate);
				fileLogger.info(strDate + " : " + tag + " : " + log);
			}
		}
	}

	public static void warn(String log) {
		if (DEBUG) {
			StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
			String tag = generateTag(caller);
			Log.w(tag, log);
			if (WRITE_FILE) {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"MM/dd HH:mm:ss");
				Date currentDate = new Date(System.currentTimeMillis());
				String strDate = formatter.format(currentDate);
				fileLogger.warn(strDate + " : " + tag + " : " + log);
			}
		}
	}

	public static void error(String log) {
		if (DEBUG) {
			StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
			String tag = generateTag(caller);
			Log.e(tag, log);
			if (WRITE_FILE) {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"MM/dd HH:mm:ss");
				Date currentDate = new Date(System.currentTimeMillis());
				String strDate = formatter.format(currentDate);
				fileLogger.error(strDate + " : " + tag + " : " + log);
			}
		}
	}
}
