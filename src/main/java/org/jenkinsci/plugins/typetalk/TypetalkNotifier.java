package org.jenkinsci.plugins.typetalk;

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
import hudson.util.Secret;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

public class TypetalkNotifier extends Notifier {

	public final Secret apiKey;
	public final String topicNumber;
	public final boolean notifyWhenSuccess;

	@DataBoundConstructor
	public TypetalkNotifier(String apiKey, String topicNumber,
			boolean notifyWhenSuccess) {
		this.apiKey = Secret.fromString(apiKey);
		this.topicNumber = topicNumber;
		this.notifyWhenSuccess = notifyWhenSuccess;
	}

	// for test
	TypetalkNotifier(boolean notifyWhenSuccess) {
		this.apiKey = null;
		this.topicNumber = null;
		this.notifyWhenSuccess = notifyWhenSuccess;
	}

	public String getApiKey() {
		return Secret.toString(apiKey);
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException {
		String buildSummary = toBuildSummary(build);
		if (buildSummary == null) {
			return true;
		}

		final String rootUrl = Jenkins.getInstance().getRootUrl();
		if (StringUtils.isEmpty(rootUrl)) {
			throw new IllegalStateException("Root URL isn't configured yet. Cannot compute absolute URL.");
		}

		String message = makeMessage(build, buildSummary, rootUrl);
		Long topicId = Long.valueOf(topicNumber);

		// Typetalkに通知中...
		listener.getLogger().println("Notifying to the typetalk...");

		Typetalk typetalk = new Typetalk(getApiKey());
		typetalk.postMessage(topicId, message);

		return true;
	}

	private String makeMessage(AbstractBuild<?, ?> build, String buildSummary, String rootUrl) {
		final StringBuilder message = new StringBuilder();
		message.append(buildSummary);
		message.append(" [ project : ");
		message.append(build.getProject().getDisplayName());
		message.append(" ]");
		message.append("\n");
		message.append(rootUrl);
		message.append(build.getUrl());
		return message.toString();
	}

	/**
	 * ビルドの要約メッセージを取得する。
	 * 
	 * @param build
	 *            ビルド
	 * @return 通知対象ならばビルドの要約メッセージ、通知対象でないならば {@code null}
	 */
	private String toBuildSummary(AbstractBuild<?, ?> build) {
		// ビルドが成功に戻った場合
		if (build.getResult().equals(Result.SUCCESS)
				&& build.getPreviousBuild() != null
				&& build.getPreviousBuild().getResult().isWorseThan(Result.SUCCESS)) {
			return ":smiley: Build recovered.";
		}

		// ビルド成功で "ビルドが成功した場合も通知する" がオフの場合、通知しない
		if (build.getResult().equals(Result.SUCCESS) && notifyWhenSuccess == false) {
			return null;
		}

		if (build.getResult().equals(Result.ABORTED)) {
			return ":astonished: Build aborted.";
		} else if (build.getResult().equals(Result.FAILURE)) {
			return ":rage: Build failure.";
		} else if (build.getResult().equals(Result.UNSTABLE)) {
			return ":cry: Build unstable.";
		} else if (build.getResult().equals(Result.SUCCESS)) {
			return ":smiley: Build successful.";
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
			return "Typetalkに通知";
		}
	}

}
