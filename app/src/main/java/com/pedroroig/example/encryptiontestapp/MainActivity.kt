package com.pedroroig.example.encryptiontestapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.pedroroig.example.encryptiontestapp.cypher.Cypher
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cleanEncryptionKey()

        val encryptedData = Cypher.encryptLegacy(this, textViewOrigin.text.toString().toByteArray())

        textViewDecrypted.text = String(Cypher.decryptLegacy(this, encryptedData.encryptedData))

    }

    /**
     * Removes the encrypted key from preferences, so a new one will be generated.
     * Useful if we want to change the key generation and encryption
     */
    @Suppress("unused")
    private fun cleanEncryptionKey() {
        val prefs = getSharedPreferences(Cypher.PREF_NAME, 0)
        val editor = prefs.edit()
        editor.putString("OK*", null)
        editor.apply()
    }
}
