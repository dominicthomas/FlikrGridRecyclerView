package com.android.domji84.mcgridview;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by domji84 on 14/11/14.
 */
public class RecyclerFragment extends Fragment {

	private static final String TAG = RecyclerFragment.class.getSimpleName();

	private View mFragmentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mFragmentView = inflater.inflate(R.layout.fragment_recycler, container, false);
		return mFragmentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		loadImages();
	}

	private void loadImages() {

		// TODO: Track Page Number...

		FlikrApiClient.getFlikrApiClient().getRecentPhotos(
			FlikrApiClient.FlikrApiParams.getRecentParams(0),
			new Callback<Recent>() {
				@Override
				public void success(Recent recent, Response response) {
					Log.d(TAG, String.valueOf(recent));
					for (Photo photo : recent.getPhotos().getPhotoList()) {
						Log.d(TAG, FlikrApiClient.FlikrApiUrls.getPhotoUrl(photo));
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, error.getMessage());
				}
			});
	}

}
