package com.agungsptr.moviecatalogue.view.tvshow


import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.adapter.CardViewTvShowAdapter
import com.agungsptr.moviecatalogue.setting.SettingActivity
import com.agungsptr.moviecatalogue.view.favorite.FavoriteActivity
import com.agungsptr.moviecatalogue.viewmodel.TvShowViewModel
import kotlinx.android.synthetic.main.fragment_tv_show.*

/**
 * A simple [Fragment] subclass.
 */
class TvShowFragment : Fragment() {

    private lateinit var adapter: CardViewTvShowAdapter
    private lateinit var tvShowViewModel: TvShowViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_tv_show, container, false)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showRecyclerCardView()

        tvShowViewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory()
        ).get(TvShowViewModel::class.java)
        showLoading(true)
        tvShowViewModel.setTvShow(TvShowViewModel.LIST)

        tvShowViewModel.getMovie().observe(
            this, Observer { tvShowItems ->
                if (tvShowItems != null) {
                    adapter.setData(tvShowItems)
                    showLoading(false)
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val searchManager = requireActivity()
            .getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                tvShowViewModel.setTvShow(TvShowViewModel.SEARCH, query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (query.isEmpty()) {
                    tvShowViewModel.setTvShow(TvShowViewModel.LIST)
                } else {
                    tvShowViewModel.setTvShow(TvShowViewModel.SEARCH, query)
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> {
                val mIntent = Intent(context, FavoriteActivity::class.java)
                startActivity(mIntent)
            }
            R.id.menu_setting -> {
                val mIntent = Intent(context, SettingActivity::class.java)
                startActivity(mIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showRecyclerCardView() {
        adapter = CardViewTvShowAdapter()
        adapter.notifyDataSetChanged()

        rv_tv_show.layoutManager = LinearLayoutManager(context)
        rv_tv_show.adapter = adapter
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}
