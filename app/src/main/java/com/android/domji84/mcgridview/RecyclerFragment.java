package com.android.domji84.mcgridview;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by domji84 on 14/11/14.
 */
public class RecyclerFragment extends Fragment {

	private static final String TAG = RecyclerFragment.class.getSimpleName();

	private View mFragmentView;
	private GridView mGridView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mFragmentView = inflater.inflate(R.layout.fragment_recycler, container, false);
		return mFragmentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mGridView = (GridView) view.findViewById(R.id.gridview);
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
					mGridView.setAdapter(new ImageAdapter(getActivity(), recent.getPhotos().getPhotoList()));
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, error.getMessage());
				}
			});
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		List<Photo> mPhotoList;

		public ImageAdapter(Context context, List<Photo> photoList) {
			this.mContext = context;
			this.mPhotoList = photoList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder;

			if(convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_grid_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.gridItemImage = (ImageView) convertView.findViewById(R.id.grid_item_image);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			Picasso.with(mContext)
				.load(FlikrApiClient.FlikrApiUrls.getPhotoUrl(mPhotoList.get(position)))
				.into(viewHolder.gridItemImage);

			return convertView;
		}

		private class ViewHolder {
			ImageView gridItemImage;
		}

		@Override
		public int getCount() {
			return mPhotoList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

}
