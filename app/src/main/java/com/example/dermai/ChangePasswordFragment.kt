package com.example.dermai

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dermai.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changePasswordButton.setOnClickListener {
            val newPassword = binding.newPasswordEditText.text.toString().trim()

            if (newPassword.length < 6) {
                Toast.makeText(requireContext(), "Şifre en az 6 karakter olmalı!", Toast.LENGTH_SHORT).show()
            } else {
                val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("password", newPassword).apply()

                Toast.makeText(requireContext(), "Şifreniz güncellendi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

