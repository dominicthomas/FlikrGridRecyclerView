package com.android.domji84.mcgridview.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.domji84.mcgridview.R;
import com.google.common.base.Optional;

import static com.android.domji84.mcgridview.app.AppConstants.KEY_ERROR_MESSAGE;

/**
 * Created by domji84 on 19/11/14.
 */
public class ErrorFragment extends Fragment {

	private Optional<String> mErrorMessageOptional = Optional.absent();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null){
			mErrorMessageOptional = Optional.of(getArguments().getString(KEY_ERROR_MESSAGE));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_error, container, false);
		if(mErrorMessageOptional.isPresent()){
			((TextView) view.findViewById(R.id.error_fragment_message)).setText(mErrorMessageOptional.get());
		}
		return view;
	}

}
