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
        whitelistData: List<AppWhitelistData>,
        block: Boolean
    ): Boolean {
        val installedPackages = getUserInstalledAppPackages(context)

        val whitelistMap = whitelistData.associateBy { it.packageName }

        for (pkg in installedPackages) {
            val whitelistEntry = whitelistMap[pkg]

            val shouldBeVisible = if (!block) {
                true // toggle OFF → everything visible
            } else {
                whitelistEntry?.isWhitelisted == true // toggle ON → visible only if marked true
            }

            try {
                val result = dpm.setApplicationHidden(admin, pkg, !shouldBeVisible)
                Log.d(TAG, "Set visibility for $pkg -> ${!shouldBeVisible} (result: $result)")
            } catch (e: Exception) {
                Log.w(TAG, "Could not set visibility for $pkg: ${e.message}")
            }
        }

        return true
    }

    fun createMdmControls(
        context: Context,
        admin: ComponentName,
        dpm: DevicePolicyManager,
        singlePackage: String,
        isCameraDisabled: Boolean,
        isInstallDisabled: Boolean,
        isPackageHidden: Boolean
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
                            whitelistData = Constants.APP_WHITELIST_DATA,
                            block = isChecked
                        )
                    }
                }
            ),
        )
    }

}