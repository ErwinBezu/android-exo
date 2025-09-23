package com.example.workshop2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
) : Parcelable {

    val fullName: String
        get() = "$firstName $lastName"

    override fun toString(): String {
        return "$fullName\n$email - $phone"
    }
}