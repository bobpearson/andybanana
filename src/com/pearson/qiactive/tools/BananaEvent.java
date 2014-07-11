package com.pearson.qiactive.tools;

/**
 * Created by vjamiro on 7/11/14.
 */
public class BananaEvent {
	public final static int UNKNOWN = 0;
	public final static int ERROR = 1;
	public final static int STATUS = 2;
	public final static int TAKE = 3;
	public final static int BOGART = 4;
	public final static int STEAL = 5;
	public final static int RELEASE = 6;

	private String message = "";


	public int getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public BananaEvent(int type, String message) {
		this.type = type;
		this.message = message;
	}


}
