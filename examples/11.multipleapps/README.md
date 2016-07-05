This is a test of running a test multiple times in parallel against multiple data sets and multiple url mappings. In this
case we test both Budget and 1st For Women with two address captures each.

There are 4 tests, and 2 are run in parallel at any one time.

Each test then jumps between two different apps. In this case we do an address ca

We also don't open the report file or leave the browser windows open, as you would not typically do this
when running larger data sets.

The important settings here are these settings in the jnlp file:

```
    <property name="javaws.numberOfThreads" value="2"/>
    <property name="javaws.numberURLs" value=""/>
    <property name="javaws.numberDataSets" value=""/>
    <property name="javaws.leaveWindowsOpen" value="false"/>
    <property name="javaws.openReportFile" value="false"/>
```