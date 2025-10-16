package com.example.dermai.adapter  // kendi paketin neyse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dermai.R
import com.example.dermai.model.YorumModel

class YorumAdapter(private val yorumList: List<YorumModel>) :
    RecyclerView.Adapter<YorumAdapter.YorumViewHolder>() {

    class YorumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val yorumText: TextView = itemView.findViewById(R.id.yorumText)
        val puanText: TextView = itemView.findViewById(R.id.puanText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YorumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_yorum, parent, false)
        return YorumViewHolder(view)
    }

    override fun onBindViewHolder(holder: YorumViewHolder, position: Int) {
        val yorum = yorumList[position]
        holder.yorumText.text = yorum.yorum
        holder.puanText.text = "Puan: ${yorum.puan}"
    }

    override fun getItemCount(): Int {
        return yorumList.size
    }
}
