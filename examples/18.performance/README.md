This example uses the NetExport and FireBug Firefox extensions to save the details of the browsers
interaction with a website.

Note that we are reusing the script from 17.simplesteps. The difference is that the JNLP file sets the
value:

```
<property name="jnlp.webdriver.firefox.profile" value="selenium" />
```

To make use of this profile you will need to compete the following steps:

1. Create a local Firefox profile called `selenium` using the instructions at https://support.mozilla.org/en-US/kb/profile-manager-create-and-remove-firefox-profiles
2. Open about:config
3. Set `xpinstall.signatures.required` to false
4. Install Firebug https://addons.mozilla.org/en-US/firefox/addon/firebug/ and NetExport http://www.softwareishard.com/blog/netexport/
5. Open about:config
6. Configure the following settings:
   * extensions.firebug.console.enableSites: true
   * extensions.firebug.script.enableSites: true
   * extensions.firebug.net.enableSites: true
   * extensions.firebug.previousPlacement: 1
   * extensions.firebug.allPagesActivation: on
   * extensions.firebug.netexport.alwaysEnableAutoExport: true
   * extensions.firebug.netexport.defaultLogDir: <your output directory>


