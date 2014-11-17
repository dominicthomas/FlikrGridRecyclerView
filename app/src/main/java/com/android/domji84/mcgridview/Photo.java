package com.android.domji84.mcgridview;

import com.google.gson.annotations.SerializedName;

/**
 * Created by domji84 on 17/11/14.
 */
public class Photo {

	private String id;

	private String owner;

	private String secret;

	private String server;

	private int farm;

	private String title;

	private int ispublic;

	private int perpage;

	private int isfriend;

	private int isfamily;

	public String getId() {
		return id;
	}

	public String getOwner() {
		return owner;
	}

	public String getSecret() {
		return secret;
	}

	public String getServer() {
		return server;
	}

	public int getFarm() {
		return farm;
	}

	public String getTitle() {
		return title;
	}

	public int getIspublic() {
		return ispublic;
	}

	public int getPerpage() {
		return perpage;
	}

	public int getIsfriend() {
		return isfriend;
	}

	public int getIsfamily() {
		return isfamily;
	}
}
