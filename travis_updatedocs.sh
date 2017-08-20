#!/usr/bin/env bash
if [[ $TEST_SUITE = "{'browsers':['PhantomJS'],'runNegTests':true,'runSimpleTests':true}" ]]; then
	cd ..
	git clone git@github.com:mcasperson/mcasperson.github.io.git
	cd mcasperson.github.io
	rm -rf IridiumApplicationTesting/javadoc
	cp -r ../IridiumApplicationTesting/build/docs/javadoc IridiumApplicationTesting
	git add .
	git commit -m "Updated docs"
	git push origin master;
fi
