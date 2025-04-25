package com.floydwiz.mdmexample.utils

import android.os.Bundle
import com.floydwiz.mdmexample.data.AppWhitelistData

object Constants {
    val APP_WHITELIST_DATA = arrayListOf(
        AppWhitelistData("com.floydwiz.forum", true),
        AppWhitelistData("com.floydwiz.browser", false),
        AppWhitelistData("com.floydwiz.youtube", true),
        AppWhitelistData("com.floydwiz.primestore", false),
        AppWhitelistData("com.floydwiz.fakepackage", false),
        AppWhitelistData("com.floydwiz.primefilemanager", false),
    )

    const val SINGLE_PACKAGE = "com.floydwiz.forum"

    const val BROWSER_PKG = "com.android.chrome"

    // Define system app allowlist that the user is allowed to toggle
    val systemAppAllowlist = listOf(
        "com.floydwiz.primestore",
        "com.floydwiz.primefilemanager",
    )

    val restrictions = Bundle().apply {
        putString("URLBlocklist", """["*"]""") // Block all URLs
        putString("URLAllowlist", """["https://primebook.in", "https://vipuljha.com", "chrome://policy"]""") //Website allowlist
        putInt("IncognitoModeAvailability", 1) // Disable Incognito
        putInt("DownloadRestrictions", 3) // Block all downloads. Works but downloads page crash
        putString("HomepageLocation", "https://www.primebook.in") // Set homepage (only when home button is pressed)
    }

    /*    val restrictions = Bundle().apply {
            putString("HomepageLocation", "https://primebook.in")
            putBoolean("HomepageIsNewTabPage", false)
            putInt("IncognitoModeAvailability", 1) // Disable Incognito
        }*/
}