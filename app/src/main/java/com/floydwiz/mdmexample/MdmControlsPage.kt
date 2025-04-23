package com.floydwiz.mdmexample

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.UserManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.floydwiz.mdmexample.data.MdmSwitchControl
import com.floydwiz.mdmexample.utils.Utils

const val TAG = "mdmexampledebug"

@Composable
fun MdmControlsPage(
    paddingValues: PaddingValues,
    admin: ComponentName,
    dpm: DevicePolicyManager
) {
    val context = LocalContext.current
    val singlePackage = "com.floydwiz.forum"
    val controls = remember {
        mutableStateListOf<MdmSwitchControl>()
    }
    val isInstallDisabled =
        dpm.getUserRestrictions(admin).getBoolean(UserManager.DISALLOW_INSTALL_APPS, false)
    val isCameraDisabled = dpm.getCameraDisabled(admin)
    val isPackageHidden = dpm.isApplicationHidden(admin, singlePackage)

    println("$TAG isInstallDisabled: $isInstallDisabled, isCameraDisabled: $isCameraDisabled, isPackageHidden: $isPackageHidden")

    LaunchedEffect(Unit) {
        if (controls.isEmpty()) {
            controls += Utils.createMdmControls(
                admin = admin,
                dpm = dpm,
                singlePackage = singlePackage,
                isCameraDisabled = isCameraDisabled,
                isInstallDisabled = isInstallDisabled,
                isPackageHidden = isPackageHidden,
                context = context
            )
        }
    }


    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        itemsIndexed(controls) { index, control ->
            MdmSwitchItem(
                title = control.title,
                isChecked = control.isChecked,
                onCheckedChange = { isChecked ->
                    control.onCheckedChange(isChecked)
                    controls[index] = control.copy(isChecked = isChecked)
                }
            )
        }
    }
}

@Composable
fun MdmSwitchItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
