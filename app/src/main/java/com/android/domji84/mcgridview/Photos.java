package com.android.domji84.mcgridview;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by domji84 on 14/11/14.
 */
public class Photos {

	private int page;

	private int pages;

	private int perpage;

	private int total;

	@SerializedName("photo")
	private List<Photo> photoList;

	public int getPage() {
		return page;
	}

	public int getPages() {
		return pages;
	}

	public int getPerpage() {
		return perpage;
	}

	public int getTotal() {
		return total;
	}

	public List<Photo> getPhotoList() {
		return photoList;
	}

}
