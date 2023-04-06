package de.planetcat.detecta

import android.Manifest.permission.*
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.w("DetectAService", "Test: Activity Created")

        findViewById<Button>(R.id.serviceaktivieren)
            .setOnClickListener {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }

        findViewById<Button>(R.id.positionaktivieren)
            .setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION), 0)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 0)
                }

            }
    }

    override fun onResume() {
        super.onResume()
        if (isAccessibilityServiceEnabled(this, DetectAService::class.java)) {
            findViewById<Button>(R.id.serviceaktivieren).isEnabled = false
            findViewById<Button>(R.id.serviceaktivieren).text = "Bereits aktiviert."
        }
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, ACCESS_BACKGROUND_LOCATION) == PERMISSION_GRANTED) {
            findViewById<Button>(R.id.positionaktivieren).isEnabled = false
            findViewById<Button>(R.id.positionaktivieren).text = "Bereits aktiviert."
        }
    }

    fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out AccessibilityService?>
    ): Boolean {
        val am: AccessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices: List<AccessibilityServiceInfo> =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(
                    service.name
                )
            ) return true
        }
        return false
    }
}