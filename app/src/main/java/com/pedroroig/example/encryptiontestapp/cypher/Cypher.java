package com.pedroroig.example.encryptiontestapp.cypher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Cypher {

    public static final String PREF_NAME = "SECURIZED_PREFS";
    private static final String TAG = Cypher.class.getSimpleName();


    public static EncryptedData encryptLegacy(Context ctx, byte[] clearData) throws Exception {

        byte[] key = getAESKey(ctx);

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(clearData);

        EncryptedData aesData = new EncryptedData();
        aesData.encryptedData= Base64.encodeToString(encrypted, Base64.NO_WRAP);
        aesData.encryptedKey = "LEGACY";

        return aesData;
    }

    public static byte[] decryptLegacy(Context ctx, String encryptedData) throws Exception {

        byte[] key = getAESKey(ctx);

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        return cipher.doFinal(
                Base64.decode(encryptedData.getBytes(), Base64.NO_WRAP)
        );
    }


    /**
     * Gets AES key from preferences. If first access, it will be created
     * @return bytes representing the AES key
     */
    private static byte[] getAESKey(Context ctx) throws NoSuchAlgorithmException {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, 0);

        String obfuscatedKey = prefs.getString("OK*", null);
        Log.d(TAG, "obfuscatedKey FROM PREFS: " + obfuscatedKey);

        // The first time, generate a new key
        if(obfuscatedKey == null) {
            byte[] key = generateAESKey();
            obfuscateAndStoreAESKey(ctx, key);
            return key;
        }

        // Else, de-obfuscate and return the stored key
        // Decode
        obfuscatedKey = new String(Base64.decode(obfuscatedKey, Base64.NO_WRAP));
        Log.d(TAG, "obfuscatedKey: " + obfuscatedKey);
        // Remove extra characters
        obfuscatedKey = obfuscatedKey.substring(2);
        obfuscatedKey = obfuscatedKey.substring(0, obfuscatedKey.length() - 2);
        Log.d(TAG, "obfuscatedKey 2: " + obfuscatedKey);

        Log.d(TAG, "CLEAR 2: " + new String(Base64.decode(obfuscatedKey, Base64.NO_WRAP)));
        // decode again
        return  Base64.decode(obfuscatedKey, Base64.NO_WRAP);
    }

    /**
     * Creates a new AES key
     * @return bytes representing the AES key
     */
    private static byte[] generateAESKey() throws NoSuchAlgorithmException {

        byte[] keyStart = "AWDW=EQ6.DEI4yhfh4-AKWr7eW-EW*AHQ".getBytes();

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(keyStart);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }


    /**
     * Obfuscates the AES key in order for it to be more difficult to steal. Stores it in preferences
     */
    private static void obfuscateAndStoreAESKey(Context ctx, byte[]  key) {
        // Convert to B64
        String b64clearKey = Base64.encodeToString(key, Base64.NO_WRAP);
        // Add characters
        String b64ObfuscatedKey = "[*" + b64clearKey + "*]";
        // Convert again to B64
        b64ObfuscatedKey = Base64.encodeToString(b64ObfuscatedKey.getBytes(), Base64.NO_WRAP);

        SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("OK*", b64ObfuscatedKey);

        editor.apply();
    }

}
