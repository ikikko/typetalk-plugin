package org.jenkinsci.plugins.typetalk;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

public class Typetalk {

	private static final String BASE_URL = "https://typetalk.in";

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private static final HttpRequestFactory REQUEST_FACTORY = HTTP_TRANSPORT
			.createRequestFactory(new HttpRequestInitializer() {

				@Override
				public void initialize(HttpRequest request) throws IOException {
					request.setParser(new JsonObjectParser(JSON_FACTORY));

				}
			});

	private final String apiKey;

	public Typetalk(final String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Typetalkにメッセージを通知する
	 */
	public void postMessage(final Long topicId, final String message)
			throws IOException {
		final GenericUrl url = new PostMessageUrl(apiKey, topicId);
		final Message messageObj = new Message();
		messageObj.setTopicId(topicId);
		messageObj.setMessage(message);
		final HttpContent content = new JsonHttpContent(JSON_FACTORY,
				messageObj);
		final HttpRequest request = REQUEST_FACTORY.buildPostRequest(url,
				content);
		HttpResponse response = request.execute();
		response.disconnect();
	}

	static class PostMessageUrl extends GenericUrl {

		PostMessageUrl(String apiKey, Long topicId) {
			super(getBaseUrl() + "/api/v1/topics/" + topicId + "?key=" + apiKey);
		}

		private static String getBaseUrl() {
			String baseUrl = System.getenv("TYPETALK_BASE_URL");
			return StringUtils.isEmpty(baseUrl) ? BASE_URL : baseUrl;
		}

	}

}
