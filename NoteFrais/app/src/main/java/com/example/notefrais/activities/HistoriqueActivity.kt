package com.example.notefrais.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.notefrais.MainActivity
import com.example.notefrais.screens.HistoriqueScreen
import com.example.notefrais.ui.theme.NoteFraisTheme

class HistoriqueActivity : ComponentActivity() {

    companion object {
        const val REQUEST_CODE_NOUVELLE_NOTE = 2001
        const val REQUEST_CODE_DETAIL = 2002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteFraisTheme {
                HistoriqueScreen(
                    onBackClick = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    },
                    onNouvelleNoteClick = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivityForResult(intent, REQUEST_CODE_NOUVELLE_NOTE)
                    },
                    onNoteClick = { noteId ->
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("note_id", noteId)
                        startActivityForResult(intent, REQUEST_CODE_DETAIL)
                    }
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_NOUVELLE_NOTE -> {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Nouvelle note créée avec succès", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CODE_DETAIL -> {
                if (resultCode == RESULT_OK && data != null) {
                    val action = data.getStringExtra("action")
                    val message = data.getStringExtra("message") ?: ""

                    when (action) {
                        "supprime" -> {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                        "modifie" -> {
                            val intent = Intent(this, MainActivity::class.java)
                            data.extras?.let { extras ->
                                intent.putExtras(extras)
                            }
                            startActivityForResult(intent, REQUEST_CODE_NOUVELLE_NOTE)
                        }
                    }
                }
            }
        }
    }
}