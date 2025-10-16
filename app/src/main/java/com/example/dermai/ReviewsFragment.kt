package com.example.dermai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dermai.databinding.FragmentReviewsBinding
import com.example.dermai.model.YorumModel
import com.example.dermai.adapter.YorumAdapter

class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    private val yorumList = mutableListOf<YorumModel>()
    private lateinit var adapter: YorumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView ve adapter ayarları
        adapter = YorumAdapter(yorumList)
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.commentsRecyclerView.adapter = adapter

        // Gönder butonu işlevi
        binding.submitCommentButton.setOnClickListener {
            val rating = binding.ratingBar.rating
            val comment = binding.commentEditText.text.toString()

            if (rating == 0f) {
                Toast.makeText(requireContext(), "Lütfen yıldızla puan verin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (comment.isBlank()) {
                Toast.makeText(requireContext(), "Lütfen yorum yazın.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Listeye yorum ekle
            val yeniYorum = YorumModel(comment, rating)
            yorumList.add(0, yeniYorum)
            adapter.notifyItemInserted(0)
            binding.commentsRecyclerView.scrollToPosition(0)

            // Alanları temizle
            binding.ratingBar.rating = 0f
            binding.commentEditText.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
