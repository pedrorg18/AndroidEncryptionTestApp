# AndroidEncryptionTestApp

## CAUTION: DO NOT USE THIS CODE IN PRODUCTION, IT IS POTENTIALLY UNSAFE!

This app implements an encryption system for android, without using the Android KeyStore. It could be useful for apps that support APIs < 18, as an alternative to the much safer KeyStore to be used in newer APIs.

It encodes the data with AES, then it hides the key. Here lies the biggest problem, as this is difficult but possible to steal.

## Behaviour
It takes a String from the main activity, encodes it with AES and saves it as a preference. Then it gets the encoded value from prefs, decodes and prints it in the same screen.

The first time, the AES key will be generated, then it will be reused in subsequent executions.
