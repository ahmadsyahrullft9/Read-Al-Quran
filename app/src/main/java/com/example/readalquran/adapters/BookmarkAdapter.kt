package com.example.readalquran.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.readalquran.databinding.ItemBookmarkBinding
import com.example.readalquran.models.Bookmark
import java.text.SimpleDateFormat
import java.util.*

class BookmarkAdapter : ListAdapter<Bookmark, BookmarkAdapter.BookmarkViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val itemBookmarkBinding =
            ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(itemBookmarkBinding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Bookmark>() {
        override fun areItemsTheSame(oldItem: Bookmark, newItem: Bookmark): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Bookmark, newItem: Bookmark): Boolean =
            oldItem == newItem

    }

    inner class BookmarkViewHolder(val itemBookmarkBinding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(itemBookmarkBinding.root) {

        fun onBind(bookmark: Bookmark) {
            itemBookmarkBinding.apply {
                val date = Date(bookmark.date_created)
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                txtDateCreated.text = simpleDateFormat.format(date)
                txtQsBookmark.text = "QS ${bookmark.name_sura} : ${bookmark.index_aya}"
            }
        }

    }
}