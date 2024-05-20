package com.example.landmarkremark.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.landmarkremark.database.repository.Result

import com.example.landmarkremark.R
import com.example.landmarkremark.database.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val respository: NoteRepository
) : ViewModel() {

    private val _userNameForm = MutableLiveData<LoginFormState>()
    val userNameFormState: LiveData<LoginFormState> = _userNameForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String) {
        // can be launched in a separate asynchronous job
        val result = respository.login(username)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = result.data)
        } else {
            _loginResult.value =
                LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String) {
        if (!isUserNameValid(username)) {
            _userNameForm.value =
                LoginFormState(usernameError = R.string.invalid_username)
        } else {
            _userNameForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }
}