package com.floydwiz.mdmexample.utils

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserManager
import android.util.Log
import com.floydwiz.mdmexample.data.AppWhitelistData
import com.floydwiz.mdmexample.data.MdmSwitchControl
import com.floydwiz.mdmexample.utils.Constants.BROWSER_PKG
import com.floydwiz.mdmexample.utils.Constants.restrictions

object Utils {

    private const val TAG = "mdmexampledebug"

    private fun getUserInstalledAppPackages(context: Context): List<String> {
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

    private fun enforceWhitelist(
        context: Context,
        admin: ComponentName,
        dpm: DevicePolicyManager,
        allowedPackages: List<AppWhitelistData>,
        block: Boolean
    ): Boolean {
        // Map for quick lookup
        val whitelistMap = allowedPackages.associateBy { it.packageName }

        // Get all user-installed apps
        val installedPackages = getUserInstalledAppPackages(context)

        // Start with installed packages only
        val allPackagesToCheck = installedPackages.toMutableSet()

        // Check each package in the whitelist map
        for (pkg in whitelistMap.keys) {
            val isWhitelisted = whitelistMap[pkg]?.isWhitelisted == true
            if (isWhitelisted) {
                // Always add explicitly whitelisted apps
                Log.i(TAG, "Explicitly whitelisted, adding to check list: $pkg")
                allPackagesToCheck.add(pkg)
            } else if (block && isSystemApp(context, pkg) && !Constants.systemAppAllowlist.contains(
                    pkg
                )
            ) {
                // Skip system apps not in allowlist
                Log.i(TAG, "Skipping system app not in allowlist: $pkg")
            } else {
                // Add user-installed or allowed system app
                Log.i(TAG, "Adding to check list: $pkg")
                allPackagesToCheck.add(pkg)
            }
        }

        for (pkg in allPackagesToCheck) {
            val whitelistEntry = whitelistMap[pkg]

            // Determine whether the app should be visible based on the block state
            val shouldBeVisible = if (!block) {
                true // Toggle OFF: show everything
            } else {
                whitelistEntry?.isWhitelisted == true // Toggle ON: visible only if marked true
            }

            try {
                // Set visibility for the app based on whether it should be shown or hidden
                val result = dpm.setApplicationHidden(admin, pkg, !shouldBeVisible)
                Log.d(TAG, "Set visibility for $pkg -> ${!shouldBeVisible} (result: $result)")
            } catch (e: Exception) {
                // Log any exception that occurs
                Log.w(TAG, "Could not set visibility for $pkg: ${e.message}")
            }
        }

        return true
    }

    // Helper function to check if a package is a system app
    private fun isSystemApp(context: Context, packageName: String): Boolean {
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


    fun createMdmControls(
        context: Context,
        admin: ComponentName,
        dpm: DevicePolicyManager,
        singlePackage: String,
        isCameraDisabled: Boolean,
        isInstallDisabled: Boolean,
        isPackageHidden: Boolean,
        isWebsiteWhitelistEnabled: Boolean,
        isScreenshotDisabled: Boolean,
    ): List<MdmSwitchControl> {
        return listOf(
            MdmSwitchControl(
                title = "Disable Camera",
                isChecked = isCameraDisabled,
                onCheckedChange = { isChecked ->
                    if (dpm.isAdminActive(admin)) {
                        dpm.setCameraDisabled(admin, isChecked)
                    }
                }
            ),
            MdmSwitchControl(
                title = "Disable App Install",
                isChecked = isInstallDisabled,
                onCheckedChange = { isChecked ->
                    if (dpm.isAdminActive(admin)) {
                        if (isChecked) {
                            dpm.addUserRestriction(admin, UserManager.DISALLOW_INSTALL_APPS)
                        } else {
                            dpm.clearUserRestriction(admin, UserManager.DISALLOW_INSTALL_APPS)
                        }
                    }
                }
            ),
            MdmSwitchControl(
                title = "Hide Single Package",
                isChecked = isPackageHidden,
                onCheckedChange = { isChecked ->
                    if (dpm.isAdminActive(admin)) {
                        dpm.setApplicationHidden(admin, singlePackage, isChecked)
                    }
                }
            ),
            MdmSwitchControl(
                title = "Enforce Whitelist",
                isChecked = false,
                onCheckedChange = { isChecked ->
                    if (dpm.isAdminActive(admin)) {
                        enforceWhitelist(
                            context = context,
                            admin = admin,
                            dpm = dpm,
                            allowedPackages = Constants.APP_WHITELIST_DATA,
                            block = isChecked
                        )
                    }
                }
            ),
            MdmSwitchControl(
                title = "Whitelist Websites",
                isChecked = isPackageHidden,
                onCheckedChange = {
                    if (dpm.isAdminActive(admin)) {
                        try {
                            Log.d("ChromeRestrictions", "Proposed restrictions: $restrictions")

                            // Set the application restrictions on Chrome
                            dpm.setApplicationRestrictions(
                                admin,
                                BROWSER_PKG,
                                restrictions
                            )

                            // Retrieve the applied restrictions and log them
                            val appliedRestrictions =
                                dpm.getApplicationRestrictions(admin, BROWSER_PKG)
                            Log.d(
                                "ChromeRestrictions",
                                "Applied restrictions: $appliedRestrictions"
                            )
                        } catch (e: Exception) {
                            Log.e("ChromeRestrictions", "Failed to set restrictions: ${e.message}")
                        }
                    } else {
                        Log.w("ChromeRestrictions", "Device admin is not active.")
                    }
                }
            ),
            MdmSwitchControl(
                title = "Disable Screenshot",
                isChecked = isScreenshotDisabled,
                onCheckedChange = { isChecked ->
                    if (dpm.isAdminActive(admin)) {
                        dpm.setScreenCaptureDisabled(admin, isChecked)
                    }
                }
            ),
        )
    }

}