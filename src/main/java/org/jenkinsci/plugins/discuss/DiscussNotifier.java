package org.jenkinsci.plugins.discuss;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.DataBoundConstructor;

public class DiscussNotifier extends Notifier {

	public final String apiKey;
	public final String topicNumber;
	public final boolean notifyWhenSuccess;

	@DataBoundConstructor
	public DiscussNotifier(String apiKey, String topicNumber,
			boolean notifyWhenSuccess) {
		this.apiKey = apiKey;
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

		Long topicId = Long.valueOf(topicNumber);

		String buildSummary = makeBuildSummary(build);
		String buildUrl = Jenkins.getInstance().getRootUrl() + build.getUrl();
		String message = buildSummary + buildUrl;

		Discuss discuss = new Discuss(apiKey);
		discuss.postMessage(topicId, message);

		return true;
	}


	private String makeBuildSummary(AbstractBuild<?, ?> build) {
		final String buildSummary;
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
		return buildSummary;
	}

}
