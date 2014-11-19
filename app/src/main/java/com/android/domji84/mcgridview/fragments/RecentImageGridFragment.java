package com.android.domji84.mcgridview.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.domji84.mcgridview.app.AppConstants;
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

import static com.android.domji84.mcgridview.app.AppConstants.KEY_ERROR_MESSAGE;
import static com.android.domji84.mcgridview.app.AppConstants.TAG_ERROR_FRAGMENT;
import static com.android.domji84.mcgridview.api.FlikrApiClient.FlikrApiUrls.getPhotoUrl;
import static com.android.domji84.mcgridview.api.FlikrApiClient.PhotoSize.*;
import static com.android.domji84.mcgridview.api.FlikrApiClient.getFlikrApiClient;

/**
 * Created by domji84 on 14/11/14.
 */
public class RecentImageGridFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

	private static final int DEFAULT_SPAN_COUNT = 2;

	private RecyclerView mRecyclerView;

	private GridItemAdapter mAdapter;

	private GridLayoutManager mLayoutManager;

	private SwipeRefreshLayout mSwipeRefreshLayout;

	private ProgressBar mProgressBar;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_refresh_recycler, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setOnRefreshListener(this);

		mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAnimationCacheEnabled(true);
		mLayoutManager = new GridLayoutManager(getActivity(), DEFAULT_SPAN_COUNT);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new GridItemAdapter(getActivity(), mGridItemObjectTapListener, mLoadImagesListener);
		mRecyclerView.setAdapter(mAdapter);
		calculateRecyclerViewSpanCount(mRecyclerView, mLayoutManager);

		mProgressBar = (ProgressBar) view.findViewById(R.id.recycler_progress_bar);
		loadImageData(1, true);
	}

	private void calculateRecyclerViewSpanCount(final RecyclerView recyclerView, final GridLayoutManager layoutManager) {
		recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
			new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					// calculate span  using recyclerview width and cardview width
					recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					int viewWidth = recyclerView.getMeasuredWidth();
					float cardViewWidth = getActivity().getResources().getDimension(R.dimen.cardview_layout_width);
					int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
					layoutManager.setSpanCount(viewWidth == 0 ? 1 : newSpanCount);
				}
			});

	}

	private final GridItemObjectTapListener mGridItemObjectTapListener = new GridItemObjectTapListener() {
		@Override
		public void itemTap(View view, int position) {
			Toast.makeText(getActivity(), "Tapped " + position, Toast.LENGTH_SHORT).show();
			final Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
			final Bundle activityOptions = ActivityOptionsCompat.makeScaleUpAnimation(
				view, 0, 0, view.getWidth(), view.getHeight()).toBundle();

			// will get a large image for the preview activity
			intent.putExtra(AppConstants.KEY_IMAGE_URL, getPhotoUrl(mAdapter.getItemAt(position), MEDIUM_640));
			checkAndStartActivity(intent, activityOptions);
		}
	};

	public void checkAndStartActivity(Intent intent, Bundle options) {
		if (Build.VERSION.SDK_INT >= 16) {
			ActivityCompat.startActivity(getActivity(), intent, options);
		} else {
			getActivity().startActivity(intent);
		}
	}

	private final LoadImagesListener mLoadImagesListener = new LoadImagesListener() {
		@Override
		public void loadPage(int page) {
			loadImageData(page, true);
		}

		@Override
		public void noMorePages() {
			Toast.makeText(getActivity(), getActivity().getString(
				R.string.no_more_pages_message), Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onRefresh() {
		// load first page
		loadImageData(1, isErrorFragmentAdded() ? true : false);
	}

	private void setupGridAdapterWithResult(int page, Recent recent) {
		final List<Photo> photoList = recent.getPhotos().getPhotoList();
		final int currentPage = recent.getPhotos().getPage();
		final int totalPages = recent.getPhotos().getPages();
		mSwipeRefreshLayout.setRefreshing(false);
		if (photoList != null) {
			if (page == 1) { // is first page
				mAdapter.setItems(photoList, currentPage, totalPages);
			} else {
				mAdapter.addItems(photoList, currentPage, totalPages);
			}
		}
	}

	public void loadImageData(final int page, boolean showLoading) {
		// don't show progress bar when loading first page
		showLoadingSpinner(showLoading);
		removeErrorFragment();
		getFlikrApiClient().getRecentPhotos(
			FlikrApiClient.FlikrApiParams.getRecentParams(page), new Callback<Recent>() {
				@Override
				public void success(Recent recent, Response response) {
					if (response.getStatus() == HttpStatus.SC_OK) {
						setupGridAdapterWithResult(page, recent);
					} else {
						handleGetRecentErrors(RetrofitError.Kind.HTTP);
					}
					showLoadingSpinner(false);
				}

				@Override
				public void failure(RetrofitError error) {
					showLoadingSpinner(false);
					handleGetRecentErrors(error.getKind());
				}
			});
	}

	private void handleGetRecentErrors(RetrofitError.Kind errorKind) {
		mSwipeRefreshLayout.setRefreshing(false);
		showLoadingSpinner(false);
		switch (errorKind) {
			case HTTP:
			case CONVERSION:
			case UNEXPECTED:
				addErrorFragment(R.string.api_error_message);
				break;
			case NETWORK:
				addErrorFragment(R.string.network_error_message);
				break;
			default:
				addErrorFragment(R.string.api_error_message);
		}
	}

	private void showLoadingSpinner(boolean show) {
		if (show) {
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			mProgressBar.setVisibility(View.GONE);
		}
	}

	private void showRecyclerView(boolean show) {
		if (show) {
			mRecyclerView.setVisibility(View.VISIBLE);
		} else {
			mRecyclerView.setVisibility(View.GONE);
		}
	}

	private boolean isErrorFragmentAdded() {
		return getActivity().getSupportFragmentManager()
			.findFragmentByTag(TAG_ERROR_FRAGMENT) != null;
	}

	private void addErrorFragment(int messageId) {
		if (!isErrorFragmentAdded()) {
			showRecyclerView(false);

			final Fragment errorFragment = new ErrorFragment();
			final Bundle bundle = new Bundle();
			bundle.putString(KEY_ERROR_MESSAGE, getActivity().getString(messageId));
			errorFragment.setArguments(bundle);

			getActivity().getSupportFragmentManager().beginTransaction()
				.add(R.id.recycler_error_container, errorFragment, TAG_ERROR_FRAGMENT)
				.commit();
		}
	}

	private void removeErrorFragment() {
		if (isErrorFragmentAdded()) {
			showRecyclerView(true);
			getActivity().getSupportFragmentManager()
				.beginTransaction()
				.remove(getActivity().getSupportFragmentManager()
					.findFragmentByTag(TAG_ERROR_FRAGMENT))
				.commit();
		}
	}

}
