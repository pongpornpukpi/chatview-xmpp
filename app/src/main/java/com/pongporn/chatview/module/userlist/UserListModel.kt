package com.pongporn.chatview.module.userlist

import android.os.Parcel
import android.os.Parcelable


data class UserListModel (
    var name : String? = "",
    var isGroup : Boolean? = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeValue(isGroup)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserListModel> {
        override fun createFromParcel(parcel: Parcel): UserListModel {
            return UserListModel(parcel)
        }

        override fun newArray(size: Int): Array<UserListModel?> {
            return arrayOfNulls(size)
        }
    }
}