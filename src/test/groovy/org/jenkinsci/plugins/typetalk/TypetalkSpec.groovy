package org.jenkinsci.plugins.typetalk

import org.apache.commons.httpclient.HttpStatus
import spock.lang.Ignore
import spock.lang.Specification

class TypetalkSpec extends Specification {

	def config = new Properties()

	def setup() {
		config.load(getClass().getResourceAsStream("/config.properties"))
	}

	// If you want to test, copy 'config.properties.tmpl' to 'config.properties' and modify it.
	@Ignore("not post normally")
	def postMessage() {
		setup:
		Typetalk typetalk = new Typetalk(config['client.id'], config['client.secret'])

		expect:
		typetalk.postMessage(config['topic.id'].toLong(), "TypetalkSpec : ${new Date()}") == HttpStatus.SC_OK
	}

}
