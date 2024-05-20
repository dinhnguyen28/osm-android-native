package com.example.landmarkremark.ui.map

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.landmarkremark.database.repository.NoteRepository
import com.example.landmarkremark.models.MarkerInfo
import com.example.landmarkremark.models.Notes
import com.example.landmarkremark.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val respository: NoteRepository
) : ViewModel() {

    val geoPoint: LiveData<GeoPoint> get() = _geoPoint
    private val _geoPoint = MutableLiveData<GeoPoint>()

    val allNotes: LiveData<UiState<List<Notes>>> get() = _allNotes
    private val _allNotes = MutableLiveData<UiState<List<Notes>>>()

    val addNote: LiveData<UiState<String>> get() = _addNotes
    private val _addNotes = MutableLiveData<UiState<String>>()

    val myNotes: LiveData<UiState<List<Notes>>> get() = _myNotes
    private val _myNotes = MutableLiveData<UiState<List<Notes>>>()

    val markerInfo: LiveData<MarkerInfo> get() = _markerInfo
    private val _markerInfo = MutableLiveData<MarkerInfo>()

    private val _markerData = MutableLiveData<Notes>()
    val markerData: LiveData<Notes> get() = _markerData

    val searchNote: LiveData<UiState<List<Notes>>> get() = _searchNote
    private val _searchNote = MutableLiveData<UiState<List<Notes>>>()

    fun onSendMarkerData(data: Notes) {
        _markerData.value = data
    }

    fun onSaveMarkerInfo(markerInfo: MarkerInfo, mNote: Notes) {
        _markerInfo.value = markerInfo

        saveNoteToFireBase(mNote)
    }

    fun addMarkerGeoPoint(geoPoint: GeoPoint) {
        _geoPoint.value = geoPoint
    }

    private fun saveNoteToFireBase(note: Notes) {
        _addNotes.value = UiState.Loading

        respository.saveNoteToFirebase(note) {
            _addNotes.value = it
        }
    }

    fun getAllNotes() {
        _allNotes.value = UiState.Loading

        respository.getAllNote {
            _allNotes.value = it
        }
    }

    fun getMyNotes(username: String) {
        _myNotes.value = UiState.Loading

        respository.getMyNote(username) {
            _myNotes.value = it
        }
    }

    fun searchNoteByUsername(username: String) {
        _searchNote.value = UiState.Loading

        respository.searchNotesByUserName(username) {
            _searchNote.value = it
        }
    }

    companion object {
        private const val USERS = "users"
    }

}