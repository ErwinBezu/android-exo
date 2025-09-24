package com.example.workshop3.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.workshop3.data.Note
import com.example.workshop3.ui.NotesApp
import com.example.workshop3.ui.components.CartePourNote
import com.example.workshop3.ui.theme.NotesAppTheme

@Preview(showBackground = true)
@Composable
fun PreviewNotesApp() {
    NotesAppTheme {
        NotesApp()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCartePourNote() {
    NotesAppTheme {
        CartePourNote(
            note = Note(1, "Exemple de note", "Voici un exemple de contenu de note pour la prévisualisation"),
            onClick = { }
        )
    }
}