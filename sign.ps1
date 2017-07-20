# 'Certificate for Card Authentication' comes from the alias name returned by the list.ps1 command
# This is some example output.
#
# Keystore type: PKCS11
# Keystore provider: SunPKCS11-OpenSC
#
# Your keystore contains 1 entry
#
# Alias name: Certificate for Card Authentication
# Entry type: PrivateKeyEntry
# Certificate chain length: 1
# Certificate[1]:
# Owner: CN=PIVKey 1A5DE5A78E5C5E408565FC71EE5D5C3F
# Issuer: CN=PIVKey Device Certificate Authority, DC=pivkey, DC=com
# Serial number: 50940422000100001b63
# Valid from: Fri Jun 02 07:15:25 AEST 2017 until: Wed May 30 07:15:25 AEST 2029
# Certificate fingerprints:
#          MD5:  7B:6F:C8:1F:EB:9F:02:79:56:12:C7:6D:67:DE:F3:3A
#          SHA1: 3E:DA:90:23:68:89:4D:1F:D4:81:31:39:7B:16:D0:90:5D:A6:00:CF
#          SHA256: CD:BA:0B:16:40:B8:E9:67:D0:B6:32:F2:D2:A9:AA:30:4E:CD:48:C5:EE:D7:E4:51:97:40:69:B4:81:16:1E:10
#          Signature algorithm name: SHA256withRSA
#          Version: 3

& "C:\Program Files\Java\jdk1.8.0_131\bin\jarsigner.exe" -tsa http://timestamp.digicert.com `
    -keystore NONE `
	-storetype PKCS11 `
    -providerClass sun.security.pkcs11.SunPKCS11 `
    -providerArg provider.cfg `
    -sigalg SHA256withRSA `
    build\libs\IridiumApplicationTesting.jar `
    'Certificate for Card Authentication'