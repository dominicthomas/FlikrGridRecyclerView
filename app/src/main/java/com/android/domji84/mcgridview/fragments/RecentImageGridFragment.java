package com.android.domji84.mcgridview.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.android.domji84.mcgridview.ImageViewerActivity;
import com.android.domji84.mcgridview.R;
import com.android.domji84.mcgridview.adapters.GridItemAdapter;
import com.android.domji84.mcgridview.api.FlikrApiClient;
import com.android.domji84.mcgridview.api.model.Photo;
import com.android.domji84.mcgridview.api.model.Recent;
import com.android.domji84.mcgridview.interfaces.GridItemObjectTapListener;
import com.android.domji84.mcgridview.interfaces.LoadImagesListener;

import org.apache.http.HttpStatus;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.android.domji84.mcgridview.api.FlikrApiClient.FlikrApiUrls.*;
import static com.android.domji84.mcgridview.api.FlikrApiClient.getFlikrApiClient;

/**
 * Created by domji84 on 14/11/14.
 */
public class RecentImageGridFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	private static final String TAG = RecentImageGridFragment.class.getSimpleName();

	private RecyclerView mRecyclerView;

	private GridItemAdapter mAdapter;

	private GridLayoutManager mLayoutManager;

	private SwipeRefreshLayout mSwipeRefreshLayout;

	private View mFragmentView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mFragmentView = inflater.inflate(R.layout.fragment_refresh_recycler, container, false);
		return mFragmentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mRecyclerView.forceLayout();
		mLayoutManager = new GridLayoutManager(getActivity(), 2); // initial span count
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new GridItemAdapter(mGridItemObjectTapListener, mLoadImagesListener);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setHasFixedSize(true);

		// calculate span count using recycler view width and card width
		mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
			new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					int viewWidth = mRecyclerView.getMeasuredWidth();
					float cardViewWidth = getActivity().getResources().getDimension(R.dimen.cardview_layout_width);
					int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
					mLayoutManager.setSpanCount(newSpanCount);
				}
			});

		// load first page
		loadImageData(1);
	}

	private final GridItemObjectTapListener mGridItemObjectTapListener = new GridItemObjectTapListener() {
		@Override
		public void itemTap(View view, int position) {
			Toast.makeText(getActivity(), "tapped " + position, Toast.LENGTH_SHORT).show();
			final Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
			final Bundle activityOptions  = ActivityOptionsCompat.makeScaleUpAnimation(
				view, 0, 0, view.getWidth(), view.getHeight()).toBundle();
			intent.putExtra("url", getPhotoUrl(mAdapter.getItemAt(position)));
			startActivity(intent, activityOptions);
		}
	};

	private final LoadImagesListener mLoadImagesListener = new LoadImagesListener() {
		@Override
		public void loadPage(int page) {
			loadImageData(page);
		}

		@Override
		public void noMorePages() {
			Toast.makeText(getActivity(), "No more pages!", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onRefresh() {
		loadImageData(1); // load first page
	}

	private void setupGridAdapterWithResult(int page, Recent recent) {
		final List<Photo> photoList = recent.getPhotos().getPhotoList();
		final int currentPage = recent.getPhotos().getPage();
		final int totalPages = recent.getPhotos().getPages();
		mSwipeRefreshLayout.setRefreshing(false);
		if (photoList != null) {
			if (page == 1) { // first page
				mAdapter.setItems(photoList, currentPage, totalPages);
			} else {
				mAdapter.addItems(photoList, currentPage, totalPages);
			}
		}
	}

	public void loadImageData(final int page) {
		getFlikrApiClient().getRecentPhotos(
			FlikrApiClient.FlikrApiParams.getRecentParams(page), new Callback<Recent>() {
				@Override
				public void success(Recent recent, Response response) {
					if (response.getStatus() == HttpStatus.SC_OK) {
						setupGridAdapterWithResult(page, recent);
					} else {
						// TODO: show error fragment!
					}
				}

				@Override
				public void failure(RetrofitError error) {
					// TODO: show error fragment!
				}
			});
	}

}
