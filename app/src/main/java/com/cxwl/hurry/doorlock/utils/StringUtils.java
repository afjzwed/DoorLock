package com.cxwl.hurry.doorlock.utils;

/**
 * @author xlei
 * @Date 2018/5/23.
 */

public class StringUtils {
    /**
     * @param str
     * @return boolean
     * @throws
     * @Title: isEmepty
     * @Description: 判断字符串是否为空和空串
     */
    public static boolean isNoEmpty(String str) {
        if (str == null) {
            return false;
        } else if ("".equals(str)) {
            return false;
        }
        return true;
    }
}
