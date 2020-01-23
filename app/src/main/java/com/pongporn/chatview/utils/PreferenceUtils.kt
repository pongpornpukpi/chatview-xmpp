package com.pongporn.chatview.utils

import com.mohamadamin.kpreferences.preference.Preference

class PreferenceUtils {

    var isFirst : Boolean by Preference(false, "isFirst")

    companion object {
        val instance: PreferenceUtils by lazy { PreferenceUtils() }
    }
}