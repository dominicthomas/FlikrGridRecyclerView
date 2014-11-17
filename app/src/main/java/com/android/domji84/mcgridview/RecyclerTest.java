package com.android.domji84.mcgridview;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
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

	private RecyclerView mRecyclerView;
	private MyAdapter mAdapter;
	private GridLayoutManager mLayoutManager;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	public interface MyObjectTapListener {
		public void itemTap(int position);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recycler_test);
		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mLayoutManager = new GridLayoutManager(this, 3);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new MyAdapter();
		mAdapter.setObjectTapListener(new MyObjectTapListener() {
			@Override
			public void itemTap(int position) {
				Toast.makeText(getApplicationContext(), "tapped " + position, Toast.LENGTH_SHORT).show();
			}
		});
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setHasFixedSize(true);
		loadImageData(0);
	}

	@Override
	public void onRefresh() {
		loadImageData(0);
	}

	private void loadImageData(int page) {
		getFlikrApiClient().getRecentPhotos(
			FlikrApiClient.FlikrApiParams.getRecentParams(page, Lists.newArrayList("owner_name")), // TODO: track page number
			new Callback<Recent>() {
				@Override
				public void success(Recent recent, Response response) {
					mAdapter.setItems(recent.getPhotos().getPhotoList());
					mAdapter.notifyDataSetChanged();
					mSwipeRefreshLayout.setRefreshing(false);
				}

				@Override
				public void failure(RetrofitError error) {

				}
			});
	}

	public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

		private List<Photo> items = new ArrayList<Photo>();
		private MyObjectTapListener tapListener;

		/**
		 * The view holder
		 */
		public class ViewHolder extends RecyclerView.ViewHolder {

			public ImageView image;
			public TextView owner;

			public ViewHolder(View view) {
				super(view);
				owner = (TextView) view.findViewById(R.id.grid_item_owner);
				image = (ImageView) view.findViewById(R.id.grid_item_image);
			}
		}

		/**
		 * To set the listener we use when an image is clicked
		 *
		 * @param listener
		 */
		public void setObjectTapListener(MyObjectTapListener listener) {
			tapListener = listener;
		}

		/**
		 * Create new views
		 *
		 * @param parent
		 * @param viewType
		 * @return
		 */
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent,
		                                     int viewType) {
			// create a new view
			View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.layout_grid_item, null);

			ViewHolder vh = new ViewHolder(v);
			return vh;
		}

		/**
		 * Fill the view with data from the adapter
		 *
		 * @param holder
		 * @param position
		 */
		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			final Photo item = items.get(position);
			holder.owner.setText(item.getOwnerName());
			Picasso.with(holder.image.getContext()).cancelRequest(holder.image);
			Picasso.with(holder.image.getContext()).load(FlikrApiUrls.getPhotoUrl(item,
				PhotoSize.MEDIUM_640)).into(holder.image);
			holder.image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (item != null)
						tapListener.itemTap(items.indexOf(item));
				}
			});

			holder.itemView.setTag(item);
		}

		@Override
		public int getItemCount() {
			return items.size();
		}

		public void setItems(List<Photo> photoList) {
			items = photoList;
		}

		public Photo getItemAt(int position) {
			if (position < items.size())
				return items.get(position);
			return null;
		}
	}

}
