package com.floydwiz.mdmexample.data

data class MdmSwitchControl(
    val title: String,
    val isChecked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)