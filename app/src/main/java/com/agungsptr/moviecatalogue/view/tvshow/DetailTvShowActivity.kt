package com.agungsptr.moviecatalogue.view.tvshow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.model.TvShow
import com.agungsptr.moviecatalogue.viewmodel.DetailTvShowViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_detail_tv_show.*

class DetailTvShowActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TVSHOW = "extra_movie"
    }

    private lateinit var detailTvShowViewModel: DetailTvShowViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tv_show)

        supportActionBar?.title = resources.getString(R.string.title_bar_tv)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tvShowId = intent.getIntExtra(EXTRA_TVSHOW, 0)

        detailTvShowViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(DetailTvShowViewModel::class.java)
        showLoading(true)

        detailTvShowViewModel.setDetaiTvShow(tvShowId)
        detailTvShowViewModel.getDetailTvShow().observe(
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

    private fun setDetail(tvShow: TvShow) {
        txt_title_detail.text = tvShow.title
        txt_score.text = tvShow.score
        txt_year.text = tvShow.year
        txt_description_detail.text = tvShow.description
        Glide.with(this)
            .load(tvShow.poster)
            .apply(RequestOptions().override(160, 240))
            .into(img_poster_detail)
        Glide.with(this)
            .load(tvShow.backdrop)
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