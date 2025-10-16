package com.example.dermai

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dermai.databinding.FragmentUploadBinding
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.*
import java.nio.*
import java.nio.channels.FileChannel
import android.util.Log
import androidx.navigation.fragment.findNavController


class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private lateinit var tflite: Interpreter
    private val inputSize = 224

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)

        try {
            try {
                Log.d("MODEL_LOAD", "Model yükleniyor...")
                val options = Interpreter.Options()
                tflite = Interpreter(loadModelFile("skin_model.tflite"), options)
                Log.d("MODEL_LOAD", "Model başarıyla yüklendi")
            } catch (e: Exception) {
                Log.e("MODEL_LOAD", "Model yüklenemedi: ${e.message}")
                Toast.makeText(requireContext(), "Model yüklenemedi: ${e.message}", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Model yüklenemedi: ${e.message}", Toast.LENGTH_LONG).show()
        }

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            val imageStream = requireActivity().contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(imageStream)
            binding.selectedImageView.setImageBitmap(bitmap)
        }
    }

    private fun analyzeImage() {
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Lütfen önce resim seçin", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val imageStream = requireActivity().contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(imageStream)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

            val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)
            val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 23), org.tensorflow.lite.DataType.FLOAT32)
            tflite.run(inputBuffer, outputBuffer.buffer.rewind())

            val predictions = outputBuffer.floatArray
            val labels = getLabels()

            val top3 = predictions
                .mapIndexed { index, score -> index to score }
                .sortedByDescending { it.second }
                .take(3)

            val resultText = top3.joinToString("\n") {
                "${labels[it.first]}: ${(it.second * 100).toInt()}%"
            }

            binding.resultText.text = resultText

            // ✅ SONUÇLARI AL
            val disease = labels[top3[0].first]
            val confidence = (top3[0].second * 100).toFloat()

            val alt1 = labels[top3[1].first]
            val alt1Conf = (top3[1].second * 100).toFloat()

            val alt2 = labels[top3[2].first]
            val alt2Conf = (top3[2].second * 100).toFloat()

            val resultData = hashMapOf(
                "disease" to disease,
                "confidence" to confidence,
                "alt1" to alt1,
                "alt1Conf" to alt1Conf,
                "alt2" to alt2,
                "alt2Conf" to alt2Conf,
                "timestamp" to java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            )

            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                db.collection("users")
                    .document(uid)
                    .collection("results")
                    .add(resultData)
                    .addOnSuccessListener {
                        Log.d("FIREBASE", "Sonuç Firestore'a kaydedildi.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FIREBASE", "Firestore'a yazılamadı: ${e.message}")
                    }
            }


            // ✅ GEÇİŞ YAP
            val bundle = Bundle().apply {
                putString("disease", disease)
                putFloat("confidence", confidence)
                putString("alt1", alt1)
                putFloat("alt1Conf", alt1Conf)
                putString("alt2", alt2)
                putFloat("alt2Conf", alt2Conf)
            }

            // Navigation Graph'taki action ID doğruysa bunu kullan
            requireActivity().runOnUiThread {
                findNavController().navigate(R.id.action_uploadFragment_to_resultFragment, bundle)
            }

        } catch (e: Exception) {
            binding.resultText.text = "Hata: ${e.message}"
        }
    }


    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in pixels) {
            byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixel and 0xFF) / 255.0f)
        }

        return byteBuffer
    }

    private fun loadModelFile(fileName: String): MappedByteBuffer {
        val fileDescriptor = requireContext().assets.openFd(fileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun getLabels(): List<String> = listOf(
        "Atopik Dermatit",
        "Büllöz Hastalıklar",
        "Damar Tümörleri",
        "Siğil, Molluskum ve Viral Enfeksiyonlar",
        "Saç Dökülmesi ve Diğer Saç Hastalıkları",
        "Egzama",
        "Tırnak Mantarları ve Diğer Tırnak Hastalıkları",
        "Sedef, Liken Planus ve Benzerleri",
        "Uyuz, Lyme ve Diğer Parazitler",
        "Kurdeşen (Ürtiker)",
        "Akne ve Rozasea",
        "Vaskülit",
        "Lupus ve Bağ Doku Hastalıkları",
        "Selülit, İmpetigo ve Bakteriyel Enfeksiyonlar",
        "Sistemik Hastalıklar",
        "Melanom, Cilt Kanseri, Benler",
        "Döküntüler ve İlaç Reaksiyonları",
        "Aktinik Keratoz, Bazal Hücreli Karsinom vs.",
        "Zehirli Sarmaşık ve Temas Dermatiti",
        "Seboreik Keratoz ve İyi Huylu Tümörler",
        "Herpes, HPV ve Cinsel Yolla Bulaşan Hastalıklar",
        "Tinea, Kandida ve Mantar Enfeksiyonları",
        "Pigment Bozuklukları ve Işık Hastalıkları"
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
