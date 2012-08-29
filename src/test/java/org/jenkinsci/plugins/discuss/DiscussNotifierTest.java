package org.jenkinsci.plugins.discuss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

@Ignore("プロダクトコードに組み込んだので、ひとまず不要。単独でhtmlunitの確認をしたい場合は、再度整備する")
public class DiscussNotifierTest {

	private static final String DISCUSS_URL = "https://discuss.nulab.co.jp";
	private static final String DISCUSS_SIGNIN_URL = DISCUSS_URL + "/signin";
	private static final String ACTION_URI = "/teams/nulab/topics";
	private static final String DISCUSS_TOPICS_URL = DISCUSS_URL + ACTION_URI;

	private WebClient client = new WebClient();

	@Test
	public void test() throws Exception {
		// JS読み込み時にエラーが出ることがあるので、JSは無効にする
		client.setJavaScriptEnabled(false);

		login();
		notifyMessage();
	}

	private void login() throws IOException, MalformedURLException {
		HtmlPage page = client.getPage(DISCUSS_SIGNIN_URL);

		HtmlForm form = page.getForms().get(0);
		HtmlTextInput name = form.getInputByName("name");
		HtmlPasswordInput password = form.getInputByName("password");

		name.setValueAttribute("nakamura");
		password.setValueAttribute("tn4575nu");

		getSubmitButton(form).click();
	}

	private void notifyMessage() throws Exception {
		HtmlPage page = client.getPage(DISCUSS_TOPICS_URL + "/9");
		HtmlForm form = (HtmlForm) page.getByXPath(
				"//form[@action='" + ACTION_URI + "']").get(0);
		HtmlTextArea message = (HtmlTextArea) form.getElementsByTagName(
				"textarea").get(0);
		message.setText("テスト : " + new Date());

		// form.getActionAttribute にはトピック番号が付いていないので、自分で付ける
		form.setActionAttribute(ACTION_URI + "/9");

		getSubmitButton(form).click();
	}

	private HtmlSubmitInput getSubmitButton(HtmlForm form) {
		return (HtmlSubmitInput) form.getByXPath("//input[@type='submit']")
				.get(0);
	}
}
