package com.agungsptr.moviefavorite.view

import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.agungsptr.moviefavorite.R
import com.agungsptr.moviefavorite.adapter.CardViewTvShowFavAdapter
import com.agungsptr.moviefavorite.db.DatabaseContract
import com.agungsptr.moviefavorite.helper.MappingHelper
import com.agungsptr.moviefavorite.model.TvShow
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favorite_tv_show.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class FavoriteTvShowFragment : Fragment() {

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var adapter: CardViewTvShowFavAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_tv_show, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadMovieAsync()
            }
        }

        requireActivity().contentResolver.registerContentObserver(
            DatabaseContract.TvShowColumns.CONTENT_URI,
            true,
            myObserver
        )

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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getData())
    }

    private fun showRecyclerCardView() {
        adapter = CardViewTvShowFavAdapter()
        adapter.notifyDataSetChanged()

        rv_movies.layoutManager = LinearLayoutManager(context)
        rv_movies.setHasFixedSize(true)
        rv_movies.adapter = adapter
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

            val deferredTvshow = async(Dispatchers.IO) {
                val cursor = requireActivity().contentResolver.query(
                    DatabaseContract.TvShowColumns.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
                MappingHelper.mapTvShowCursorToArrayList(cursor)
            }

            showLoading(false)

            val tvShow = deferredTvshow.await()
            if (tvShow.size > 0) {
                adapter.setData(tvShow)
            } else {
                adapter.setData(ArrayList())
                val message = resources.getString(R.string.no_data_favorite_tvshow)
                Snackbar.make(rv_movies, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
