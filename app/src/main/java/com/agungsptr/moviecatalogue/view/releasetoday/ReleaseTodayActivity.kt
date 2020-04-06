package com.agungsptr.moviecatalogue.view.releasetoday

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agungsptr.moviecatalogue.MainActivity
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.adapter.CardViewMovieAdapter
import com.agungsptr.moviecatalogue.viewmodel.MovieViewModel
import kotlinx.android.synthetic.main.activity_movie_release_today.*

class ReleaseTodayActivity : AppCompatActivity() {

    private lateinit var adapter: CardViewMovieAdapter
    private lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_release_today)

        supportActionBar?.title = resources.getString(R.string.release_today)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showRecyclerCardView()

        movieViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MovieViewModel::class.java)
        showLoading(true)
        movieViewModel.setMovie(MovieViewModel.RELEASE_TODAY)

        movieViewModel.getMovie().observe(
            this, Observer { movieItems ->
                if (movieItems != null) {
                    adapter.setData(movieItems)
                    showLoading(false)
                }
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        return true
    }

    private fun showRecyclerCardView() {
        adapter = CardViewMovieAdapter(this)
        adapter.notifyDataSetChanged()

        rv_movies.layoutManager = LinearLayoutManager(this)
        rv_movies.adapter = adapter
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}
