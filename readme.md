# A&G Web Application Testing Suite

See the [documentation](https://mcasperson.gitbooks.io/iridiumapplicationtesting-gettingstartedguide/content/)
for more details.

# Important Information

The use of cucumber with Java Web Start requires a custom version of the cucumber core library.
See https://github.com/mcasperson/cucumber-jvm/tree/master/core for the fork that enables Web
Start functionality.

To publish the new core library to the internal Nexus instance, add the following to the pom.xml
file (this is not commited to the repo because it includes internal host names):

```
    <distributionManagement>
        <snapshotRepository>
            <id>InternalSnapshots</id>
            <name>Internal Snapshots</name>
            <url>http://internalnexusrepo:8081/nexus/content/repositories/snapshots/</url>
        </snapshotRepository>
        <repository>
            <id>InternalReleases</id>
            <name>Internal Releases</name>
            <url>http://internalnexusrepo:8081/nexus/content/repositories/releases/</url>
        </repository>
    </distributionManagement>
```

Then add the details of the server to your `~/.m2/settings.xml` file:

```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
	<servers>
		<server>
          <id>InternalReleases</id>
          <username>deployment</username>
          <password>passwordgoeshere</password>
         </server>
        <server>
          <id>InternalSnapshots</id>
          <username>deployment</username>
          <password>passwordgoeshere</password>
         </server>
	</servers>
</settings>
```

Then run:

```
mvn clean deploy -Denforcer.skip=true
```

## How to use

Simple script (note that multiline scripts may not work in your system)

```
java -DfeatureGroupName=SAR-PROD
     -DtestDestination=Firefox
     -DtestSource=C:\Users\mcasperson\IdeaProjects\end-to-end-tests\features
     -Dconfiguration=C:\Users\mcasperson\IdeaProjects\end-to-end-tests\settings\configuration.xml
     -Ddataset=C:\Users\mcasperson\IdeaProjects\end-to-end-tests\data\sar-dataset.xml
     -DappURLOverride=https://secure.budgetdirect.com.au/sar/quote.jsp?hSty=BUDD
     -jar .\WebAppTesting.jar
```

Simple browserstack setup:

```
.\gradlew.bat run
     -Penvironment=production
     -DtestDestination=BrowserStack
     -DfeatureGroupName=SAR-PROD
     -DgroupName=Chrome
     -DtestSource=C:\Users\mcasperson\IdeaProjects\end-to-end-tests\features
     -Dconfiguration=C:\Users\mcasperson\IdeaProjects\end-to-end-tests\settings\configuration.xml
     -Ddataset=C:\Users\mcasperson\IdeaProjects\end-to-end-tests\data\sar-dataset.xml
```

## Params

Params marked as * are required

#### configuration *
We're no longer using DDS to store testing configuration so you MUST specify .xml file with testing profile.
Usually that is http://gitstash/projects/AGIC/repos/end-to-end-tests/browse/settings/configuration.xml.
This option accepts a complete file path, or a URL.

#### dataset *
We're no longer using DDS to store data sets configuration so you MUST specify .xml file with dataset.
Check http://gitstash/projects/AGIC/repos/end-to-end-tests/browse/data to see existing dataset profiles.
This option accepts a complete file path, or a URL.

#### testSource *
Path to E2E tests.

If this is a directory, it will be scanned for .feature files that match the `featureGroupName` setting.
If it is a file or a URL, the script is run disregrading the `featureGroupName` setting.

#### testDestination
Name of the browser to run the tests (Chrome, Firefox, IE). Select BrowserStack to run the tests remotely in BrowserStack.

#### featureGroupName
This is optional, and is only used when `testSource` references a directory. 
It links to the featureGroupName attribute on a feature element.
The featureGroup attribute is a mapping between a feature script and the urls and data sets that will be applied to it.

#### webdriver.chrome.driver, webdriver.ie.driver or webdriver.opera.driver
Path to web drivers for those browsers

#### groupName
This defines the group that a BrowserStack capabilities profile needs to belong to in order to be included in the test. 
Groups usually indicate the browser (IE, Chrome etc), OS (Windows, MacOS etc) or device kind (Desktop, Mobile etc).
This is useful when you want to limit your tests to a logical group of device or platform.

#### leaveWindowsOpen
Tick this option to leave the browser window open after the local tests have been completed.
This is useful when you want to continue using the web application from where the script finished up.
This has no effect when tests are run in BrowserStack.

If you select the "Leave Browser Open" option, you may find that some resources are not released when the test is 
finished and the application is closed. In particular, the chromedriver and operadriver processes are left in memory 
(depending on the browser you are using). If you run a number of tests in succession, these processes can take up quite 
a bit of memory, and it is up to you to kill them manually.

#### appURLOverride
If Override URL is selected, this field can be used to define the single custom URL that the application will open with.
This is useful if want want to test a feature branch with a test script, or if you want to test custom query params.
Because you are specifying a single URL, the test will be run once.

Note that you can only override the URL when the test script does not reference a named url. Fo rexample, this 
line in a test script will fail if `appURLOverride` is defined, because these named url references from the
configuration file are no longer loaded.
```
And I open the application "app"
```

#### numberDataSets
This defines the maximum number of entries that will be selected from the data set.
Usually when testing locally you'll set this to one to ensure that you are only opening one instance of an application.

#### numberOfThreads
In order to improve the performance of the tests, they can be run in parallel. This setting defines the maximum number
of threads that Cucumber will be run in. Keep in mind that when you run tests in BrowserStack, there is a limit (probably 2)
of the number of parallel tests. You will get errors if you set this value higher than the maximum number of parallel tests
 when running in BrowserStack.

*NOTE:* Running cucumber in seperate threads does not mean that local browsers are running in their own "sandbox".
It is quite likely that running two or more threads for the same web application in the same browser will result in
those browser instances conflicting with each other as they edit the same server session.
When running locally against a single instance of an application (i.e. running multiple data sets against a single application),
it is usually best to use a single thread.

#### tagsOverride
See https://mcasperson.gitbooks.io/iridiumapplicationtesting-gettingstartedguide/content/
to get more information about this option.

#### enableVideoCapture
When running a local test, enabling this option will save a screen capture to an AVI file.
The location of the AVI file is displayed in the output window once the test is finished.

#### enableScenarioScreenshots
When set to true, screenshots will be taken at the end of each scenario. This is useful when
running tests against a headless browser like PhantonJS, as it gives you an opportunity to see
the state of the web page at points during the test.

#### openReportFile
When set to true, the HTML report will be opened once the test is finished.

#### saveReportsInHomeDir
When set to true, all report files and screenshots will be saved in a folder in the users home
directory. Otherwise the files are saved in the current working directory.

You almost always want to set this to true when running tests locally, and set it to false when
running tests in Bamboo.

#### phantomJSLoggingLevel
Sets the PhantomJS logging level. PhantomJS will generate a lot of unnecessary log messages while 
waiting for elements to be displayed, so by default the logging is turned off. But it can be handy
to see these messages sometimes, so setting this value to something like `WARN` will give
you some additional logging.

#### startInternalProxy
Set this to the name of the internal proxy that should be started with all tests e.g.
```
-DstartInternalProxy=zap
```

## How to build

To run from source:
```
./gradlew run -Penvironment=production
```

To build a native package

```
./gradlew clean shadowJar
```

For Mac Users
```
/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/bin/javapackager -deploy -native -outdir packages -outfile WebAppTesting -srcdir build/libs/ -srcfiles WebAppTesting.jar -appclass au.com.agic.apptesting.Main -name WebAppTesting -title WebAppTesting
```

For Windows Users
```
"/cygdrive/c/Program Files/Java/jdk1.8.0_51/bin/javapackager" -deploy -native -outdir packages -outfile WebAppTesting -srcdir build/libs -srcfiles WebAppTesting.jar -appclass au.com.agic.apptesting.Main -name WebAppTesting -title WebAppTesting
```

For Local Testing
```
gradlew.bat run -Dmode=commandLine -DfeatureGroupName=TQS -DappURLOverride="https://secure.budgetdirect.com.au/travel/new_quote.jsp?hSty=BUDD" -DleaveWindowsOpen=true -DtestDestination=Firefox -DtagsOverride=@PAGEOBJECT,@LAUNCH,@TOOLBAR,@TQS1,@TQS2,@TQS3 -DnumberDataSets=1 -DtestSource="c:/Sohil/LightsOn/end-to-end-test/features" -DnumberOfThreads=1
```

# ZAP integration
ZAP has been bundled inside the testing app. ZAP doesn't support this as such, but with a few tweaks can be made
to run from a self contained JAR.

ZAP is made up of the main ZAP jar file, saved in the `/lib` folder.
The supporting ZAP files are saved under `/resources/zap`.
The resources from the ZAP JAR have been copied into `src/main/resources`.
We also include the ZAP JAR dependencies as part of the Gradle build.

# Proxy Notes
Not webdrivers support setting a proxy. Firefox and PhantomJS do, while Chrome, Opera and Safari don't. This means that
ZAP integration and steps that rely on BrowserMob (e.g. I remove repeated AWSELB cookies from the request to the URL regex "whatever")
will not work when run in these browsers.