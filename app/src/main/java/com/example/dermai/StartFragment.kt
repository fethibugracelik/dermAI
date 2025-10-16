package com.example.dermai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class StartFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val activity = requireActivity() as MainActivity

        if (currentUser != null) {
            // ✅ Giriş yapılmışsa alt menüyü göster ve ana sayfaya yönlendir
            activity.binding.bottomNavigationView.visibility = View.VISIBLE

            findNavController().navigate(
                R.id.homeFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.startFragment, true)
                    .build()
            )
        } else {
            // ❌ Giriş yapılmamışsa alt menüyü gizle ve giriş sayfasına yönlendir
            activity.binding.bottomNavigationView.visibility = View.GONE

            findNavController().navigate(
                R.id.signInFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.startFragment, true)
                    .build()
            )
        }
    }
}
