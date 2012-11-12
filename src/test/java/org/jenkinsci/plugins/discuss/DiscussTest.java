package org.jenkinsci.plugins.discuss;

import org.junit.Test;

public class DiscussTest {

	// Jenkins 君
	private static final String API_KEY = "QWBQZUGleHTBFIC3CmMfAD91Kj68QL9h";

	@Test
	public void test() throws Exception {
		Discuss discuss = new Discuss(API_KEY);
		discuss.postMessage(9L, "test");	// topic: Labs
	}

}
