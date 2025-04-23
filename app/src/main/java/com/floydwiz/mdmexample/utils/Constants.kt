package com.floydwiz.mdmexample.utils

import com.floydwiz.mdmexample.data.AppWhitelistData

object Constants {
    val APP_WHITELIST_DATA = arrayListOf(
        AppWhitelistData("com.floydwiz.forum", true),
        AppWhitelistData("com.floydwiz.browser", false),
        AppWhitelistData("com.floydwiz.youtube", true),
        AppWhitelistData("com.floydwiz.primestore", false),
    )
}