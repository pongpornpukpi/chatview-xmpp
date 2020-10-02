package com.pongporn.chatview.utils

import com.mohamadamin.kpreferences.preference.Preference

class PreferenceUtils {

    var isFirst : Boolean by Preference(false, "isFirst")
    var ACCESS_TOKEN : String by Preference("", "ACCESS_TOKEN")
    var AUTH_STATE: String by Preference("","AUTH_STATE")
    var isGoogleLogin: Boolean by Preference(false,"isGoogleLogin")

    companion object {
        val instance: PreferenceUtils by lazy { PreferenceUtils() }
    }
}