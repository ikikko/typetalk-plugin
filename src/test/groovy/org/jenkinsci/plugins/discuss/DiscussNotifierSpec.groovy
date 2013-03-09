package org.jenkinsci.plugins.discuss

import hudson.model.AbstractBuild
import hudson.model.Result

import org.apache.commons.lang.StringUtils

import spock.lang.Specification

class DiscussNotifierSpec extends Specification {

	def "通知対象かどうかを toBuildSummry で判定する"() {
		setup:
		def notifier = new DiscussNotifier('', '', notifyWhenSuccess)
		def build = makeMockBuild(result)

		when:
		def buildSummary = notifier.toBuildSummary(build)

		then:
		StringUtils.isNotEmpty(buildSummary) == expected

		where:
		notifyWhenSuccess | result          | expected
		false             | Result.SUCCESS  | false
		false             | Result.UNSTABLE | true
		false             | Result.FAILURE  | true
		false             | Result.ABORTED  | true

		true              | Result.SUCCESS  | true
		true              | Result.UNSTABLE | true
		true              | Result.FAILURE  | true
		true              | Result.ABORTED  | true
	}

	def makeMockBuild(Result result) {
		def build = Mock(AbstractBuild)
		build.result >> result

		return build
	}
}
