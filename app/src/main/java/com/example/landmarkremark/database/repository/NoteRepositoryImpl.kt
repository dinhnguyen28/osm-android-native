package com.example.landmarkremark.database.repository

import android.util.Log
import com.example.landmarkremark.models.Notes
import com.example.landmarkremark.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException

class NoteRepositoryImpl(
    private val database: FirebaseFirestore
) : NoteRepository {

    override fun login(username: String): Result<String> {

        return try {

            //TODO: handler login here
            //            database.collection(LoginRepository.USERS)
            //                .document(username).set(data)

            Result.Success(username)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }

    }

    override fun saveNoteToFirebase(
        note: Notes,
        result: (UiState<String>) -> Unit
    ) {

        val dbRef = database.collection(USERS).document()
        note.id = dbRef.id

        dbRef.set(note).addOnSuccessListener {
            result.invoke(
                UiState.Success("")
            )
        }.addOnFailureListener {
            result.invoke(
                UiState.Failure(it.localizedMessage)
            )
        }

    }

    override fun getAllNote(result: (UiState<List<Notes>>) -> Unit) {
        database.collection(USERS).get()
            .addOnSuccessListener { docSnaps ->
                val notes = arrayListOf<Notes>()

                for (doc in docSnaps) {
                    val note = doc.toObject(Notes::class.java)

                    Log.d("DATA_DOCS", note.toString())
                    notes.add(note)
                }
                result.invoke(
                    UiState.Success(notes)
                )
            }.addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }

    }

    override fun getMyNote(
        username: String,
        result: (UiState<List<Notes>>) -> Unit
    ) {
        database.collection(USERS).whereEqualTo("userName", username)
            .get()
            .addOnSuccessListener { docSnaps ->
                val notes = arrayListOf<Notes>()

                for (doc in docSnaps) {
                    val note = doc.toObject(Notes::class.java)

                    Log.d("DATA_DOCS", note.toString())
                    notes.add(note)
                }
                result.invoke(
                    UiState.Success(notes)
                )
            }.addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun deleteNote(
        id: String,
        result: (UiState<String>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun searchNotesByUserName(
        username: String,
        result: (UiState<List<Notes>>) -> Unit
    ) {

        //Trick like full-text-search using in firebase
        val query = database.collection(USERS).orderBy("userName")
            .whereGreaterThanOrEqualTo("userName", username)
            .whereLessThanOrEqualTo("userName", username + "\uf8ff")
            .get()


        query.addOnSuccessListener { docSnaps ->
            val notes = arrayListOf<Notes>()


            if (docSnaps.isEmpty) {
                result.invoke(
                    UiState.Success(notes)
                )
            } else {
                for (doc in docSnaps) {
                    val note = doc.toObject(Notes::class.java)

                    Log.d("DATA_DOCS_SEARCH", note.toString())
                    notes.add(note)
                }
                result.invoke(
                    UiState.Success(notes)
                )
            }
        }.addOnFailureListener {
            result.invoke(
                UiState.Failure(it.localizedMessage)
            )
        }

    }

    companion object {
        private const val USERS = "users"
    }
}