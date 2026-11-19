package com.example.pokedex.ViewModel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokedex.Data.remote.AuthRepository
import com.example.pokedex.R

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _carregando = MutableLiveData(false)
    val carregando: LiveData<Boolean> = _carregando

    private val _mensagem = MutableLiveData<Int?>()
    val mensagem: LiveData<Int?> = _mensagem

    private val _logado = MutableLiveData(false)
    val logado: LiveData<Boolean> = _logado

    fun entrar(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _mensagem.value = R.string.error_fill_fields
            return
        }
        _carregando.value = true
        repository.login(email.trim(), senha) { erro ->
            _carregando.value = false
            if (erro == null) _logado.value = true
            else _mensagem.value = erro.toAuthMessage()
        }
    }

    fun esqueciSenha(email: String) {
        val e = email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            _mensagem.value = R.string.error_type_email_for_reset
            return
        }
        repository.sendPasswordReset(e) { erro ->
            _mensagem.value = if (erro == null) R.string.reset_email_sent else erro.toAuthMessage()
        }
    }

    fun mensagemExibida() {
        _mensagem.value = null
    }

    fun navegacaoFeita() {
        _logado.value = false
    }
}
