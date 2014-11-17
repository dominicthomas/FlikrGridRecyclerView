package com.android.domji84.mcgridview;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by domji84 on 14/11/14.
 */
public class FlikrApiClient {

	private static String END_POINT = "https://api.flickr.com/services";
	private static String API_KEY = "0a6006474b146381616f256798b2916f";
	private static String API_SECRET = "053be28f04771e3e";

	public static enum PhotoSize {

		SMALL_SQUARE_75("s"),
		LARGE_SQUARE_150("q"),
		THUMBNAIL_100("t"),
		SMALL_240("m"),
		SMALL_320("n"),
		MEDIUM_640("z"),
		MEDIUM_800("c"),
		LARGE_1024("b"),
		LARGE_1600("h"),
		LARGE_2048("k"),
		ORIGINAL("o");

		private String mSize;

		PhotoSize(String size) {
			mSize = size;
		}

		public String getSize() {
			return mSize;
		}
	}

	private static FlikrApiInterface mFlikrApiInterface;

	public static FlikrApiInterface getFlikrApiClient() {
		if (mFlikrApiInterface == null) {
			RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint(END_POINT)
				.build();

			mFlikrApiInterface = restAdapter.create(FlikrApiInterface.class);
		}
		return mFlikrApiInterface;
	}

	public static class FlikrApiParams {

		public static Map<String, String> getRecentParams(int page, List<String> extras) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "flickr.photos.getRecent");
			params.put("api_key", API_KEY);
			params.put("format", "json");
			params.put("nojsoncallback", "1");
			params.put("page", page > 0 ? String.valueOf(page) : String.valueOf(0));
			params.put("extras", Joiner.on(",").join(extras == null ? Lists.newArrayList() : extras));
			return params;
		}

		public static Map<String, String> getRecentParams(int page) {
			return getRecentParams(page, null);
		}

	}

	public static class FlikrApiUrls {

		public static String getPhotoUrl(Photo photo, PhotoSize size) {
			return Joiner.on("").join("https://farm", photo.getFarm(), ".staticflickr.com/", photo.getServer(),
				"/", photo.getId(), "_", photo.getSecret(), "_" + size.getSize() + ".jpg").toString();
		}

	}

	public interface FlikrApiInterface {

		@GET("/rest")
		void getRecentPhotos(@QueryMap Map<String, String> getRecentParams, Callback<Recent> callback);
	}

}
