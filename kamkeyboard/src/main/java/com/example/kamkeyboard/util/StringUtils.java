package com.example.kamkeyboard.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Kam
 */
public class StringUtils {

    public static final String VERSION_SEPERATOR = ".";

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim());
    }

    /**
     * judge string equals or not
     *
     * @param str
     * @param other
     * @return false if anyone is empty
     */
    public static boolean equals(String str, String other) {
        if (isEmpty(str) || isEmpty(other)) {
            return false;
        }
        return str.equals(other);
    }

    /**
     * judge string equals or not
     *
     * @param str
     * @param other
     * @return false if anyone is empty
     */
    public static boolean equalsIgnoreCase(String str, String other) {
        if (isEmpty(str) || isEmpty(other)) {
            return false;
        }
        return str.equalsIgnoreCase(other);
    }

    public static List<String> stringToList(String str, String seperator) {
        List<String> itemList = new ArrayList<String>();
        if (isEmpty(str)) {
            return itemList;
        }
        StringTokenizer st = new StringTokenizer(str, seperator);
        while (st.hasMoreTokens()) {
            itemList.add(st.nextToken());
        }

        return itemList;
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    public static long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    public static String calculateStr(CharSequence c) {
        String str = "";
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            if (len < 4) {
                int tmp = (int) c.charAt(i);
                if (tmp > 0 && tmp < 127) {
                    len += 0.5;
                } else {
                    len++;
                }
            } else {
                break;
            }
        }
        if (len > 4) {
            str = str + "...";
        }
        return str;
    }

}
