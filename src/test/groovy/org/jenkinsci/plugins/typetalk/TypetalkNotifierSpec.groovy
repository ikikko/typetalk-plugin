package org.jenkinsci.plugins.typetalk

import hudson.model.AbstractBuild
import hudson.model.Result
import spock.lang.Specification
import spock.lang.Unroll

class TypetalkNotifierSpec extends Specification {

	@Unroll
	def "各種条件に応じた toBuildSummary のテストを行う"() {
		setup:
		def notifier = new TypetalkNotifier(notifyWhenSuccess)
		def build = makeMockBuild(result, previousResult)

		when:
		def summary = notifier.toBuildSummary(build)

		then:
		summary ? summary.contains(expected) : expected == null

		where:
		notifyWhenSuccess | previousResult | result          || expected
		false             | Result.SUCCESS | Result.SUCCESS  || null
		false             | Result.SUCCESS | Result.UNSTABLE || 'unstable'
		false             | Result.SUCCESS | Result.FAILURE  || 'failure'
		false             | Result.SUCCESS | Result.ABORTED  || 'aborted'

		false             | Result.FAILURE | Result.SUCCESS  || 'recovered'
		false             | Result.FAILURE | Result.UNSTABLE || 'unstable'
		false             | Result.FAILURE | Result.FAILURE  || 'failure'
		false             | Result.FAILURE | Result.ABORTED  || 'aborted'

		true              | Result.SUCCESS | Result.SUCCESS  || 'successful'
		true              | Result.SUCCESS | Result.UNSTABLE || 'unstable'
		true              | Result.SUCCESS | Result.FAILURE  || 'failure'
		true              | Result.SUCCESS | Result.ABORTED  || 'aborted'

		true              | Result.FAILURE | Result.SUCCESS  || 'recovered'
		true              | Result.FAILURE | Result.UNSTABLE || 'unstable'
		true              | Result.FAILURE | Result.FAILURE  || 'failure'
		true              | Result.FAILURE | Result.ABORTED  || 'aborted'
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
