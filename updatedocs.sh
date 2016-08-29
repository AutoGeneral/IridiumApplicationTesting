#!/usr/bin/env bash
./gradlew clean javadoc
rm -rf ../AutoGeneral.github.io/IridiumApplicationTesting/javadoc
cp -r build/docs/javadoc ../AutoGeneral.github.io/IridiumApplicationTesting
cd ../AutoGeneral.github.io
git add .; git commit -m "Updated docs"; git push origin master