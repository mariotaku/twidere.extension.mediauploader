package org.mariotaku.twidere.extension.mediauploader.activity;

import org.mariotaku.twidere.Twidere;
import org.mariotaku.twidere.extension.mediauploader.fragment.SettingsFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	private static final int REQUEST_REQUEST_PERMISSIONS = 101;

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch (requestCode) {
			case REQUEST_REQUEST_PERMISSIONS: {
				finish();
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			if (!Twidere.isPermissionGranted(this)) {
				final Intent intent = new Intent(Twidere.INTENT_ACTION_REQUEST_PERMISSIONS);
				intent.setPackage(Twidere.APP_PACKAGE_NAME);
				startActivityForResult(intent, REQUEST_REQUEST_PERMISSIONS);
			}
		} catch (final SecurityException e) {
			finish();
			return;
		}
		final FragmentManager fm = getFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.content, new SettingsFragment());
		ft.commit();
	}

}
