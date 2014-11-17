package com.android.domji84.mcgridview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class RecyclerTest extends ActionBarActivity {

	private RecyclerView recyclerView;
	private MyAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

	public interface MyObjectTapListener {
		public void itemTap(int position);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recycler_test);
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		mLayoutManager = new GridLayoutManager(this, 2);
		recyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new MyAdapter();
		mAdapter.setObjectTapListener(new MyObjectTapListener() {
			@Override
			public void itemTap(int position) {
				Toast.makeText(getApplicationContext(), "tapped " + position, Toast.LENGTH_SHORT).show();
			}
		});
		recyclerView.setAdapter(mAdapter);

		loadImages();
	}

	private void loadImages() {
		getFlikrApiClient().getRecentPhotos(
			FlikrApiClient.FlikrApiParams.getRecentParams(0), // TODO: track page number
			new Callback<Recent>() {
				@Override
				public void success(Recent recent, Response response) {
					mAdapter.setItems(recent.getPhotos().getPhotoList());
					mAdapter.notifyDataSetChanged();
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
		 * Our view holder class
		 */
		public class ViewHolder extends RecyclerView.ViewHolder {
			public ImageView image;

			public ViewHolder(View v) {
				super(v);
				image = (ImageView) v.findViewById(R.id.grid_item_image);
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

		public Photo getItemAt(int position) {
			if (position < items.size())
				return items.get(position);
			return null;
		}

		@Override
		public int getItemCount() {
			return items.size();
		}

		public void setItems(List<Photo> photoList) {
			items = photoList;
		}
	}
}
