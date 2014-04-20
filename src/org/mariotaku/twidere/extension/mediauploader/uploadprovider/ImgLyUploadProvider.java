package org.mariotaku.twidere.extension.mediauploader.uploadprovider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.mariotaku.twidere.model.Account.AccountWithCredentials;
import org.mariotaku.twidere.model.ParcelableStatusUpdate;
import org.mariotaku.twidere.model.UploaderMediaItem;

import twitter4j.TwitterConstants;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.http.HttpParameter;
import twitter4j.http.HttpResponse;

public class ImgLyUploadProvider extends AbsUploadProviderImpl {

	public static final String URL_IMGLY_UPLOAD = "http://img.ly/api/2/upload.json";

	public ImgLyUploadProvider(final Configuration conf, final AccountWithCredentials account) {
		super(conf, account);
	}

	@Override
	public String upload(final UploaderMediaItem item, final ParcelableStatusUpdate status) throws TwitterException {
		final String verifyCredentialsAuthorizationHeader = generateVerifyCredentialsAuthorizationHeader();
		final Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Service-Provider", conf.getSigningRestBaseURL()
				+ TwitterConstants.ENDPOINT_ACCOUNT_VERIFY_CREDENTIALS);
		headers.put("X-Verify-Credentials-Authorization", verifyCredentialsAuthorizationHeader);
		final List<HttpParameter> paramsList = new ArrayList<HttpParameter>();
		paramsList.add(getMediaParameter("media", item));
		paramsList.add(new HttpParameter("message", status.text));
		final HttpParameter[] params = paramsList.toArray(new HttpParameter[paramsList.size()]);
		final HttpResponse resp = post(URL_IMGLY_UPLOAD, params, headers);
		final JSONObject json = resp.asJSONObject();
		if (json.isNull("url")) throw new TwitterException("Invalid response", resp);
		return json.optString("url");
	}

}
