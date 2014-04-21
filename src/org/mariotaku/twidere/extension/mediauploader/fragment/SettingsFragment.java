package org.mariotaku.twidere.extension.mediauploader.fragment;

import org.mariotaku.twidere.extension.mediauploader.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

}
