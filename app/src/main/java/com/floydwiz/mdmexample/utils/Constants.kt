package com.floydwiz.mdmexample.utils

import android.os.Bundle
import com.floydwiz.mdmexample.data.AppWhitelistData

object Constants {
    val APP_WHITELIST_DATA = arrayListOf(
        AppWhitelistData("com.floydwiz.forum", true),
        AppWhitelistData("com.floydwiz.browser", false),
        AppWhitelistData("com.floydwiz.youtube", true),
        AppWhitelistData("com.floydwiz.primestore", false),
        AppWhitelistData("com.floydwiz.nigger", false),
        AppWhitelistData("com.floydwiz.primefilemanager", false),
    )

    const val BROWSER_PKG = "com.microsoft.emmx"

    // Define system app allowlist that the user is allowed to toggle
    val systemAppAllowlist = listOf(
        "com.floydwiz.primestore",
        "com.floydwiz.primefilemanager",
    )

    val restrictions = Bundle().apply {
        putString("URLBlocklist", """["*"]""")
        putString("URLAllowlist", """["https://primebook.in", "https://vipuljha.com"]""")
    }

    /*    val restrictions = Bundle().apply {
            putString("HomepageLocation", "https://primebook.in")
            putBoolean("HomepageIsNewTabPage", false)
            putInt("IncognitoModeAvailability", 1) // Disable Incognito
        }*/
}