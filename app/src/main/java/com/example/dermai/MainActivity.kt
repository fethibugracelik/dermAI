package com.example.dermai

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dermai.databinding.ActivityMainBinding
import com.example.dermai.utils.ThemeUtils
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Alt menü kontrolü:
        binding.bottomNavigationView.setupWithNavController(navController)

        // Alt menüye tıklanınca giriş kontrolü:
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser == null &&
                item.itemId != R.id.signInFragment &&
                item.itemId != R.id.registerFragment
            ) {
                Toast.makeText(this, "Lütfen önce giriş yapın", Toast.LENGTH_SHORT).show()
                false
            } else {
                // Aynı destination'a tekrar tıklamayı engelle
                if (navController.currentDestination?.id != item.itemId) {
                    navController.navigate(item.itemId)
                }
                true
            }
        }

        // Başlangıçta login değilse alt menüyü sakla
        if (FirebaseAuth.getInstance().currentUser == null) {
            binding.bottomNavigationView.visibility = View.GONE
        } else {
            binding.bottomNavigationView.visibility = View.VISIBLE
        }
    }
}
