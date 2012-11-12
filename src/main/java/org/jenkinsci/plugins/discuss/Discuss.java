package org.jenkinsci.plugins.discuss;

import java.io.IOException;
import java.net.MalformedURLException;

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

public class Discuss {

	private static final String BASE_URL = "https://discuss.nulab.co.jp";
	private static final String API_POST_MESSAGE = "/api/v1/topics/";

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private final String apiKey;

	public Discuss(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Discussにメッセージを通知する
	 */
	public void postMessage(Long topicId, String message) throws IOException,
			MalformedURLException {

		HttpRequestFactory requestFactory = HTTP_TRANSPORT
				.createRequestFactory(new HttpRequestInitializer() {

					@Override
					public void initialize(HttpRequest request)
							throws IOException {
						request.setParser(new JsonObjectParser(JSON_FACTORY));

					}
				});

		GenericUrl url = new GenericUrl(BASE_URL + API_POST_MESSAGE + topicId
				+ "?key=" + apiKey);
		System.out.println(url);

		Message messageObj = new Message();
		messageObj.setTopicId(topicId);
		messageObj.setMessage(message);
		HttpContent content = new JsonHttpContent(JSON_FACTORY, messageObj);

		HttpRequest request = requestFactory.buildPostRequest(url, content);
		HttpResponse result = request.execute();
		System.out.println(result);
	}
}
