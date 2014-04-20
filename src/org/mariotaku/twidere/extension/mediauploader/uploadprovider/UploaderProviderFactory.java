package org.mariotaku.twidere.extension.mediauploader.uploadprovider;

import org.mariotaku.twidere.model.Account.AccountWithCredentials;

import twitter4j.conf.Configuration;

public class UploaderProviderFactory {
	
	private Configuration conf;

	public UploaderProviderFactory(Configuration conf) {
		this.conf = conf;
	}

	public UploaderProvider newInstance(final AccountWithCredentials account, final String providerName) {
		if (providerName == null) throw new NullPointerException();
		if ("twitpic".equalsIgnoreCase(providerName)) return new TwitPicUploadProvider(conf, account);
		if ("imgly".equalsIgnoreCase(providerName)) return new ImgLyUploadProvider(conf, account);
		throw new IllegalArgumentException(String.format("Unrecognized provider %s", providerName));
	}
}
