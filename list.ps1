& "C:\Program Files\Java\jdk1.8.0_131\bin\keytool.exe" `
    -keystore NONE `
	-storetype PKCS11 `
    -providerClass sun.security.pkcs11.SunPKCS11 `
	-providerArg provider.cfg `
    -list -v