package com.example.dermai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dermai.databinding.FragmentSigninBinding
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔒 Giriş yapılmadan alt menü gizlenir
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE

        auth = FirebaseAuth.getInstance()

        binding.signinButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Giriş başarılı!", Toast.LENGTH_SHORT).show()

                    // ✅ Alt menü görünür hale getirilir
                    (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE

                    // Ana sayfaya yönlendirme
                    findNavController().navigate(R.id.homeFragment)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Hatalı giriş: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        // 👇 "Hesabın yok mu? Kayıt Ol" yazısına tıklanınca kayıt ekranına geç
        binding.goToRegisterText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
