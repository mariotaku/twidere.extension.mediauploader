package org.mariotaku.twidere.extension.mediauploader.uploadprovider;

import org.mariotaku.twidere.model.ParcelableStatusUpdate;
import org.mariotaku.twidere.model.UploaderMediaItem;

import twitter4j.TwitterException;

public interface UploaderProvider {

	public String upload(UploaderMediaItem item,ParcelableStatusUpdate status) throws TwitterException;

}