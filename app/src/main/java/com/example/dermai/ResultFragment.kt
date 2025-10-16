package com.example.dermai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dermai.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Argumentları al
        val disease = arguments?.getString("disease") ?: "Bilinmiyor"
        val confidence = arguments?.getFloat("confidence") ?: 0f
        val alt1 = arguments?.getString("alt1") ?: "-"
        val alt1Conf = arguments?.getFloat("alt1Conf") ?: 0f
        val alt2 = arguments?.getString("alt2") ?: "-"
        val alt2Conf = arguments?.getFloat("alt2Conf") ?: 0f

        // Verileri ekranda göster
        binding.textDisease.text = "Tahmin: $disease"
        binding.textConfidence.text = "Güven Skoru: %.1f%%".format(confidence)
        binding.textOthers.text = "Diğer Tahminler:\n• $alt1 (%.1f%%)\n• $alt2 (%.1f%%)".format(alt1Conf, alt2Conf)

        // ✅ Paylaş Butonu
        binding.shareButton.setOnClickListener {
            val resultText = """
                📊 Analiz Tamamlandı
                
                ${binding.textDisease.text}
                ${binding.textConfidence.text}
                ${binding.textOthers.text}
                
                ⚠️ ${binding.textDescription.text}
            """.trimIndent()

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, resultText)
                type = "text/plain"
            }

            startActivity(Intent.createChooser(shareIntent, "Sonucu paylaş"))
        }

        // ✅ Bilgi Al Butonu
        binding.infoButton.setOnClickListener {
            val query = disease.replace(" ", "+")
            val url = "https://www.google.com/search?q=$query+nedir"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
