package com.agungsptr.moviecatalogue.view.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.adapter.CardViewMovieFavAdapter
import com.agungsptr.moviecatalogue.db.MovieHelper
import com.agungsptr.moviecatalogue.helper.MappingHelper
import com.agungsptr.moviecatalogue.model.Movie
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favorite_movie.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteMovieFragment : Fragment() {

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var adapter: CardViewMovieFavAdapter
    private lateinit var movieHelper: MovieHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieHelper = MovieHelper.getInstance(requireContext())
        movieHelper.open()

        showRecyclerCardView()

        if (savedInstanceState == null) {
            loadMovieAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Movie>(EXTRA_STATE)
            if (list != null)
                adapter.setData(list)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        movieHelper.close()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getData())
    }

    private fun showRecyclerCardView() {
        adapter = CardViewMovieFavAdapter(requireContext())
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

            val deferredMovie = async(Dispatchers.IO) {
                val cursor = movieHelper.queryAll()
                MappingHelper.mapMovieCursorToArrayList(cursor)
            }

            showLoading(false)

            val movie = deferredMovie.await()
            if (movie.size > 0) {
                adapter.setData(movie)
            } else {
                adapter.setData(ArrayList())
                val message = resources.getString(R.string.no_data_favorite_movie)
                Snackbar.make(rv_movies, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
