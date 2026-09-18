package com.example.camerarecorder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRecord = findViewById<ImageButton>(R.id.btnRecord)
        val secretHeader = findViewById<View>(R.id.secretHeaderArea)

        btnRecord.setOnClickListener {
            val serviceIntent = Intent(this, StealthRecordService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
            
            // Dims brightness to simulate screen off while keeping background service running
            window.attributes = window.attributes.apply { screenBrightness = 0.01f }
            Toast.makeText(this, "Recording active in background", Toast.LENGTH_SHORT).show()
        }

        secretHeader.setOnClickListener {
            promptScreenPassword()
        }
    }

    private fun promptScreenPassword() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    stopService(Intent(this@MainActivity, StealthRecordService::class.java))
                    window.attributes = window.attributes.apply { screenBrightness = -1f }
                    Toast.makeText(applicationContext, "Recording Stopped & Saved", Toast.LENGTH_LONG).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Storage Vault")
            .setSubtitle("Enter your phone PIN, pattern, or password")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
