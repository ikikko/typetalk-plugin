package org.jenkinsci.plugins.typetalk;

import org.junit.Test;

public class TypetalkTest {

	// Jenkins 君
	private static final String API_KEY = "QWBQZUGleHTBFIC3CmMfAD91Kj68QL9h";

	@Test
	public void test() throws Exception {
		Typetalk typetalk = new Typetalk(API_KEY);
		typetalk.postMessage(9L, "test"); // topic: Labs
	}

}
