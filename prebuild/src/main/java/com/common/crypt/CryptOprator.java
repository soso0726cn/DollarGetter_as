package com.common.crypt;

import com.common.io.FileUtils;
import com.common.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 *	必须要有一个约定：当有一个字符串需要加密时，应该如何方便调用？
	比如当我们定义一个String变量时，想对其进行加密，我们只需要这样创建就可以了
	String abc = ByteCrypt.getString("beijing".getBytes());
	我们在正式编译前会对字符串beijing进行加密，转化成字节数组 
 * @author lijichuan
 */
public class CryptOprator {
	private static final boolean DEBUG = true;
	
	private static final String KEY_OPERATE_TYPE= "type";
	private static final String OPERATE_TYPE_VALUE_ENCRYPT = "en";
	private static final String OPERATE_TYPE_VALUE_DECRYPT = "de";
	
	private static final String KEY_KEY1 = "key1";
	private static final String KEY_KEY2= "key2";
	private static final String KEY_DIRS = "dirs";
	private static final String KEY_FILTER= "filters";
	
	private static HashMap<String, String> mArgsMap = new HashMap<String, String> (); 

	/**
	 *	参数：
	 *	type: en/de		en表示加密，de表示解密
	 *	key1,key2: 		加密用的key
	 *	dirs: 			需要处理的目录列表,内容以逗号‘,’号分隔，比如： dirs:/Users/china,/Users/beijing
	 *	filters:     	需要过滤的文件，即这些文件名对应的文件不进行操作，以逗号‘,’号分隔。比如：filters:Test.java,Demo.java
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("arg length: " + args.length);
		saveArgs(args);
		
		String oprateType = mArgsMap.get(KEY_OPERATE_TYPE);
		
		if(oprateType == null) {
			System.err.println("请指定操作类型，例如：\"type:de\" 或者\"type:en\"");
			return;
		}
		
		String opratingDir = mArgsMap.get(KEY_DIRS);
		System.out.println("----- opratingDir: " + opratingDir);
		if(opratingDir == null) {
			System.err.println("请指定要操作的目录，例如：\"dirs:/Users/lijichuan/Documents/\"");
			return;
		}

		String[] opratingDirs = opratingDir.split(",");
		for(int i=0; i<opratingDirs.length; i++) {
			System.out.println("----- opdir: " + opratingDirs[i]);

			boolean exists = new File(opratingDirs[i]).exists();

			System.out.println("----- exists: " + exists);
		}
		List<File> javaFiles = parseFileList(opratingDirs);
		
//		parseKey();
		
		String filter = mArgsMap.get(KEY_FILTER);
		String[] fileFilters = filter == null ? null : filter.split(",");
		
		for(File javaFile: javaFiles) {
			
			if(filter(fileFilters, javaFile)) {
				System.out.println("\n******~~~~~~filted file: " + javaFile + " ~~~~~~******");
				continue;
			}
			
			if(oprateType.equals(OPERATE_TYPE_VALUE_ENCRYPT)) {
				doEncrypt(javaFile);
			} else if(oprateType.equals(OPERATE_TYPE_VALUE_DECRYPT)) {
				doDecrypt(javaFile);
			}
		}
		
		System.out.println("\n ------- 处理完毕 --------- \n");
	}
	
	private static boolean filter(String[] fileFilters, File file) {
		String fileName = file.getName();

		if(fileName.equals(CryptOprator.class.getSimpleName() + ".java")) {
			return true;
		}

		if(fileFilters == null) {
			return false;
		}
		
		for (int i = 0; i < fileFilters.length; i++) {
			if(fileName.equals(fileFilters[i])) {
				return true;
			}
		}
		
		return false;
	}

//	private static void parseKey() {
//		String key1 = mArgsMap.get(KEY_KEY1);
//		String key2 = mArgsMap.get(KEY_KEY2);
//		if(key1 != null && key2 != null) {
//			ByteCrypt.setKey(key1, key2);
//			System.out.println("new key will be used --- key1: " + key1 + " ;key2: " + key2);
//		} else {
//			System.out.println("****** default key will be used ******\n");
//		}
//	}

	private static List<File> parseFileList(String[] opratingDirs) {
		//从目录中找出java文件
		List<File> javaFiles = JavaFileFinder.getJavaFileList(opratingDirs);
		
		if(DEBUG) {
			System.out.println("----- file found: ");
			for(File javaFile: javaFiles) {
				System.out.println("javaFile: " + javaFile.getAbsolutePath());
			}
			System.out.println("---------- \n");
		}
		return javaFiles;
	}
	
	private static void saveArgs(String[] args) {
		for(String arg: args) {
			int indexOf = arg.indexOf(":");
			String key = arg.substring(0,indexOf);
			String value = arg.substring(indexOf +1 );

			System.out.println("key: " + key + " ;value：" + value);

			if(key == null || value == null) {
				continue;
			}
			mArgsMap.put(key, value);
		}
	}

	//--------------- 解密相关 ------------ start
	private static void doDecrypt(File javaFile) {
		System.out.println("\n****** 正在处理的文件：" + javaFile.getAbsolutePath());
		System.out.println("正在搜索需要~~解密~~的内容......");
		ArrayList<String> todos = findDecryptOrigin(javaFile);
		if(todos.size() == 0) {
			System.out.println("无需处理......");
		} else {
			System.out.println("正在~~解密~~......");
		}
		for (String origin : todos) {
			String realTarget = generateDecryptRealTarget(origin);
			System.out.println("原始内容: " + origin + "\n~~解密~~替换后: " + realTarget);
			try {
				StringReplacer.replaceCotent(javaFile, origin, realTarget);
			} catch (PatternSyntaxException e) {
				e.printStackTrace();
			}
			System.out.println("--------------------");
		}
	}
	
	private static String generateDecryptRealTarget(String origin) {
		String byteContent = origin.substring(origin.indexOf("{") + 1, origin.lastIndexOf("}") -1);
		String[] bytes = byteContent.split(",");
		byte[] targetBytes = new byte[bytes.length];
		for(int i=0; i<targetBytes.length; i++) {
			targetBytes[i] = (byte)Integer.parseInt(bytes[i].trim());
		}
		
		String target = new String(ByteCrypt.doEn(targetBytes,1));
		
		return "\"" + target + "\".getBytes()";
	}

	private static Pattern decryptMatchPattern= Pattern.compile("ByteCrypt.getString\\s*\\(\\s*(new\\s*byte(?s).*?\\})");
	private static ArrayList<String> findDecryptOrigin(File javaFile) {
		ArrayList<String> originList = new ArrayList<String> (); 
		try {
			String content = FileUtils.readFileToString(javaFile);
			Matcher matcher = decryptMatchPattern.matcher(content);
			while(matcher.find()) {
				String decryptOrigin = matcher.group(1);
				originList.add(decryptOrigin);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return originList;
	}
	//--------------- 解密相关 ------------ end

	
	//--------------- 加密相关 ------------ start
	private static void doEncrypt(File javaFile) {
		System.out.println("\n****** 正在处理的文件：" + javaFile.getAbsolutePath());
		System.out.println("正在搜索需要~~加密~~的内容......");
		HashMap<String, String> todos = findEncryptOriginAndTarget(javaFile);
		if(todos.size() == 0) {
			System.out.println("无需处理......");
		} else {
			System.out.println("正在~~加密~~......");
		}
		
		for(Entry<String, String> entry: todos.entrySet()) {
			String origin = entry.getKey();
			String target = entry.getValue();
			String realTarget = generateEncryptRealTarget(target);
			System.out.println("原始内容: " + origin + "\n~~加密~~替换后: " + realTarget);
			try {
				StringReplacer.replaceCotent(javaFile, origin, realTarget);
			} catch (PatternSyntaxException e) {
				e.printStackTrace();
			}
			System.out.println("--------------------");
		}
	}

	private static String generateEncryptRealTarget(String target) {
		byte[] enCrypted = ByteCrypt.doEn(target.getBytes(),0);
		
		StringBuilder encodedTarget = new StringBuilder();
		encodedTarget.append("new byte[] {");
		for(int i=0; i< enCrypted.length; i++) {
			encodedTarget.append(enCrypted[i]).append(", ");
			if(i != 0 && i % 5 == 0) {
				encodedTarget.append("\n\t\t\t");
			}
		}

		encodedTarget.deleteCharAt(encodedTarget.lastIndexOf(","));
		encodedTarget.append("}");
		
		return encodedTarget.toString();
	}

	private static Pattern encryptMatchPattern = Pattern.compile("ByteCrypt.getString\\s*\\(\\s*(\"(.*?)\"(?s).*?getBytes\\s*\\(\\s*\\))");

	//结果以map形式返回，key为原始字符串，value为对应的target
	private static HashMap<String,String> findEncryptOriginAndTarget(File javaFile) {
		HashMap<String,String> targetAndOrigin = new HashMap<String,String>();
		
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(
					new InputStreamReader(
					new FileInputStream(javaFile)));
			String line = null;
			while( (line = bufferedReader.readLine()) != null) {
				Matcher matcher = encryptMatchPattern.matcher(line);
				
				while(matcher.find()) {	//一行中有多个匹配，也可以处理
					String lineTrim = line.trim();
					if(lineTrim.startsWith("//") || lineTrim.startsWith("/*")) {		//过滤掉注释， 不对注释进行加密操作
						continue;
					}
					
					String originStr = matcher.group(1);
					String targetStr = matcher.group(2);
					targetAndOrigin.put(originStr, targetStr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(bufferedReader);
		}
		
		return targetAndOrigin;
	}
	
	//--------------- 加密相关 ------------ end
}
