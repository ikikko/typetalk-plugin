namespace(lib.FormTagLib).with {
	entry(title:'APIキー', field:'apiKey') { password() }
	entry(title:'トピック番号', field:'topicNumber') { textbox() }
	entry(title:'ビルドが成功した場合も通知する', field:'notifyWhenSuccess') { checkbox() }
}