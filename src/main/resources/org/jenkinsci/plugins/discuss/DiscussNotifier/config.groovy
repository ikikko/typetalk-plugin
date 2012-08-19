namespace(lib.FormTagLib).with {
	entry(title:'ユーザID', field:'name') { textbox() }
	entry(title:'パスワード', field:'password') { password() }
	entry(title:'トピック番号', field:'topicNumber') { textbox() }
	entry(title:'ビルドが成功した場合も通知する', field:'notifyWhenSuccess') { checkbox() }
}