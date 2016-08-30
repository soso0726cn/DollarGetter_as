package com.common.crypt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.PatternSyntaxException;

public class StringReplacer {
	
	/**
	 *	将filePath指定的文件中所有的origin字符串替换为target 
	 * @param filePath	需要操作的文件路径
	 * @param origin	原始字符串，即将被替换的字符串
	 * @param target	替换成该字符串
	 * @throws PatternSyntaxException
	 */
	public static void replaceCotent(String filePath, String origin, String target) throws PatternSyntaxException {
		replaceCotent(filePath, origin, target, false);
	}
	
	/**
	 *	将file指定的文件中所有的origin字符串替换为target 
	 * @param file	需要操作的文件
	 * @param origin	原始字符串，即将被替换的字符串
	 * @param target	替换成该字符串
	 * @throws PatternSyntaxException
	 */
	public static void replaceCotent(File file, String origin, String target) throws PatternSyntaxException {
		replaceCotent(file.getAbsolutePath(), origin, target, false);
	}
	
	public static void replaceCotent(File file, String origin, String target, boolean notDoIfTargetExist) throws PatternSyntaxException {
		replaceCotent(file.getAbsolutePath(), origin, target, notDoIfTargetExist);
	}
	
	/**
	 * @param notDoIfTargetExist	如果原文件内容包含target指定的字符串， 则不进行操作。 如果需要避免重复操作，可以将其设置为true
	 */
	public static void replaceCotent(String filePath, String origin, String target, boolean notDoIfTargetExist) throws PatternSyntaxException {
		Path path = Paths.get(filePath);
		Charset charset = StandardCharsets.UTF_8;
		String originParsed = escapeExprSpecialWord(origin);
		String targetParsed = escapeExprSpecialWord(target);
//		System.out.println("originParsed: " + originParsed + "targetParsed: " + targetParsed);
		String content;
		try {
			content = new String(Files.readAllBytes(path), charset);
			if(notDoIfTargetExist) {
				if(content.contains(originParsed) &&!content.contains(targetParsed)) {
					content = content.replaceAll(originParsed, targetParsed);
					Files.write(path, content.getBytes(charset));
				}
			} else {
				content = content.replaceAll(originParsed, targetParsed);
				Files.write(path, content.getBytes(charset));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param file		文件路径
	 * @param start		开始标签
	 * @param end		结束标签
	 * @param target	开始标签与结束标签之前的字符串将被替换成target值
	 */
	public static void replaceContent(File file,String start,String end,String target){
		Path path = Paths.get(file.getAbsolutePath());
		Charset charset = StandardCharsets.UTF_8;
		String targetParsed = target;
		String content;
		try {
			content = new String(Files.readAllBytes(path), charset);
			int beginIndex = content.indexOf(start);
			if (beginIndex == -1) {
				System.out.println("没找到开始标签start");
				return;
			}
			int endIndex = content.indexOf(end) + end.length();
			if (endIndex - end.length() == -1) {
				System.out.println("没找到结束标签end");
				return;
			}
			String originParsed = content.substring(beginIndex, endIndex);
			content = content.replace(originParsed, targetParsed);
			Files.write(path, content.getBytes(charset));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}



	/**
	 * 转义正则特殊字符 （$()*+.[]?\^{},|）
	 * 
	 * @param keyword
	 * @return
	 */
	public static String escapeExprSpecialWord(String keyword) {
		if (keyword != null && !"".equals(keyword)) {
			String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
			for (String key : fbsArr) {
				if (keyword.contains(key)) {
					keyword = keyword.replace(key, "\\" + key);
				}
			}
		}
		return keyword;
	}
	
}