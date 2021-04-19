package com.example.readalquran.fragments

import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.readalquran.R
import com.example.readalquran.adapters.AyatAdapter
import com.example.readalquran.custom.BaseFragmentBinding
import com.example.readalquran.custom.RecyclerTouchListener
import com.example.readalquran.databinding.FragmentSuraBinding
import com.example.readalquran.datastores.HistoryReadQuran
import com.example.readalquran.dialogs.GotoAyatDialog
import com.example.readalquran.models.Ayat
import com.example.readalquran.models.Bookmark
import com.example.readalquran.models.Sura
import com.example.readalquran.viewmodels.QuranDataViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class SuraFragment : BaseFragmentBinding<FragmentSuraBinding>(),
    GotoAyatDialog.GotoAyatDialogListener {
    private val TAG = "SuraFragment"

    val quranDataViewModel: QuranDataViewModel by viewModels()
    lateinit var ayatAdapter: AyatAdapter
    val args: SuraFragmentArgs by navArgs()
    lateinit var binding: FragmentSuraBinding
    lateinit var sura: Sura
    var indexAya: Int = 1
    val gotoAyatDialog = GotoAyatDialog(this)

    lateinit var smoothScroller: LinearSmoothScroller
    var visiblePosition = 1

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSuraBinding
        get() = FragmentSuraBinding::inflate

    override fun setupView(binding: FragmentSuraBinding) {
        this.binding = binding
        setHasOptionsMenu(true)
        sura = args.sura
        indexAya = args.indexAya
        visiblePosition = indexAya

        ayatAdapter = AyatAdapter()
        smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        binding.apply {
            rvAyat.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = ayatAdapter
            }
            rvAyat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager: LinearLayoutManager =
                        recyclerView.getLayoutManager() as LinearLayoutManager
                    visiblePosition = layoutManager.findLastVisibleItemPosition() + 1
                }
            })

            val touchListener = RecyclerTouchListener(activity, rvAyat)
            touchListener
                .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                    override fun onRowClicked(position: Int) {

                    }

                    override fun onIndependentViewClicked(independentViewID: Int, position: Int) {

                    }
                })
                .setSwipeOptionViews(R.id.bookmark_menu)
                .setSwipeable(
                    R.id.rowFG,
                    R.id.rowBG,
                    object : RecyclerTouchListener.OnSwipeOptionsClickListener {
                        override fun onSwipeOptionClicked(viewID: Int, position: Int) {
                            val ayat: Ayat = ayatAdapter.currentList!![position]!!
                            when (viewID) {
                                R.id.bookmark_menu -> {
                                    val bookmark = Bookmark(
                                        index_sura = sura.index,
                                        name_sura = sura.tname,
                                        index_aya = ayat.aya
                                    )
                                    quranDataViewModel.insertBookmark(bookmark)
                                }
                            }
                        }
                    })
            rvAyat.addOnItemTouchListener(touchListener)
        }

        quranDataViewModel.getAyatPagedList(sura.index, indexAya)
        observeAyatPagedList()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            quranDataViewModel.quranUserEvent.collect { event ->
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
        // This callback will only be called when MyFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            val historyReadQuran = HistoryReadQuran(
                sura.index,
                sura.tname,
                sura.name,
                visiblePosition,
                System.currentTimeMillis()
            )
            quranDataViewModel.updateHistoryReadQuran(historyReadQuran)
            findNavController().popBackStack(R.id.listSuraFragment, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sura_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_subject -> {
                if (gotoAyatDialog.isVisible) gotoAyatDialog.dismiss()
                gotoAyatDialog.show(childFragmentManager, gotoAyatDialog.TAG)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun gotoAyat(numberAyat: Int) {
        if (numberAyat > sura.ayas) {
            Snackbar.make(requireView(), "number ayat is not available", Snackbar.LENGTH_LONG)
                .setAction("change") {
                    if (gotoAyatDialog.isVisible) gotoAyatDialog.dismiss()
                    gotoAyatDialog.show(childFragmentManager, gotoAyatDialog.TAG)
                }.show()
        } else {
            indexAya = numberAyat
            Log.d(TAG, "gotoAyat: ${numberAyat}")
            if (numberAyat > ayatAdapter.itemCount) {
                quranDataViewModel.getAyatPagedList(sura.index, numberAyat)
                observeAyatPagedList()
            } else {
                scrollTo(indexAya)
            }
        }
    }

    private fun observeAyatPagedList() {
        quranDataViewModel._ayatPagedList.observe(this, { ayatPageList ->
            ayatAdapter.submitList(ayatPageList)
            scrollTo(indexAya)
        })
    }

    fun scrollTo(index: Int) {
        smoothScroller.targetPosition = index - 1
        binding.rvAyat.layoutManager?.startSmoothScroll(smoothScroller)
    }
}