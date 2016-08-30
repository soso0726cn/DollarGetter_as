package com.common.crypt;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class JavaFileFinder {
	
	/**
	 * @param dirs	需要遍历的目录
	 * @return
	 */
	public static List<File> getJavaFileList(String[] dirs) {
		List<File> result = new ArrayList<File> ();
		for(String dir: dirs) {
			File currentDir = new File(dir);
			
			if(currentDir.isDirectory()) {
				JavaFileFilter javaFileFilter = new JavaFileFilter();
				listFiles(result, currentDir.listFiles(), javaFileFilter);
			} else {
				if(currentDir.getPath().endsWith(".java")) {
					result.add(currentDir);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @param dir	需要遍历的目录
	 * @return
	 */
	public static List<File> getJavaFileList(String dir) {
		File currentDir = new File(dir);
		List<File> result = new ArrayList<File> ();
		
		if(currentDir.isDirectory()) {
			JavaFileFilter javaFileFilter = new JavaFileFilter();
			listFiles(result, currentDir.listFiles(), javaFileFilter);
		} else {
			if(currentDir.getPath().endsWith(".java")) {
				result.add(currentDir);
			}
		}
		
		return result;
	}

	private static void listFiles(List<File> result, File[] dirs, FileFilter filter) {
		for(File dir: dirs) {
			if(dir.isDirectory()) {
				listFiles(result, dir.listFiles(filter), filter);
			} else {
				if(dir.getPath().endsWith(".java")) {
					result.add(dir);
				}
			}
		}
	}
	
	private static final class JavaFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			
			if(pathname.isDirectory()) {
				return true;
			}
			
			try {
				String path = pathname.getPath();
				if(pathname != null && path.contains("src") && path.endsWith(".java")) {
					return true;
				}
			} catch (Exception e) {
				//ignore
			}
			
			return false;
		}
	}
}
