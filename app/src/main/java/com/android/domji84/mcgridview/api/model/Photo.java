package com.android.domji84.mcgridview.api.model;

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

	@SerializedName("ispublic")
	private int isPublic;

	@SerializedName("perpage")
	private int perPage;

	@SerializedName("isfriend")
	private int isFriend;

	@SerializedName("isfamily")
	private int isFamily;

	@SerializedName("ownername")
	private String ownerName;

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
		return isPublic;
	}

	public int getPerPage() {
		return perPage;
	}

	public int getIsFriend() {
		return isFriend;
	}

	public int getIsFamily() {
		return isFamily;
	}

	public String getOwnerName() {
		return ownerName;
	}
}
