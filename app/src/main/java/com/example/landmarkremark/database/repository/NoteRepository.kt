package com.example.landmarkremark.database.repository

import com.example.landmarkremark.models.Notes
import com.example.landmarkremark.utils.UiState

interface NoteRepository {

    fun login(username: String): Result<String>

    fun saveNoteToFirebase(
        note: Notes,
        result: (UiState<String>) -> Unit
    )

    fun getAllNote(result: (UiState<List<Notes>>) -> Unit)

    fun getMyNote(
        username: String,
        result: (UiState<List<Notes>>) -> Unit
    )

    fun deleteNote(
        id: String,
        result: (UiState<String>) -> Unit
    )

    fun searchNotesByUserName(
        username: String,
        result: (
            UiState<List<Notes>>
        ) -> Unit
    )
}