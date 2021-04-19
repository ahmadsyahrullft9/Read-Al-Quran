package com.example.readalquran.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.example.readalquran.databinding.ItemSuraBinding
import com.example.readalquran.lib.convertToArabic
import com.example.readalquran.models.Sura

class SuraAdapter(val itemListener: ItemListener, val mComparator: Comparator<Sura>) :
    RecyclerView.Adapter<SuraAdapter.SuraViewHolder>() {

    companion object {
        val NUMERICAL_COMPARATOR: Comparator<Sura> = Comparator { a, b ->
            a.index.compareTo(b.index)
        }
    }

    val mCallback: SortedList.Callback<Sura> = object : SortedList.Callback<Sura>() {

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun compare(a: Sura, b: Sura): Int {
            return mComparator.compare(a, b)
        }

        override fun areContentsTheSame(oldItem: Sura, newItem: Sura): Boolean {
            return oldItem.equals(newItem)
        }

        override fun areItemsTheSame(item1: Sura, item2: Sura): Boolean {
            return item1.index == item2.index
        }
    }

    val sortedList = SortedList(Sura::class.java, this.mCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuraViewHolder {
        val itemSuraBinding: ItemSuraBinding =
            ItemSuraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuraViewHolder(itemSuraBinding)
    }

    override fun onBindViewHolder(holder: SuraViewHolder, position: Int) {
        val item: Sura = getItem(position)
        holder.onBind(item)
    }

    fun getItem(position: Int): Sura {
        return sortedList.get(position)
    }

    fun add(model: Sura) {
        sortedList.add(model)
    }

    fun remove(model: Sura) {
        sortedList.remove(model)
    }

    fun add(models: List<Sura>) {
        sortedList.addAll(models)
    }

    fun remove(models: List<Sura>) {
        sortedList.beginBatchedUpdates()
        for (model in models) {
            sortedList.remove(model)
        }
        sortedList.endBatchedUpdates()
    }

    fun replaceAll(models: List<Sura>) {
        sortedList.beginBatchedUpdates()
        for (i in sortedList.size() - 1 downTo 0) {
            val model: Sura = sortedList.get(i)
            if (!models.contains(model)) {
                sortedList.remove(model)
            }
        }
        sortedList.addAll(models)
        sortedList.endBatchedUpdates()
    }

    fun removeAll() {
        sortedList.beginBatchedUpdates()
        repeat(sortedList.size()) {
            sortedList.removeItemAt(it)
        }
        sortedList.endBatchedUpdates()
    }

    override fun getItemCount(): Int {
        return sortedList.size()
    }

    interface ItemListener {
        fun onClickListener(sura: Sura)
    }

    inner class SuraViewHolder(val binding: ItemSuraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    itemListener.onClickListener(getItem(adapterPosition))
                }
            }
        }

        fun onBind(sura: Sura) {
            binding.apply {
                txtNum.text = convertToArabic(sura.index)
                txtSuraDesc.text = "${sura.ayas} ayat, ${sura.rukus} ruku'"
                txtSuraEng.text = "${sura.ename}"
                txtSuraName.text = sura.tname
                txtSuraArab.text = sura.name
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Sura>() {
        override fun areItemsTheSame(oldItem: Sura, newItem: Sura) =
            oldItem.index == newItem.index

        override fun areContentsTheSame(oldItem: Sura, newItem: Sura) =
            oldItem == newItem
    }
}