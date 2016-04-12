package com.longhuapuxin.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by asus on 2015/12/25.
 */
public class InputTool {
    public static boolean isContainSpace(String s) {
        char[] items = s.toCharArray();
        for (char item : items
                ) {
            if (item == ' ') {
                return true;
            }
        }
        return false;
    }

    public static boolean isContainSpecial(String s) {
        char[] items = s.toCharArray();
        for (char item : items
                ) {
            if ((item > 31 && item < 48) || (item > 57 && item < 65) || (item > 90 && item < 97) || (item > 122 && item < 126)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isContainChinese(String s) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = null;
        char[] items = s.toCharArray();
        for (char item : items
                ) {
            m = p.matcher(String.valueOf(item));
            if (!m.matches()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isContainNumber(String s) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = null;
        char[] items = s.toCharArray();
        for (char item : items
                ) {
            m = p.matcher(String.valueOf(item));
            if (!m.matches()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isContainCharacter(String s) {
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = null;
        char[] items = s.toCharArray();
        for (char item : items
                ) {
            m = p.matcher(String.valueOf(item));
            if (!m.matches()) {
                return false;
            }
        }
        return true;
    }
}
