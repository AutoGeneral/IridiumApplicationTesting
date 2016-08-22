#!/bin/bash

ALIAS=iridium
PASSWORD=password

./gradlew clean checkstyleTest shadowJar --refresh-dependencies
mv build/libs/IridiumApplicationTesting.jar build/libs/IridiumApplicationTesting.jar
pack200 --repack build/libs/IridiumApplicationTesting.jar
keytool -genkey -noprompt -dname "CN=Auto and General, OU=Online Systems, O=Auto and General, L=Brisbane, S=Queensland, C=AU" -alias ${ALIAS} -keyalg RSA -keystore keystore -keysize 2048 -storepass ${PASSWORD} -keypass ${PASSWORD} -validity 3650
jarsigner -keystore keystore -storepass ${PASSWORD} -keypass ${PASSWORD} build/libs/IridiumApplicationTesting.jar ${ALIAS}
pack200 build/libs/IridiumApplicationTesting.jar.pack.gz build/libs/IridiumApplicationTesting.jar
rm keystore
