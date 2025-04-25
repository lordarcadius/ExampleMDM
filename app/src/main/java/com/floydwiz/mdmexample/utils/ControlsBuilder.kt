package com.floydwiz.mdmexample.utils

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import com.floydwiz.mdmexample.data.AppWhitelistData
import com.floydwiz.mdmexample.data.MdmSwitchControl
import com.floydwiz.mdmexample.utils.Constants.BROWSER_PKG
import com.floydwiz.mdmexample.utils.Constants.restrictions
import com.floydwiz.mdmexample.utils.Constants.SINGLE_PACKAGE
import com.floydwiz.mdmexample.utils.Utils.getUserInstalledAppPackages
import com.floydwiz.mdmexample.utils.Utils.isSystemApp

class ControlsBuilder(
    private val context: Context,
    private val admin: ComponentName,
    private val dpm: DevicePolicyManager,
) {
    private val isInstallDisabled =
        dpm.getUserRestrictions(admin).getBoolean(UserManager.DISALLOW_INSTALL_APPS, false)
    private val isCameraDisabled = dpm.getCameraDisabled(admin)
    private val isPackageHidden = dpm.isApplicationHidden(admin, SINGLE_PACKAGE)
    private val isScreenshotDisabled = dpm.getScreenCaptureDisabled(admin)
    private val isMtpBlocked =
        dpm.getUserRestrictions(admin).getBoolean(UserManager.DISALLOW_USB_FILE_TRANSFER, false)
    private val isMicrophoneDisabled = dpm.getUserRestrictions(admin)
        .getBoolean(UserManager.DISALLOW_UNMUTE_MICROPHONE, false)
    private val isCallBlocked = dpm.getUserRestrictions(admin)
        .getBoolean(UserManager.DISALLOW_OUTGOING_CALLS, false)
    private val isSmsBlocked = dpm.getUserRestrictions(admin)
        .getBoolean(UserManager.DISALLOW_SMS, false)
    private val isAirplaneModeBlocked = dpm.getUserRestrictions(admin)
        .getBoolean(UserManager.DISALLOW_AIRPLANE_MODE, false)
    private val isBluetoothBlocked = dpm.getUserRestrictions(admin)
        .getBoolean(UserManager.DISALLOW_BLUETOOTH, false)
    private val isBluetoothSharingBlocked = dpm.getUserRestrictions(admin)
        .getBoolean(UserManager.DISALLOW_BLUETOOTH_SHARING, false)

    companion object {
        const val TAG = "ExampleMDMDebug: ControlsBuilder"
    }

    fun build(): List<MdmSwitchControl> {
        return listOf(
            MdmSwitchControl(
                title = "Block Bluetooth",
                isChecked = isBluetoothBlocked,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
                        if (isChecked) {
                            dpm.addUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH)
                        } else {
                            dpm.clearUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH)
                        }
                    }
                }
            ),
            MdmSwitchControl(
                title = "Block Bluetooth Sharing",
                isChecked = isBluetoothSharingBlocked,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
                        if (isChecked) {
                            dpm.addUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH_SHARING)
                        } else {
                            dpm.clearUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH_SHARING)
                        }
                    }
                }
            ),
            MdmSwitchControl(
                title = "Block Airplane Mode",
                isChecked = isAirplaneModeBlocked,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
                        if (isChecked) {
                            dpm.addUserRestriction(admin, UserManager.DISALLOW_AIRPLANE_MODE)
                        } else {
                            dpm.clearUserRestriction(admin, UserManager.DISALLOW_AIRPLANE_MODE)
                        }
                    }
                }
            ),
            MdmSwitchControl(
                title = "Disable SMS",
                isChecked = isSmsBlocked,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
                        if (isChecked) {
                            dpm.addUserRestriction(admin, UserManager.DISALLOW_SMS)
                        } else {
                            dpm.clearUserRestriction(admin, UserManager.DISALLOW_SMS)
                        }
                    }
                }
            ),
            MdmSwitchControl(
                title = "Disable Outgoing Calls",
                isChecked = isCallBlocked,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
                        if (isChecked) {
                            dpm.addUserRestriction(admin, UserManager.DISALLOW_OUTGOING_CALLS)
                        } else {
                            dpm.clearUserRestriction(admin, UserManager.DISALLOW_OUTGOING_CALLS)
                        }
                    }
                }
            ),
            MdmSwitchControl(
                title = "Disable Camera",
                isChecked = isCameraDisabled,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
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
                    ifAdminActive(dpm, admin) {
                        dpm.setApplicationHidden(admin, SINGLE_PACKAGE, isChecked)
                    }
                }
            ),
            MdmSwitchControl(
                title = "Enforce App Whitelist",
                isChecked = false,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
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
                isChecked = isWebsiteWhitelistEnabled,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
                        BROWSER_PKG.forEach { pkg ->
                            try {
                                if (isChecked) {
                                    Log.d("ChromeRestrictions", "Applying to $pkg → $restrictions")
                                    dpm.setApplicationRestrictions(admin, pkg, restrictions)
                                    val applied = dpm.getApplicationRestrictions(admin, pkg)
                                    Log.d("ChromeRestrictions", "Restrictions for $pkg: $applied")
                                } else {
                                    Log.d("ChromeRestrictions", "Clearing restrictions for $pkg")
                                    dpm.setApplicationRestrictions(admin, pkg, Bundle()) // clear
                                }
                            } catch (e: Exception) {
                                Log.e("ChromeRestrictions", "Failed for $pkg: ${e.message}")
                            }
                        }
                    }
                }
            ),
            MdmSwitchControl(
                title = "Disable Screenshot",
                isChecked = isScreenshotDisabled,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
                        dpm.setScreenCaptureDisabled(admin, isChecked)
                    }
                }
            ),
            MdmSwitchControl(
                title = "Disable MTP",
                isChecked = isMtpBlocked,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
                        try {
                            if (isChecked) {
                                dpm.addUserRestriction(
                                    admin,
                                    UserManager.DISALLOW_USB_FILE_TRANSFER
                                )
                                Log.i(
                                    TAG,
                                    "MTP file transfer disabled via DISALLOW_USB_FILE_TRANSFER"
                                )
                            } else {
                                dpm.clearUserRestriction(
                                    admin,
                                    UserManager.DISALLOW_USB_FILE_TRANSFER
                                )
                                Log.i(
                                    TAG,
                                    "MTP file transfer enabled by clearing DISALLOW_USB_FILE_TRANSFER"
                                )
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error while toggling MTP restriction: ${e.message}", e)
                        }
                    }
                }
            ),
            MdmSwitchControl(
                title = "Disable Microphone",
                isChecked = isMicrophoneDisabled,
                onCheckedChange = { isChecked ->
                    ifAdminActive(dpm, admin) {
                        if (isChecked) {
                            dpm.addUserRestriction(admin, UserManager.DISALLOW_UNMUTE_MICROPHONE)
                        } else {
                            dpm.clearUserRestriction(admin, UserManager.DISALLOW_UNMUTE_MICROPHONE)
                        }
                    }
                }
            ),
        )
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

    private val isWebsiteWhitelistEnabled: Boolean
        get() {
            val restrictions = dpm.getApplicationRestrictions(admin, BROWSER_PKG[0])
            val urlBlocklistJson = restrictions.getString("URLBlocklist")
            val urlAllowlistJson = restrictions.getString("URLAllowlist")

            return urlBlocklistJson?.contains("*") == true &&
                    !urlAllowlistJson.isNullOrBlank() &&
                    urlAllowlistJson != "[]"
        }


    private inline fun ifAdminActive(
        dpm: DevicePolicyManager,
        admin: ComponentName,
        block: () -> Unit
    ) {
        if (dpm.isAdminActive(admin)) block()
    }
}