package org.jenkinsci.plugins.discuss

import hudson.model.AbstractBuild
import hudson.model.Result
import spock.lang.Specification

class DiscussNotifierSpec extends Specification {

	def "各種条件に応じた toBuildSummary のテストを行う"() {
		setup:
		def notifier = new DiscussNotifier(notifyWhenSuccess)
		def build = makeMockBuild(result, previousResult)

		expect:
		expected == notifier.toBuildSummary(build)

		where:
		notifyWhenSuccess | previousResult | result          | expected
		false             | Result.SUCCESS | Result.SUCCESS  | null
		false             | Result.SUCCESS | Result.UNSTABLE | 'Build unstable.'
		false             | Result.SUCCESS | Result.FAILURE  | 'Build failure.'
		false             | Result.SUCCESS | Result.ABORTED  | 'Build aborted.'

		false             | Result.FAILURE | Result.SUCCESS  | 'Build recovered.'
		false             | Result.FAILURE | Result.UNSTABLE | 'Build unstable.'
		false             | Result.FAILURE | Result.FAILURE  | 'Build failure.'
		false             | Result.FAILURE | Result.ABORTED  | 'Build aborted.'

		true              | Result.SUCCESS | Result.SUCCESS  | 'Build successful.'
		true              | Result.SUCCESS | Result.UNSTABLE | 'Build unstable.'
		true              | Result.SUCCESS | Result.FAILURE  | 'Build failure.'
		true              | Result.SUCCESS | Result.ABORTED  | 'Build aborted.'

		true              | Result.FAILURE | Result.SUCCESS  | 'Build recovered.'
		true              | Result.FAILURE | Result.UNSTABLE | 'Build unstable.'
		true              | Result.FAILURE | Result.FAILURE  | 'Build failure.'
		true              | Result.FAILURE | Result.ABORTED  | 'Build aborted.'
	}

	def makeMockBuild(Result result, Result previousResult) {
		def build = Mock(AbstractBuild)

		build.result >> result
		build.previousBuild >> {
			def previousBuild = Mock(AbstractBuild)
			previousBuild.result >> previousResult

			return previousBuild
		}

		return build
	}
}
