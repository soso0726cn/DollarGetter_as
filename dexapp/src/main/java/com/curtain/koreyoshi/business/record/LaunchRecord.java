package com.curtain.koreyoshi.business.record;

import com.common.crypt.ByteCrypt;

public class LaunchRecord{
	public static final int STATUS_INVALID = -1;
	public static final int STATUS_FIRST = 1001;
	public static final int STATUS_SECOND = 1002;
	public static final int STATUS_THIRD = 1003;
	
	private String pName;
	private int next;
	private long installTime;
	private int first;
	private int second;
	private int third;
	
	
	
	public long getInstallTime() {
		return installTime;
	}
	public void setInstallTime(long installTime) {
		this.installTime = installTime;
	}
	public String getpName() {
		return pName;
	}
	public void setpName(String pName) {
		this.pName = pName;
	}
	
	public int getNext() {
		return next;
	}
	
	public void setNext(int next) {
		this.next = next;
	}
	public int getFirst() {
		return first;
	}
	public void setFirst(int first) {
		this.first = first;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	public int getThird() {
		return third;
	}
	public void setThird(int third) {
		this.third = third;
	}
	@Override
	public String toString() {
		return ByteCrypt.getString("LaunchRecord [pName=".getBytes()) + pName
				+ ByteCrypt.getString(", next=".getBytes()) + next
				+ ByteCrypt.getString(", installTime=".getBytes())+ installTime
				+ ByteCrypt.getString(", first=".getBytes()) + first
				+ ByteCrypt.getString(", second=".getBytes()) + second
				+ ByteCrypt.getString(", third=".getBytes()) + third
				+ ByteCrypt.getString("]".getBytes());
	}
	
	
	
	
}
