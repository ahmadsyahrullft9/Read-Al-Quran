package com.example.readalquran.fragments

import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.readalquran.R
import com.example.readalquran.adapters.SuraAdapter
import com.example.readalquran.custom.BaseFragmentBinding
import com.example.readalquran.databinding.FragmentListSuraBinding
import com.example.readalquran.lib.onQueryTextChanged
import com.example.readalquran.models.Sura
import com.example.readalquran.models.Suras
import com.example.readalquran.viewmodels.QuranDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ListSuraFragment : BaseFragmentBinding<FragmentListSuraBinding>(), SuraAdapter.ItemListener {

    private val TAG = "ListSuraFragment"

    val quranDataViewModel: QuranDataViewModel by viewModels()
    lateinit var suraAdapter: SuraAdapter
    lateinit var binding: FragmentListSuraBinding
    var listSura: List<Sura> = ArrayList()

    private lateinit var searchView: SearchView

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentListSuraBinding
        get() = FragmentListSuraBinding::inflate

    override fun setupView(binding: FragmentListSuraBinding) {
        this.binding = binding

        setHasOptionsMenu(true)

        suraAdapter = SuraAdapter(this, SuraAdapter.NUMERICAL_COMPARATOR)

        binding.apply {
            rvSura.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = suraAdapter
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            quranDataViewModel.quranDataFlow.collect { quranData ->
                val suras: Suras = quranData.suras
                suraAdapter.add(suras.sura)
                listSura = suras.sura
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            quranDataViewModel.userDatastoreFlow.collect { historyReadQuran ->
                binding.apply {
                    cardHistory.setOnClickListener {
                        var sura: Sura? = null
                        listSura.forEach lit@{ suraTmp ->
                            if (suraTmp.index == historyReadQuran.index_sura) {
                                sura = suraTmp
                                return@lit
                            }
                        }
                        val action =
                            ListSuraFragmentDirections.actionListSuraFragmentToSuraFragment(
                                title = historyReadQuran.name_sura,
                                indexAya = historyReadQuran.index_aya,
                                sura = sura!!
                            )
                        findNavController().navigate(action)
                    }
                    txtSuraArabLastRead.text = historyReadQuran.name_sura_arab
                    txtAyatLastRead.text =
                        "QS ${historyReadQuran.name_sura} : ${historyReadQuran.index_aya}"
                    val date = Date(historyReadQuran.time_add)
                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    txtTimeLastRead.text = "Last Read ${simpleDateFormat.format(date)}"
                }
            }
        }

        quranDataViewModel.getQuranData()
    }

    override fun onClickListener(sura: Sura) {
        val action = ListSuraFragmentDirections.actionListSuraFragmentToSuraFragment(
            sura, "Surat ${sura.tname}"
        )
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_sura_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            val listSuraFiltered: List<Sura> = filter(listSura, it)
            suraAdapter.replaceAll(listSuraFiltered)
            binding.rvSura.scrollToPosition(0)
        }
        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                suraAdapter.removeAll()
                suraAdapter.add(listSura)
                binding.rvSura.scrollToPosition(0)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    /*fun sort_list(listSuraTmp: List<Sura>): List<Sura> {
        val listSuraResult: MutableList<Sura> = listSuraTmp as MutableList<Sura>
        for (i in 0..listSuraResult.size) {
            for (j in i + 1..listSuraTmp.size) {
                val sura1 = listSuraTmp.get(i)
                val sura2 = listSuraTmp.get(j)
                if (sura1.index > sura2.index) {
                    listSuraResult[i] = sura2
                    listSuraResult[j] = sura1
                }
            }
        }
        return listSuraResult
    }*/

    fun filter(models: List<Sura>, query: String): List<Sura> {
        val lowerCaseQuery = query.toLowerCase(Locale.getDefault())
        val filteredModelList: MutableList<Sura> = ArrayList()
        for (model in models) {
            val text: String = model.tname.toLowerCase(Locale.getDefault())
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bookmarks -> {
                val acton = ListSuraFragmentDirections.actionListSuraFragmentToBookmarkFragment()
                findNavController().navigate(acton)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}