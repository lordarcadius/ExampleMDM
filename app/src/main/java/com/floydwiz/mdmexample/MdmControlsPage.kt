package com.floydwiz.mdmexample

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.floydwiz.mdmexample.data.MdmSwitchControl
import com.floydwiz.mdmexample.utils.ControlsBuilder

@Composable
fun MdmControlsPage(
    paddingValues: PaddingValues,
    controlsBuilder: ControlsBuilder
) {
    val controls =
        remember { mutableStateListOf<MdmSwitchControl>().apply { addAll(controlsBuilder.build()) } }

    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        itemsIndexed(controls) { index, control ->
            MdmSwitchItem(
                title = control.title,
                isChecked = control.isChecked,
                onCheckedChange = { isChecked ->
                    control.onCheckedChange(isChecked)
                    controls[index] = controls[index].copy(isChecked = isChecked)
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
