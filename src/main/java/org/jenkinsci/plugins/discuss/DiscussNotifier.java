package org.jenkinsci.plugins.discuss;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.DataBoundConstructor;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class DiscussNotifier extends Notifier {

	private static final String DISCUSS_URL = "https://discuss.nulab.co.jp";
	private static final String DISCUSS_SIGNIN_URL = DISCUSS_URL + "/signin";
	private static final String ACTION_URI = "/teams/nulab/topics";
	private static final String DISCUSS_TOPICS_URL = DISCUSS_URL + ACTION_URI;

	public final String name;
	public final String password;
	public final String topicNumber;
	public final boolean notifyWhenSuccess;

	private WebClient client;

	@DataBoundConstructor
	public DiscussNotifier(String name, String password, String topicNumber,
			boolean notifyWhenSuccess) {
		this.name = name;
		this.password = password;
		this.topicNumber = topicNumber;
		this.notifyWhenSuccess = notifyWhenSuccess;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		// 成功だったらスキップする("ビルドが成功した場合も通知する"がオフの場合)
		if (build.getResult().equals(Result.SUCCESS)
				&& notifyWhenSuccess == false) {
			return true;
		}

		listener.getLogger().println("Discussに通知中...");

		// TODO HtmlUnit実行時のログが不要に出ているので、制御する
		initClient();
		login();
		notifyMessage(build);

		return true;
	}

	/**
	 * WebClientの初期設定を行う
	 */
	private void initClient() {
		client = new WebClient();
		client.setJavaScriptEnabled(false);
	}

	/**
	 * Discussにログインする
	 */
	private void login() throws IOException, MalformedURLException {
		HtmlPage page = client.getPage(DISCUSS_SIGNIN_URL);

		HtmlForm form = page.getForms().get(0);
		HtmlTextInput nameField = form.getInputByName("name");
		HtmlPasswordInput passwordField = form.getInputByName("password");

		nameField.setValueAttribute(name);
		passwordField.setValueAttribute(password);

		getSubmitButton(form).click();
	}

	/**
	 * Discussにメッセージを通知する
	 */
	private void notifyMessage(AbstractBuild<?, ?> build) throws IOException,
			MalformedURLException {
		String buildSummary;
		if (build.getResult().equals(Result.ABORTED)) {
			buildSummary = "ビルドが中止されました\n";
		} else if (build.getResult().equals(Result.FAILURE)) {
			buildSummary = "ビルドが失敗しました\n";
		} else if (build.getResult().equals(Result.UNSTABLE)) {
			buildSummary = "ビルドが不安定です\n";
		} else if (build.getResult().equals(Result.SUCCESS)) {
			buildSummary = "ビルドが成功しました\n";
		} else {
			throw new RuntimeException("ビルドの結果が、想定していない状態です");
		}
		String buildUrl = Jenkins.getInstance().getRootUrl() + build.getUrl();

		HtmlPage page = client.getPage(DISCUSS_TOPICS_URL + "/" + topicNumber);
		HtmlForm form = (HtmlForm) page.getByXPath(
				"//form[@action='" + ACTION_URI + "']").get(0);
		HtmlTextArea message = (HtmlTextArea) form.getElementsByTagName(
				"textarea").get(0);
		message.setText(buildSummary + buildUrl);

		// form.getActionAttribute にはトピック番号が付いていないので、自分で付ける
		form.setActionAttribute(ACTION_URI + "/" + topicNumber);

		getSubmitButton(form).click();
	}

	/**
	 * フォームのsubmitボタンを取得する
	 */
	private HtmlSubmitInput getSubmitButton(HtmlForm form) {
		List<?> submits = form.getByXPath("//input[@type='submit']");

		return (HtmlSubmitInput) submits.get(0);
	}

	@Extension
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Discussに通知";
		}
	}

}
