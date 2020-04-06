package com.agungsptr.moviecatalogue.view.movie

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.model.Movie
import com.agungsptr.moviecatalogue.viewmodel.DetailMovieViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_detail_movie.*

class DetailMovieActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MOVIE = "extra_movie"
    }

    private lateinit var detailMovieViewModel: DetailMovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_movie)

        supportActionBar?.title = resources.getString(R.string.title_bar_movie)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val movieId = intent.getIntExtra(EXTRA_MOVIE, 0)

        detailMovieViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(DetailMovieViewModel::class.java)
        showLoading(true)

        detailMovieViewModel.setDetailMovie(movieId)
        detailMovieViewModel.getDetailMovie().observe(
            this, Observer { detailItem ->
                if (detailItem != null) {
                    setDetail(detailItem)
                    showLoading(false)
                }
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setDetail(movie: Movie) {
        txt_title_detail.text = movie.title
        txt_score.text = movie.score
        txt_year.text = movie.year
        txt_description_detail.text = movie.description
        Glide.with(this)
            .load(movie.poster)
            .apply(RequestOptions().override(160, 240))
            .into(img_poster_detail)
        Glide.with(this)
            .load(movie.backdrop)
            .apply(RequestOptions())
            .into(img_backdrop_detail)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}