package org.jenkinsci.plugins.typetalk;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

public class TypetalkTest {

	// TODO APIキーを埋め込まない
	// 念のために、公開リリース前は新しいキーに変更して、既存のキーを使ってるところはreplaceしておくこと

	// Jenkins 君
	private static final String API_KEY = "QWBQZUGleHTBFIC3CmMfAD91Kj68QL9h";

	@Test
	@Ignore("not post usually")
	public void postMessage() throws Exception {
		Typetalk typetalk = new Typetalk(API_KEY);
		typetalk.postMessage(9L, "test : " + new Date()); // topic: Labs
	}

}
