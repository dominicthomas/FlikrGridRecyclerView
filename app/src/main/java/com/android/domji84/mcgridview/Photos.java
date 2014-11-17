package com.android.domji84.mcgridview;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by domji84 on 14/11/14.
 */
public class Photos {

	public int page;

	public int pages;

	public int perpage;

	public int total;

	@SerializedName("photo")
	private List<Photo> photoList;

	public List<Photo> getPhotoList() {
		return photoList;
	}
}
