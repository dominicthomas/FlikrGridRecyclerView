package com.android.domji84.mcgridview;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.android.domji84.mcgridview.FlikrApiClient.FlikrApiUrls;
import static com.android.domji84.mcgridview.FlikrApiClient.PhotoSize;
import static com.android.domji84.mcgridview.FlikrApiClient.getFlikrApiClient;

public class RecyclerTest extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

	private static final String TAG = RecyclerTest.class.getSimpleName();

	private RecyclerView mRecyclerView;

	private GridItemAdapter mAdapter;

	private GridLayoutManager mLayoutManager;

	private SwipeRefreshLayout mSwipeRefreshLayout;

	public interface GridItemObjectTapListener {
		public void itemTap(int position);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recycler_test);
		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		// TODO: scroll listener to show [Top] button when scrolling up
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mLayoutManager = new GridLayoutManager(this, 2);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new GridItemAdapter();
		mAdapter.setObjectTapListener(mGridItemObjectTapListener);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setHasFixedSize(true);
	}

	private final GridItemObjectTapListener mGridItemObjectTapListener = new GridItemObjectTapListener() {
		@Override
		public void itemTap(int position) {
			Toast.makeText(getApplicationContext(), "tapped " + position, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		loadImageData(1);
		mRecyclerView.scrollToPosition(0);
	}

	@Override
	public void onRefresh() {
		loadImageData(1);
	}

	private void loadImageData(final int page) {
		getFlikrApiClient().getRecentPhotos(
			FlikrApiClient.FlikrApiParams.getRecentParams(page),
			new Callback<Recent>() {
				@Override
				public void success(Recent recent, Response response) {

					final List<Photo> photoList = recent.getPhotos().getPhotoList();
					final int currentPage = recent.getPhotos().getPage();
					final int totalPages = recent.getPhotos().getPages();
					mAdapter.notifyDataSetChanged();
					mSwipeRefreshLayout.setRefreshing(false);

					if (page == 1) { // first page
						mAdapter.setItems(photoList, currentPage, totalPages);
					} else {
						mAdapter.addItems(photoList, currentPage, totalPages);
					}
				}

				@Override
				public void failure(RetrofitError error) {
					// TODO: show error fragment!
				}
			});
	}

	public class GridItemAdapter extends RecyclerView.Adapter<GridItemAdapter.ViewHolder> {

		private List<Photo> mItems = new ArrayList<Photo>();

		private GridItemObjectTapListener mTapListener;

		private int mCurrentPage;

		private int mTotalPageCount;

		public class ViewHolder extends RecyclerView.ViewHolder {

			public ImageView image;
			public TextView owner;

			public ViewHolder(View view) {
				super(view);
				owner = (TextView) view.findViewById(R.id.grid_item_owner);
				image = (ImageView) view.findViewById(R.id.grid_item_image);
			}
		}

		public void setObjectTapListener(GridItemObjectTapListener listener) {
			mTapListener = listener;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.layout_grid_item, null);

			ViewHolder viewHolder = new ViewHolder(view);
			return viewHolder;
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {

			final Photo item = mItems.get(position);
			holder.owner.setText(item.getOwnerName());
			Picasso.with(holder.image.getContext()).cancelRequest(holder.image);
			Picasso.with(holder.image.getContext()).load(FlikrApiUrls.getPhotoUrl(item,
				PhotoSize.MEDIUM_640)).into(holder.image);
			holder.image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (item != null)
						mTapListener.itemTap(mItems.indexOf(item));
				}
			});
			holder.itemView.setTag(item);

			// infinite scrolling checker
			if (position == mItems.size() - 1 && mCurrentPage <= mTotalPageCount) {
				if (mCurrentPage == mTotalPageCount) {
					Toast.makeText(RecyclerTest.this, "No more pages!", Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, "mCurrentPage: " + String.valueOf(mCurrentPage));
					int newPage = mCurrentPage + 1;
					Log.d(TAG, "newPage: " + String.valueOf(newPage));
					loadImageData(newPage);
				}
			}
		}

		@Override
		public int getItemCount() {
			return mItems.size();
		}

		public void setItems(List<Photo> photoList, int currentPage, int totalPages) {
			mItems = photoList;
			mCurrentPage = currentPage;
			mTotalPageCount = totalPages;
		}

		public void addItems(List<Photo> photoList, int currentPage, int totalPages) {
			for (Photo photo : photoList) {
				mItems.add(photo);
			}
			mCurrentPage = currentPage;
			mTotalPageCount = totalPages;
			mAdapter.notifyDataSetChanged();
		}

		public Photo getItemAt(int position) {
			if (position < mItems.size())
				return mItems.get(position);
			return null;
		}
	}

}
