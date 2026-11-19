package com.example.pokedex.ViewModel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokedex.Data.remote.AuthRepository
import com.example.pokedex.R

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _carregando = MutableLiveData(false)
    val carregando: LiveData<Boolean> = _carregando

    private val _mensagem = MutableLiveData<Int?>()
    val mensagem: LiveData<Int?> = _mensagem

    private val _cadastrado = MutableLiveData(false)
    val cadastrado: LiveData<Boolean> = _cadastrado

    fun cadastrar(nome: String, nick: String, email: String, senha1: String, senha2: String) {
        val erro = when {
            nome.isBlank() || nick.isBlank() || email.isBlank() || senha1.isEmpty() ->
                R.string.error_fill_fields
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> R.string.error_invalid_email
            senha1.length < MIN_PASSWORD -> R.string.error_weak_password
            senha1 != senha2 -> R.string.error_passwords_differ
            else -> null
        }
        if (erro != null) {
            _mensagem.value = erro
            return
        }

        _carregando.value = true
        repository.register(nome.trim(), nick.trim(), email.trim(), senha1) { e ->
            _carregando.value = false
            if (e == null) _cadastrado.value = true
            else _mensagem.value = e.toAuthMessage()
        }
    }

    fun mensagemExibida() {
        _mensagem.value = null
    }

    fun navegacaoFeita() {
        _cadastrado.value = false
    }

    companion object {
        private const val MIN_PASSWORD = 6
    }
}
