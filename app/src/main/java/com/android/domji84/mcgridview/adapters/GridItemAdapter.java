package com.android.domji84.mcgridview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.domji84.mcgridview.R;
import com.android.domji84.mcgridview.api.model.Photo;
import com.android.domji84.mcgridview.interfaces.GridItemObjectTapListener;
import com.android.domji84.mcgridview.interfaces.LoadImagesListener;
import com.android.domji84.mcgridview.utils.ConnectionTypeChecker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.android.domji84.mcgridview.api.FlikrApiClient.FlikrApiUrls;
import static com.android.domji84.mcgridview.api.FlikrApiClient.PhotoSize;
import static com.android.domji84.mcgridview.utils.ConnectionTypeChecker.ConnectionType;

/**
 * Created by domji84 on 18/11/14.
 */
public class GridItemAdapter extends RecyclerView.Adapter<GridItemAdapter.ViewHolder> {

	private Context mContext;

	private List<Photo> mItems = new ArrayList<Photo>();

	private GridItemObjectTapListener mTapListener;

	private LoadImagesListener mLoadImagesListener;

	private int mCurrentPage;

	private int mTotalPageCount;

	private ConnectionType mConnectionType;

	public GridItemAdapter(Context context, GridItemObjectTapListener tapListener, LoadImagesListener loadImagesListener) {
		mContext = context;
		mTapListener = tapListener;
		mLoadImagesListener = loadImagesListener;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ImageView image;
		public TextView owner;
		public TextView commentCount;
		public TextView favCount;

		public ViewHolder(View view) {
			super(view);
			owner = (TextView) view.findViewById(R.id.grid_item_owner);
			image = (ImageView) view.findViewById(R.id.grid_item_image);
			commentCount = (TextView) view.findViewById(R.id.grid_item_comment_count);
			favCount = (TextView) view.findViewById(R.id.grid_item_fav_count);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.layout_grid_item, null);
		final ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		final Photo item = mItems.get(position);
		holder.owner.setText(item.getOwnerName());
		Picasso.with(holder.image.getContext()).cancelRequest(holder.image);
		Picasso.with(holder.image.getContext()).load(FlikrApiUrls.getPhotoUrl(item,
			getPohtoSizeForConnection())).into(holder.image);

		holder.image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (item != null)
					mTapListener.itemTap(view, mItems.indexOf(item));
			}
		});
		holder.itemView.setTag(item);

		// dummy values for faves and comments
		holder.commentCount.setText(String.valueOf(getDummyValue()));
		holder.favCount.setText(String.valueOf(getDummyValue()));

		// infinite scroll handling
		if (position == mItems.size() - 1 && mCurrentPage <= mTotalPageCount) {
			if (mCurrentPage == mTotalPageCount) {
				mLoadImagesListener.noMorePages();
			} else {
				int newPage = mCurrentPage + 1;
				mLoadImagesListener.loadPage(newPage);
			}
		}
	}

	/**
	 * Will check the connection type and return a small photo
	 * size if on 3G/Mobile Data and large one if there is WIFI
	 *
	 * @return
	 */
	private PhotoSize getPohtoSizeForConnection() {
		if (mConnectionType != null) {
			switch (mConnectionType) {
				case MOBILE:
					return PhotoSize.LARGE_SQUARE_150;
				case WIFI:
					return PhotoSize.SMALL_320;
			}
		}
		return PhotoSize.SMALL_240;
	}

	private int getDummyValue() {
		return (int) Math.floor(Math.random() * 150);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	public void setItems(List<Photo> photoList, int currentPage, int totalPages) {
		mItems.removeAll(mItems);
		mItems = photoList;
		setPageTracking(currentPage, totalPages);
		checkConnectionType();
		/*
		Currently unable to load items with animations. We need to call
		notifyItemInserted or notifyItemRangeInserted to trigger the RecyclerView
		itemAnimator and there seems to be a bug in the platform where, when adding
		new items to the dataset and calling notify{...}, we get an index out of bounds exception.
		This should be fixed in future updates. See https://code.google.com/p/android/issues/detail?id=77232
		*/
		notifyDataSetChanged();
	}

	public void addItems(List<Photo> photoList, int currentPage, int totalPages) {
		for (Photo photo : photoList) {
			mItems.add(photo);
		}
		setPageTracking(currentPage, totalPages);
		checkConnectionType();
		notifyDataSetChanged();
	}

	private void setPageTracking(int currentPage, int totalPages) {
		mCurrentPage = currentPage;
		mTotalPageCount = totalPages;
	}

	public Photo getItemAt(int position) {
		if (position < mItems.size())
			return mItems.get(position);
		return null;
	}

	private void checkConnectionType() {
		mConnectionType = ConnectionTypeChecker.getConnectionType(mContext);
	}
}
