package com.example.readalquran.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.readalquran.databinding.ItemAyatBinding
import com.example.readalquran.lib.convertToArabic
import com.example.readalquran.models.Ayat

class AyatAdapter : PagedListAdapter<Ayat, AyatAdapter.AyatViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyatAdapter.AyatViewHolder {
        val itemAyatBinding =
            ItemAyatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AyatViewHolder(itemAyatBinding)
    }

    override fun onBindViewHolder(holder: AyatAdapter.AyatViewHolder, position: Int) {
        holder.onBind(getItem(position)!!)
    }

    inner class AyatViewHolder(val itemAyatBinding: ItemAyatBinding) :
        RecyclerView.ViewHolder(itemAyatBinding.root) {

        fun onBind(ayat: Ayat) {
            itemAyatBinding.apply {
                txtNumAyat.text = convertToArabic(ayat.aya)
                txtAyat.text = ayat.text
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Ayat>() {
        override fun areItemsTheSame(oldItem: Ayat, newItem: Ayat): Boolean =
            oldItem.index == newItem.index

        override fun areContentsTheSame(oldItem: Ayat, newItem: Ayat): Boolean =
            oldItem == newItem
    }
}