package com.android.domji84.mcgridview;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import static com.android.domji84.mcgridview.app.AppConstants.*;

/**
 * Created by domji84 on 18/11/14.
 */
public class ImageViewerActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
		setContentView(R.layout.activity_image_viewer);

		// TODO: implement image viewing
		String value = getIntent().getExtras().getString(KEY_IMAGE_URL);
	}
}
