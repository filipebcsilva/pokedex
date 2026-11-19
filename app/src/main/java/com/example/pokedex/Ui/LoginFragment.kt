package com.example.pokedex.Ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pokedex.R
import com.example.pokedex.ViewModel.LoginViewModel
import com.example.pokedex.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            viewModel.entrar(
                binding.editEmail.text.toString(),
                binding.editPassword.text.toString()
            )
        }
        binding.forgotPassword.setOnClickListener {
            viewModel.esqueciSenha(binding.editEmail.text.toString())
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        setObservers()
    }

    private fun setObservers() {
        viewModel.carregando.observe(viewLifecycleOwner) { carregando ->
            binding.loginButton.isEnabled = !carregando
            binding.progressLogin.visibility = if (carregando) View.VISIBLE else View.GONE
        }

        viewModel.mensagem.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                viewModel.mensagemExibida()
            }
        }

        viewModel.logado.observe(viewLifecycleOwner) { logado ->
            if (logado) {
                viewModel.navegacaoFeita()
                findNavController().navigate(R.id.action_loginFragment_to_searchFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
