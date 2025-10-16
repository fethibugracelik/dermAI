package com.example.dermai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dermai.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val historyList: MutableList<ResultData>,
    private val onDelete: (ResultData) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.binding.apply {
            textDisease.text = "Tahmin: ${item.disease}"
            textConfidence.text = "Güven: %${String.format("%.1f", item.confidence)}"
            textAlternatives.text = "Alternatifler:\n• ${item.alt1} (%${String.format("%.1f", item.alt1Conf)})\n• ${item.alt2} (%${String.format("%.1f", item.alt2Conf)})"
            textTimestamp.text = "Tarih: ${item.timestamp}"

            // 🔥 Sil butonu tıklanınca dışarıdan verilen onDelete fonksiyonunu çağır
            deleteButton.setOnClickListener {
                onDelete(item)
            }
        }
    }

    override fun getItemCount(): Int = historyList.size
}
