package com.agungsptr.moviecatalogue.view.releasetoday

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
import com.agungsptr.moviecatalogue.adapter.CardViewMovieAdapter
import com.agungsptr.moviecatalogue.setting.SettingActivity
import com.agungsptr.moviecatalogue.view.favorite.FavoriteActivity
import com.agungsptr.moviecatalogue.viewmodel.MovieViewModel
import kotlinx.android.synthetic.main.fragment_movie_release_today.*

/**
 * A simple [Fragment] subclass.
 */
class ReleaseTodayFragment : Fragment() {

    private lateinit var adapter: CardViewMovieAdapter
    private lateinit var movieViewModel: MovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_movie_release_today, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showRecyclerCardView()

        movieViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MovieViewModel::class.java)
        showLoading(true)
        movieViewModel.setMovie(MovieViewModel.RELEASE_TODAY)

        movieViewModel.getMovie().observe(
            viewLifecycleOwner, Observer { movieItems ->
                if (movieItems != null) {
                    adapter.setData(movieItems)
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
                showLoading(true)
                movieViewModel.setMovie(MovieViewModel.SEARCH, query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                showLoading(true)
                if (query.isEmpty()) {
                    movieViewModel.setMovie(MovieViewModel.RELEASE_TODAY)
                } else {
                    movieViewModel.setMovie(MovieViewModel.SEARCH, query)
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
        adapter = CardViewMovieAdapter(requireContext())
        adapter.notifyDataSetChanged()

        rv_releasetoday.layoutManager = LinearLayoutManager(context)
        rv_releasetoday.adapter = adapter
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}
