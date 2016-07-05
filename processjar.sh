#!/bin/bash
./gradlew clean shadowJar --refresh-dependencies
mv build/libs/WebAppTesting.jar build/libs/webapptesting-signed.jar
pack200 --repack build/libs/webapptesting-signed.jar
jarsigner -keystore webstart/keystore -storepass password -keypass password build/libs/webapptesting-signed.jar wat
pack200 build/libs/webapptesting-signed.jar.pack.gz build/libs/webapptesting-signed.jar
