package danB.bebicardo

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permissions {

    private val smsRequestCode = 69
    private val internetRequestCode = 420

    constructor(context: Context) {
        requestPermission(context)
    }

    private fun requestPermission(context: Context) {
        // Verification si l'on a deja la permission
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Demande la permission
//            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.SEND_SMS), smsRequestCode)
        }

        // Comme precedent, mais permission internet
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.INTERNET), internetRequestCode)
        }
    }

    fun handlePermissionsResult(requestCode: Int, permissions: Array<out String>, grantResult: IntArray) {
        when(requestCode) {
            smsRequestCode -> {
                checkGrantResult(grantResult)
            }
            internetRequestCode -> {
                checkGrantResult(grantResult)
            }
        }
    }

    private fun checkGrantResult(grantResult: IntArray) {
        if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            print("permission !")
        } else {
            print("no permissions")
        }
    }
}