package com.agungsptr.moviecatalogue.view.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.adapter.CardViewTvShowFavAdapter
import com.agungsptr.moviecatalogue.db.TvShowHelper
import com.agungsptr.moviecatalogue.helper.MappingHelper
import com.agungsptr.moviecatalogue.model.TvShow
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favorite_tv_show.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteTvShowFragment : Fragment() {

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var adapter: CardViewTvShowFavAdapter
    private lateinit var tvShowHelper: TvShowHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_tv_show, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvShowHelper = TvShowHelper.getInstance(requireContext())
        tvShowHelper.open()

        showRecyclerCardView()

        if (savedInstanceState == null) {
            loadMovieAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<TvShow>(EXTRA_STATE)
            if (list != null)
                adapter.setData(list)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        tvShowHelper.close()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getData())
    }

    private fun showRecyclerCardView() {
        adapter = CardViewTvShowFavAdapter()
        adapter.notifyDataSetChanged()

        rv_tv_show.layoutManager = LinearLayoutManager(context)
        rv_tv_show.setHasFixedSize(true)
        rv_tv_show.adapter = adapter
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun loadMovieAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            showLoading(true)

            val deferredTvShow = async(Dispatchers.IO) {
                val cursor = tvShowHelper.queryAll()
                MappingHelper.mapTvShowCursorToArrayList(cursor)
            }

            showLoading(false)

            val tvShow = deferredTvShow.await()
            if (tvShow.size > 0) {
                adapter.setData(tvShow)
            } else {
                adapter.setData(ArrayList())
                val message = resources.getString(R.string.no_data_favorite_tvShow)
                Snackbar.make(rv_tv_show, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
