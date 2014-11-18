package com.android.domji84.mcgridview.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.domji84.mcgridview.R;
import com.android.domji84.mcgridview.api.FlikrApiClient;
import com.android.domji84.mcgridview.api.model.Photo;
import com.android.domji84.mcgridview.interfaces.GridItemObjectTapListener;
import com.android.domji84.mcgridview.interfaces.LoadImagesListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by domji84 on 18/11/14.
 */
public class GridItemAdapter extends RecyclerView.Adapter<GridItemAdapter.ViewHolder> {

	private List<Photo> mItems = new ArrayList<Photo>();

	private GridItemObjectTapListener mTapListener;

	private LoadImagesListener mLoadImagesListener;

	private int mCurrentPage;

	private int mTotalPageCount;

	public GridItemAdapter(GridItemObjectTapListener tapListener, LoadImagesListener loadImagesListener) {

		this.mTapListener = tapListener;
		this.mLoadImagesListener = loadImagesListener;
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
		Picasso.with(holder.image.getContext()).load(FlikrApiClient.FlikrApiUrls.getPhotoUrl(item,
			FlikrApiClient.PhotoSize.MEDIUM_640)).into(holder.image);
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

	private int getDummyValue(){
		return (int) Math.floor(Math.random() * 150);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	public void setItems(List<Photo> photoList, int currentPage, int totalPages) {
		mItems = photoList;
		mCurrentPage = currentPage;
		mTotalPageCount = totalPages;
		notifyDataSetChanged();
	}

	public void addItems(List<Photo> photoList, int currentPage, int totalPages) {
		for (Photo photo : photoList) {
			mItems.add(photo);
		}
		mCurrentPage = currentPage;
		mTotalPageCount = totalPages;
		notifyDataSetChanged();
	}

	public Photo getItemAt(int position) {
		if (position < mItems.size())
			return mItems.get(position);
		return null;
	}
}
