This is an example that makes use of an extension. An extension is
just another webstart application that contains a JAR file that
holds Cucumber methods. In this case we are referencing the 
JAR file created from the code at https://github.com/mcasperson/iridium-example-extension.

You can see this extension being referenced in the jnlp file with the element:

```
 <extension 
    name="Example Iridium Extension" 
    href="https://raw.githubusercontent.com/mcasperson/iridium-example-extension/master/webstart/example.jnlp"/>
```