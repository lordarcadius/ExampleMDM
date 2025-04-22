package com.floydwiz.mdmexample.utils

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.UserManager
import com.floydwiz.mdmexample.data.MdmSwitchControl

object Utils {
    fun createMdmControls(
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
            )
        )
    }

}