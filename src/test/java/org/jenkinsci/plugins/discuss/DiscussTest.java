package org.jenkinsci.plugins.discuss;

import org.junit.Ignore;
import org.junit.Test;

//@Ignore("プロダクトコードに組み込んだので、ひとまず不要。単独でhtmlunitの確認をしたい場合は、再度整備する")
public class DiscussTest {

	private static final String API_KEY = "QWBQZUGleHTBFIC3CmMfAD91Kj68QL9h";

	@Test
	public void test() throws Exception {
		Discuss discuss = new Discuss(API_KEY);
		discuss.postMessage(9L, "test");
	}

}
