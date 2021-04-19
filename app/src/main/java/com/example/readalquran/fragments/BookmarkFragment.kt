package com.example.readalquran.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.readalquran.R
import com.example.readalquran.adapters.BookmarkAdapter
import com.example.readalquran.custom.BaseFragmentBinding
import com.example.readalquran.custom.RecyclerTouchListener
import com.example.readalquran.databinding.FragmentBookmarkBinding
import com.example.readalquran.models.Bookmark
import com.example.readalquran.models.Sura
import com.example.readalquran.models.Suras
import com.example.readalquran.viewmodels.QuranDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BookmarkFragment : BaseFragmentBinding<FragmentBookmarkBinding>() {
    private val TAG = "BookmarkFragment"

    val quranDataViewModel: QuranDataViewModel by viewModels()
    lateinit var binding: FragmentBookmarkBinding
    lateinit var bookmarkAdapter: BookmarkAdapter
    var listSura: List<Sura> = ArrayList()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBookmarkBinding
        get() = FragmentBookmarkBinding::inflate

    override fun setupView(binding: FragmentBookmarkBinding) {
        this.binding = binding

        bookmarkAdapter = BookmarkAdapter()
        binding.apply {
            rvBookmark.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = bookmarkAdapter
            }
            val touchListener = RecyclerTouchListener(requireActivity(), rvBookmark)
            touchListener
                .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                    override fun onRowClicked(position: Int) {
                        val bookmark: Bookmark = bookmarkAdapter.currentList[position]
                        if (listSura.size > 0) {
                            var sura: Sura? = null
                            listSura.forEach lit@{ suraTmp ->
                                if (suraTmp.index == bookmark.index_sura) {
                                    sura = suraTmp
                                    return@lit
                                }
                            }
                            val action =
                                BookmarkFragmentDirections.actionBookmarkFragmentToSuraFragment(
                                    title = bookmark.name_sura,
                                    indexAya = bookmark.index_aya,
                                    sura = sura!!
                                )
                            findNavController().navigate(action)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "data listSura not found",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onIndependentViewClicked(independentViewID: Int, position: Int) {

                    }
                })
                .setSwipeOptionViews(R.id.delete_menu)
                .setSwipeable(
                    R.id.rowFG,
                    R.id.rowBG,
                    object : RecyclerTouchListener.OnSwipeOptionsClickListener {
                        override fun onSwipeOptionClicked(viewID: Int, position: Int) {
                            val bookmark = bookmarkAdapter.currentList[position]
                            when (viewID) {
                                R.id.delete_menu -> {
                                    val builder = AlertDialog.Builder(requireContext())
                                        .setTitle("Delete Confirmation")
                                        .setMessage("Do you really want to delete this bookmark ?")
                                        .setPositiveButton("Delete") { dialog, which ->
                                            dialog?.dismiss()
                                            quranDataViewModel.deleteBookmark(bookmark)
                                        }
                                        .setNegativeButton("Cancel") { dialog, which -> dialog?.dismiss() }
                                        .setCancelable(true)
                                    val alertDialog = builder.create()
                                    alertDialog.show()
                                }
                            }
                        }
                    })
            rvBookmark.addOnItemTouchListener(touchListener)
        }

        quranDataViewModel.apply {

            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                bookmarkListFlow.collect { bookmarks ->
                    bookmarkAdapter.submitList(bookmarks)
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                quranDataFlow.collect { quranData ->
                    val suras: Suras = quranData.suras
                    listSura = suras.sura
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                quranUserEvent.collect { event ->
                    when (event) {
                        is QuranDataViewModel.QuranUserEvent.BookmarkInserted -> {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG)
                                .show()
                        }
                        is QuranDataViewModel.QuranUserEvent.BookmarkUpdated -> {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG)
                                .show()
                        }
                        is QuranDataViewModel.QuranUserEvent.BookmarkDeleted -> {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }

        quranDataViewModel.getQuranData()
    }
}