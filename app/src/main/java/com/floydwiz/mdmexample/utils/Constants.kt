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

    val BROWSER_PKG = listOf(
        "com.floydwiz.browser",         // PrimeOS Browser
        "com.android.chrome",           // Chrome Stable
        "com.chrome.beta",              // Chrome Beta
        "com.chrome.dev",               // Chrome Dev
        "com.chrome.canary",            // Chrome Canary
        "com.brave.browser",            // Brave
        "com.microsoft.emmx",           // Edge
        "com.opera.browser",            // Opera
        "com.opera.mini.native",        // Opera Mini
        "com.vivaldi.browser",          // Vivaldi
        "com.sec.android.app.sbrowser", // Samsung Internet
        "com.kiwibrowser.browser",      // Kiwi
        "com.yandex.browser",           // Yandex
        "com.duckduckgo.mobile.android" // DuckDuckGo
    )

    // Define system app allowlist that the user is allowed to toggle
    val systemAppAllowlist = listOf(
        "com.floydwiz.primestore",
        "com.floydwiz.primefilemanager",
    )

    val restrictions = Bundle().apply {
        putString("URLBlocklist", """["*"]""") // Block all URLs
        putString(
            "URLAllowlist",
            formatUrlList(arrayListOf("vipuljha.com", "primebook.in"))
        ) //Website allowlist
        putInt("IncognitoModeAvailability", 1) // Disable Incognito
        putInt("DownloadRestrictions", 3) // Block all downloads. Works but downloads page crash
        putString("HomepageLocation", "https://www.primebook.in") // Set homepage (only when home button is pressed)
    }

    private fun formatUrlList(websites: List<String>): String {
        val formatted =
            websites.joinToString(prefix = "[\"", separator = "\", \"", postfix = "\"]") { domain ->
                domain.removePrefix("https://").removePrefix("http://")
            }
        return formatted
    }

    /*    val restrictions = Bundle().apply {
            putString("HomepageLocation", "https://primebook.in")
            putBoolean("HomepageIsNewTabPage", false)
            putInt("IncognitoModeAvailability", 1) // Disable Incognito
        }*/
}