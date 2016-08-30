package com.common.crypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 使用简单异或的方式对指定文件进行加密
 * @author wmmeng
 *
 */
public class XEncryptUtil {

	private static final class Config {
		static final boolean DO_FILE_ENCRYPT = true;
		static final String DEFAULT_KEY = "GEEK=qLI";
	}

	public static void encrypt(String src, String target) {
		encrypt(src, target, Config.DEFAULT_KEY);
	}

	public static void decrypt(String src, String target) {
		decrypt(src, target, Config.DEFAULT_KEY);
	}


	/**
	 * 对文件进行加密
	 * 说明：先移位，后异或
	 * @param src
	 * @param target
	 * @param key
	 */
	public static void encrypt(String src, String target, String key) {
		byte[] bytes = key.getBytes();
		int size = bytes.length;

		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(src);
			fos = new FileOutputStream(target);
			int len;
			byte[] buf = new byte[1024];
			while ((len = fis.read(buf)) != -1) {
				byte first = buf[0];
				for(int i = 0;i<len -1;i++){
					buf[i] = buf[i+1];
				}
				buf[len - 1] = first;
				
				for (int i = 0; i < len; i++) {
					int m = i % size;
					buf[i] ^= bytes[m];
				}
				fos.write(buf, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 对文件进行解密
	 * 说明：先异或，后移位
	 * @param srcFile
	 * @param target
	 * @param key
	 */
	public static void decrypt(String srcFile, String target, String key){
		byte[] bytes = key.getBytes();
		int size = bytes.length;

		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(target);
			int len;
			byte[] buf = new byte[1024];
			while ((len = fis.read(buf)) != -1) {
				for (int i = 0; i < len; i++) {
					int m = i % size;
					buf[i] ^= bytes[m];
				}
				
				byte last = buf[len - 1];
				for (int i = len-1; i >0; i--) {
					buf[i] = buf[i-1];
				}
				buf[0] = last;
				fos.write(buf, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static InputStream decrypt(InputStream is){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] bytes = Config.DEFAULT_KEY.getBytes();
		int size = bytes.length;

		try {
			int len;
			byte[] buf = new byte[1024];
			while ((len = is.read(buf)) != -1) {
				for (int i = 0; i < len; i++) {
					int m = i % size;
					buf[i] ^= bytes[m];
				}

				byte last = buf[len - 1];
				for (int i = len-1; i >0; i--) {
					buf[i] = buf[i-1];
				}
				buf[0] = last;
				baos.write(buf, 0, len);
			}
			byte[] data = baos.toByteArray();
			return new ByteArrayInputStream(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}
}
