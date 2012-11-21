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

		// Discussに通知中...
		listener.getLogger().println("Notifying to the discuss...");

		Long topicId = Long.valueOf(topicNumber);
		String message = makeMessage(build);

		Discuss discuss = new Discuss(apiKey);
		discuss.postMessage(topicId, message);

		return true;
	}

	private String makeMessage(AbstractBuild<?, ?> build) {
		final StringBuilder message = new StringBuilder();
		message.append(toBuildSummary(build));
		message.append(" [project: ");
		message.append(build.getProject().getDisplayName());
		message.append("]");
		message.append("\n");
		final String rootUrl = Jenkins.getInstance().getRootUrl();
		if (rootUrl == null) {
			throw new IllegalStateException(
					"Root URL isn't configured yet. Cannot compute absolute URL.");
		}
		message.append(rootUrl);
		message.append(build.getUrl());
		return message.toString();
	}

	private String toBuildSummary(AbstractBuild<?, ?> build) {
		if (build.getResult().equals(Result.ABORTED)) {
			return "Build aborted.";
		} else if (build.getResult().equals(Result.FAILURE)) {
			return "Build failure.";
		} else if (build.getResult().equals(Result.UNSTABLE)) {
			return "Build unstable.";
		} else if (build.getResult().equals(Result.SUCCESS)) {
			return "Build successful.";
		}
		throw new RuntimeException("Unknown build result.");
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
