package com.example.pokedex.Ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pokedex.R
import com.example.pokedex.ViewModel.RegisterViewModel
import com.example.pokedex.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        binding.registerButton.setOnClickListener {
            viewModel.cadastrar(
                binding.editName.text.toString(),
                binding.editNick.text.toString(),
                binding.editEmail.text.toString(),
                binding.editPassword1.text.toString(),
                binding.editPassword2.text.toString()
            )
        }

        setObservers()
    }

    private fun setObservers() {
        viewModel.carregando.observe(viewLifecycleOwner) { carregando ->
            binding.registerButton.isEnabled = !carregando
            binding.progressRegister.visibility = if (carregando) View.VISIBLE else View.GONE
        }

        viewModel.mensagem.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                viewModel.mensagemExibida()
            }
        }

        viewModel.cadastrado.observe(viewLifecycleOwner) { ok ->
            if (ok) {
                viewModel.navegacaoFeita()
                findNavController().navigate(R.id.action_registerFragment_to_searchFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
