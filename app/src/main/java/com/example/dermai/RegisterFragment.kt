package com.example.dermai

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dermai.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Eğer giriş yapılmışsa ana sayfaya yönlendir
        if (auth.currentUser != null) {
            findNavController().navigate(R.id.homeFragment)
            return
        }

        val harfFilter = InputFilter { source, _, _, _, _, _ ->
            val regex = Regex("^[a-zA-ZğüşöçıİĞÜŞÖÇ\\s]*$")
            if (source.matches(regex)) source else ""
        }

        binding.nameEditText.filters = arrayOf(harfFilter)
        binding.surnameEditText.filters = arrayOf(harfFilter)
        binding.phoneEditText.filters = arrayOf(InputFilter.LengthFilter(10))

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val surname = binding.surnameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            when {
                name.isEmpty() -> showToast("Lütfen adınızı girin.")
                surname.isEmpty() -> showToast("Lütfen soyadınızı girin.")
                !email.contains("@") || !email.contains(".") -> showToast("Geçerli bir e-posta girin.")
                phone.length < 10 -> showToast("Telefon numarası en az 10 haneli olmalı.")
                password.length < 6 -> showToast("Şifreniz en az 6 karakter olmalı.")
                else -> {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val fullName = "$name $surname"
                            val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            prefs.edit()
                                .putString("username", fullName)
                                .putString("email", email)
                                .putString("phone", phone)
                                .apply()

                            auth.signOut() // 🔐 Otomatik girişi iptal et

                            showToast("Kayıt başarılı! Giriş yapabilirsiniz.")
                            findNavController().navigate(R.id.signInFragment)
                        }
                        .addOnFailureListener {
                            showToast("Kayıt başarısız: ${it.message}")
                        }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
