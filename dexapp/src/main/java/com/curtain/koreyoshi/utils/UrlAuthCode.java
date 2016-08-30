package com.curtain.koreyoshi.utils;

public class UrlAuthCode {

	public static int BKDRHash(String str) {
		int seed = 131;
		int hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = (hash * seed) + str.charAt(i);
		}
		return (hash & 0x7FFFFFFF);
	}

	public static String hashResultTo64(int result) {
		StringBuffer sb = new StringBuffer("");
		int c = 0;
		while (result != 0) {
			c = result % 64;
			result = result / 64;
			sb.append(get_64_element(c));
		}
		return sb.reverse().toString();
	}

	static char get_64_element(int num) {
		char c = 0;
		if (num >= 0 && num <= 9) {
			c = (char) (num + 48);
		} else if (num >= 10 && num <= 35) {
			c = (char) (num + 87);
		} else if (num >= 36 && num <= 61) {
			c = (char) (num + 29);
		} else if (num == 62) {
			c = (char) (43);
		} else if (num == 63) {
			c = (char) (45);
		} else {
		}
		return c;
	}
}
