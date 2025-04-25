package com.floydwiz.mdmexample.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

object Utils {

    private const val TAG = "ExampleMDMDebug: Utils"

    fun getUserInstalledAppPackages(context: Context): List<String> {
        val packageManager = context.packageManager
        val userInstalledPackages = mutableListOf<String>()
        val allInstalledApps: List<ApplicationInfo>

        try {
            allInstalledApps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledApplications(
                    PackageManager.ApplicationInfoFlags.of(
                        PackageManager.MATCH_DISABLED_COMPONENTS.toLong() or
                                PackageManager.MATCH_UNINSTALLED_PACKAGES.toLong()
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstalledApplications(
                    PackageManager.GET_DISABLED_COMPONENTS or
                            PackageManager.GET_UNINSTALLED_PACKAGES
                )
            }

            for (appInfo in allInstalledApps) {
                val packageName = appInfo.packageName
                val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val isHidden = !appInfo.enabled

                Log.d(
                    TAG,
                    "App: $packageName | SystemApp: $isSystemApp | Enabled: ${appInfo.enabled} | Hidden: $isHidden"
                )

                if (!isSystemApp) {
                    userInstalledPackages.add(packageName)
                }
            }

            Log.i(
                TAG,
                "User-installed packages found (including hidden): ${userInstalledPackages.size}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting installed applications", e)
        }

        return userInstalledPackages
    }

    // Helper function to check if a package is a system app
    fun isSystemApp(context: Context, packageName: String): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                packageName,
                PackageManager.MATCH_DISABLED_COMPONENTS
            )

            // Check if the app is a system app by checking the FLAG_SYSTEM flag
            val isSystemApp =
                packageInfo.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) != 0

            Log.d(TAG, "Package: $packageName | Is system app: $isSystemApp")

            isSystemApp // If applicationInfo is null, consider it a system app (to avoid blocking)
        } catch (e: PackageManager.NameNotFoundException) {
            // Log error and consider it as a system app to avoid blocking
            Log.w(TAG, "Package not found: $packageName. Treating it as a system app.")
            true
        }
    }

}