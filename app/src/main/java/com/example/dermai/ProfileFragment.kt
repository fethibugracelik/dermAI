package com.example.dermai

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dermai.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        binding.nameText.text = prefs.getString("username", "Bilinmiyor")
        binding.emailText.text = prefs.getString("email", "Bilinmiyor")
        binding.phoneText.text = prefs.getString("phone", "Bilinmiyor")

        // Toolbar geri ok
        binding.profileToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Çıkış yap ve giriş ekranına dön
        binding.signOutButton.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.startFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
