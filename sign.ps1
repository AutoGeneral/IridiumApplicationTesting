& "C:\Program Files\Java\jdk1.8.0_131\bin\jarsigner.exe" -tsa http://timestamp.digicert.com `
    -keystore NONE `
	-storetype PKCS11 `
    -providerClass sun.security.pkcs11.SunPKCS11 `
    -providerArg provider.cfg `
    -sigalg SHA256withRSA `
    build\libs\IridiumApplicationTesting.jar `
    'Certificate for Card Authentication'