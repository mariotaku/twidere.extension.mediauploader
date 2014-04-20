package org.mariotaku.twidere.extension.mediauploader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.mariotaku.twidere.IMediaUploader;
import org.mariotaku.twidere.Twidere;
import org.mariotaku.twidere.extension.mediauploader.uploadprovider.UploaderProvider;
import org.mariotaku.twidere.extension.mediauploader.uploadprovider.UploaderProviderFactory;
import org.mariotaku.twidere.model.Account;
import org.mariotaku.twidere.model.Account.AccountWithCredentials;
import org.mariotaku.twidere.model.MediaUploadResult;
import org.mariotaku.twidere.model.ParcelableStatusUpdate;
import org.mariotaku.twidere.model.UploaderMediaItem;

import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Sample media uploader
 * 
 * @author mariotaku
 */
public class MediaUploaderService extends Service implements Constants {

	private final ImageUploaderStub mBinder = new ImageUploaderStub(this);

	@Override
	public IBinder onBind(final Intent intent) {
		return mBinder;
	}

	public MediaUploadResult upload(final ParcelableStatusUpdate status, final UploaderMediaItem[] medias) {
		if (!Twidere.isPermissionGranted(this))
			return MediaUploadResult.getInstance(1, getString(R.string.error_no_permission));
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final AccountWithCredentials account = Account.getAccountWithCredentials(this, status.accounts[0].account_id);
		final UploaderProviderFactory f = new UploaderProviderFactory(new ConfigurationBuilder().build());
		final UploaderProvider uploader;
		try {
			uploader = f.newInstance(account, prefs.getString(KEY_MEDIA_UPLOAD_PROVIDER, null));
		} catch (final Exception e) {
			return MediaUploadResult.getInstance(1, getString(R.string.error_provider_not_specified));
		}
		final List<String> result = new ArrayList<String>();
		try {
			for (final UploaderMediaItem item : medias) {
				result.add(uploader.upload(item, status));
			}
		} catch (final TwitterException e) {
			Log.w(LOGTAG, e);
			final int errorCode = e.getErrorCode() != 0 ? e.getErrorCode() : -1;
			final String errorMessage = e.getErrorMessage() != null ? e.getErrorMessage() : e.getMessage();
			return MediaUploadResult.getInstance(errorCode, errorMessage);
		}
		return MediaUploadResult.getInstance(result.toArray(new String[result.size()]));
	}

	/*
	 * By making this a static class with a WeakReference to the Service, we
	 * ensure that the Service can be GCd even when the system process still has
	 * a remote reference to the stub.
	 */
	private static final class ImageUploaderStub extends IMediaUploader.Stub {

		final WeakReference<MediaUploaderService> mService;

		public ImageUploaderStub(final MediaUploaderService service) {
			mService = new WeakReference<MediaUploaderService>(service);
		}

		@Override
		public MediaUploadResult upload(final ParcelableStatusUpdate status, final UploaderMediaItem[] medias)
				throws RemoteException {
			return mService.get().upload(status, medias);
		}

	}

}
