package com.floydwiz.mdmexample

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.floydwiz.mdmexample.ui.theme.ExampleMDMTheme

class MainActivity : ComponentActivity() {
    private lateinit var adminComponent: ComponentName
    private lateinit var dpm: DevicePolicyManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)

        setContent {
            ExampleMDMTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MdmControlsPage(innerPadding, adminComponent, dpm)
                }
            }
        }
    }
}