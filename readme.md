# Iridium Application Testing Suite

## BrowserStack

![Browserstack](browserstack.png)

Thanks to Browserstack for their support of this project.

## Important Announcements

Java Web Start has been depreciated, and therefor the JAR file that was launched by the JNLP files will no longer be updated.
Please launch the JAR file directly.

The location of the JAR file that is referenced by the Web Start JNLP files has changed recently. It used to be https://s3-ap-southeast-2.amazonaws.com/ag-iridium/, so your JNLP files used to look like this:
```
<?xml version="1.0" encoding="UTF-8"?>  
<jnlp spec="1.0+" codebase="https://s3-ap-southeast-2.amazonaws.com/ag-iridium/">  
    ...  
</jnlp>
```
The new file location is https://s3.amazonaws.com/iridium-release, so your JNLP files should look like this:
```
<?xml version="1.0" encoding="UTF-8"?>  
<jnlp spec="1.0+" codebase="https://s3.amazonaws.com/iridium-release">  
    ...  
</jnlp>
```
Files from https://s3-ap-southeast-2.amazonaws.com/ag-iridium/won't be updated, and the certificate used to sign the JAR file has expired, so you will most likely receive a warning if you do not update the location.

## Chat

[![Chat](https://badges.gitter.im/Iridiumtester/repo.png)](https://gitter.im/Iridiumtester/Lobby)

## Build Status
[![Build Status](https://travis-ci.org/mcasperson/IridiumApplicationTesting.svg?branch=master)](https://travis-ci.org/mcasperson/IridiumApplicationTesting)

[![Dependency Status](https://www.versioneye.com/user/projects/57c4c52f69d94900403f6466/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57c4c52f69d94900403f6466)

[![codecov](https://codecov.io/gh/mcasperson/IridiumApplicationTesting/branch/master/graph/badge.svg)](https://codecov.io/gh/mcasperson/IridiumApplicationTesting)

## Documentation
See the [documentation](https://github.com/mcasperson/IridiumApplicationTesting/wiki)
for more details.

## Training
A training package has been made available on [Udemy](https://www.udemy.com/creating-end-to-end-tests-with-cucumber-and-webdriver/?couponCode=IRIDIUM_COUPON_2017).

## Downloads
Download the latest version from [here](https://s3.amazonaws.com/iridium-release/IridiumApplicationTesting.jar)

Find past releases [here](https://github.com/mcasperson/IridiumApplicationTesting/releases)

## Articles

* [An Introduction to Iridium, an Open Source Selenium and Cucumber Testing Tool](https://dzone.com/articles/an-introduction-to-iridium-an-open-source-selenium)

* [How to Capture Network Transactions With Iridium](https://dzone.com/articles/network-analysis-with-iridium)

* [CI Testing With Iridium and PhantomJS](https://dzone.com/articles/ci-testing-with-iridium-and-phantomjs)

* [Modifying Network Requests With Iridium](https://dzone.com/articles/modifying-network-requests-with-iridium)

* [Security Testing With ZAP and Iridium](https://dzone.com/articles/security-testing-with-zap-and-iridium)

* [Integration Testing With Iridium](https://dzone.com/articles/integration-testing-with-iridium)

* [Verifying Page Content and Styles With Iridium](http://dzone.com/articles/verifying-page-content-and-styles-with-iridium)

* [Taking the Pain Out of Writing Iridium Test Scripts](https://dzone.com/articles/taking-the-pain-out-of-writing-iridium-test-script)

* [Extending Iridium With Custom Step Definitions](https://dzone.com/articles/extending-iridium-with-custom-step-definitions)

* [Dead Link Checking With Iridium](https://dzone.com/articles/dead-link-checking-with-iridium)

* [Sharing Scenarios in Iridium Test Scripts](https://dzone.com/articles/sharing-scenarios-in-iridium-test-scripts)

* [Why You May Not Need (or Even Want) to Use Page Objects With Your Webdriver Tests](https://dzone.com/articles/why-you-may-not-need-or-even-want-to-use-page-obje)

* [Are Cucumber Features and Scenarios to Be Taken Literally?](https://dzone.com/articles/do-you-need-to-take-a-literal-view-of-features-and)

* [Running Iridium Scripts in Firefox With Jenkins](https://dzone.com/articles/running-iridium-scripts-in-firefox-in-jenkins)

* [Introducing PICQT for Writing Cucumber Tests With Iridium](https://dzone.com/articles/introducing-picqt-for-writing-cucumber-tests-with)
