package com.pongporn.chatview.utils

import com.mohamadamin.kpreferences.preference.Preference

class PreferenceUtils {

    var isFirst : Boolean by Preference(false, "isFirst")
    var ACCESS_TOKEN : String by Preference("", "ACCESS_TOKEN")

    companion object {
        val instance: PreferenceUtils by lazy { PreferenceUtils() }
    }
}