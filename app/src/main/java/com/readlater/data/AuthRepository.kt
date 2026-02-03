package com.readlater.data

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Scope
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val context: Context) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(CalendarScopes.CALENDAR_EVENTS))
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    init {
        checkExistingAuth()
    }

    private fun checkExistingAuth() {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null && hasCalendarScope(account)) {
            _authState.value = AuthState.Authenticated(account)
        } else {
            _authState.value = AuthState.NotAuthenticated
        }
    }

    private fun hasCalendarScope(account: GoogleSignInAccount): Boolean {
        return GoogleSignIn.hasPermissions(account, Scope(CalendarScopes.CALENDAR_EVENTS))
    }

    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    fun launchSignIn(launcher: ActivityResultLauncher<Intent>) {
        launcher.launch(googleSignInClient.signInIntent)
    }

    fun handleSignInResult(data: Intent?) {
        if (data == null) {
            _authState.value = AuthState.NotAuthenticated
            return
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                _authState.value = AuthState.Authenticated(account)
            } else {
                _authState.value = AuthState.NotAuthenticated
            }
        } catch (e: ApiException) {
            val status = e.statusCode
            if (status == CommonStatusCodes.CANCELED || status == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                _authState.value = AuthState.NotAuthenticated
            } else {
                _authState.value = AuthState.Error(e.message ?: "sign in failed")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "sign in failed")
        }
    }

    suspend fun signOut() {
        googleSignInClient.signOut()
        _authState.value = AuthState.NotAuthenticated
    }

    fun getAccount(): GoogleSignInAccount? {
        return when (val state = _authState.value) {
            is AuthState.Authenticated -> state.account
            else -> null
        }
    }
}

sealed class AuthState {
    data object Loading : AuthState()
    data object NotAuthenticated : AuthState()
    data class Authenticated(val account: GoogleSignInAccount) : AuthState()
    data class Error(val message: String) : AuthState()
}
