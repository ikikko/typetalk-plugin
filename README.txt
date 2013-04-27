* ビルドの方法 ( ref : https://wiki.jenkins-ci.org/display/JENKINS/Gradle+JPI+Plugin )

** Eclipseプロジェクトへのインポート
# gradle cleanEclipse eclipse
ただし、Eclipse の workspace / project ともに groovy 1.8 に揃えないと、Eclipse 上で spock テストが実行できない

** 開発用にJenkins起動
# gradle clean server

** プラグインファイル（JPIファイル）を作成
# gradle clean jpi

- gradleをインストールしていない場合は、gradleの代わりにgradlew(もしくはgradlew.bat)を使用できます
-- ref : http://gradle.monochromeroad.com/docs/userguide/gradle_wrapper.html
