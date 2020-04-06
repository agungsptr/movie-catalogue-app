package com.agungsptr.moviecatalogue.view.favorite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.adapter.FavoriteSectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_favorite.*

class FavoriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        val sectionsPagerAdapter = FavoriteSectionsPagerAdapter(
            this,
            supportFragmentManager
        )

        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = resources.getString(R.string.favorite)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
