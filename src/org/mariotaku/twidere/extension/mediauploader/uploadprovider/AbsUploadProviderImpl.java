package org.mariotaku.twidere.extension.mediauploader.uploadprovider;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.mariotaku.twidere.extension.mediauploader.Constants;
import org.mariotaku.twidere.model.Account.AccountWithCredentials;
import org.mariotaku.twidere.model.UploaderMediaItem;

import twitter4j.TwitterConstants;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.HttpClientWrapper;
import twitter4j.http.HttpParameter;
import twitter4j.http.HttpResponse;

public abstract class AbsUploadProviderImpl implements Constants, UploaderProvider {

	protected final AccountWithCredentials account;
	protected final OAuthAuthorization oauth;
	protected final HttpClientWrapper client;
	protected final Configuration conf;

	public AbsUploadProviderImpl(final Configuration confOrig, final AccountWithCredentials account) {
		this.account = account;
		final ConfigurationBuilder cb = new ConfigurationBuilder(confOrig);
		cb.setOAuthConsumerKey(account.consumer_key);
		cb.setOAuthConsumerSecret(account.consumer_secret);
		cb.setRestBaseURL(account.rest_base_url);
		cb.setOAuthBaseURL(account.oauth_base_url);
		cb.setSigningRestBaseURL(account.signing_rest_base_url);
		cb.setSigningOAuthBaseURL(account.signing_oauth_base_url);
		conf = cb.build();
		oauth = new OAuthAuthorization(conf);
		oauth.setOAuthAccessToken(new AccessToken(account.oauth_token, account.oauth_token_secret));
		oauth.setOAuthRealm("https://api.twitter.com/");
		client = new HttpClientWrapper(confOrig);
	}

	protected String generateVerifyCredentialsAuthorizationHeader() {
		final List<HttpParameter> oauthSignatureParams = oauth.generateOAuthSignatureHttpParams("GET",
				conf.getSigningRestBaseURL() + TwitterConstants.ENDPOINT_ACCOUNT_VERIFY_CREDENTIALS);
		return String.format("OAuth %s", OAuthAuthorization.encodeParameters(oauthSignatureParams, ",", true));
	}

	protected HttpResponse post(final String url, final HttpParameter[] parameters,
			final Map<String, String> requestHeaders) throws TwitterException {
		return client.post(url, url, parameters, requestHeaders);
	}

	protected static HttpParameter getMediaParameter(final String key, final UploaderMediaItem item) {
		final String fileName = item.path != null ? getFileName(item.path) : "media_file";
		return new HttpParameter(key, fileName, new FileInputStream(item.fd.getFileDescriptor()));
	}

	private static String getFileName(final String path) {
		if (path == null) return null;
		final int idx = path.lastIndexOf(File.separator);
		return idx >= 0 ? path.substring(idx + 1) : path;
	}

}
