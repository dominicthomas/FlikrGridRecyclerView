package com.android.domji84.mcgridview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.android.domji84.mcgridview.fragments.RecentImageGridFragment;


public class HomeActivity extends ActionBarActivity {

	private static final String TAG = HomeActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new RecentImageGridFragment())
				.commit();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
