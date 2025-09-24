package com.example.workshop3.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Note(
    val id: Int,
    val titre: String,
    val contenu: String,
    val dateCreation: Long = System.currentTimeMillis()
) {
    fun obtenirDateFormatee(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(dateCreation))
    }
}