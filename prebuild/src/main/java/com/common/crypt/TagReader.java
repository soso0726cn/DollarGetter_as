package com.common.crypt;

/**
 * Created by lijichuan on 16/3/10.
 */
public class TagReader {
    /**
     * @param content 原始文本内容
     * @param start   开始标签
     * @param end     结束标签
     * @return 开始标签与结束标签之前的字符串
     */
    public static String readContent(String content, String start, String end) {
        int beginIndex = content.indexOf(start) + start.length();
        if (beginIndex == -1) {
            System.out.println("没找到开始标签start");
            return null;
        }
        int endIndex = content.indexOf(end);
        if (endIndex - end.length() == -1) {
            System.out.println("没找到结束标签end");
            return null;
        }
        String base = content.substring(beginIndex, endIndex);

        String firstRemoved = base.replaceAll("(?m)^[ \t]*\r?\n", "");
        String lastRemoved = firstRemoved.replaceAll("(?m)[ \t]*\r?\n$", "");

        return lastRemoved;


    }
}
