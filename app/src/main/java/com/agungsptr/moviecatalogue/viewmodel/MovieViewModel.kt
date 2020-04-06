package com.agungsptr.moviecatalogue.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agungsptr.moviecatalogue.model.Movie
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MovieViewModel : ViewModel() {
    companion object {
        private const val API_KEY = "eafd9e49c70f2f35274ec7c65d9a8816"
        const val SEARCH = 100
        const val LIST = 101
        const val RELEASE_TODAY = 102
    }

    private val listMovie = MutableLiveData<ArrayList<Movie>>()

    fun setMovie(type: Int, query: String = "") {
        var url: String? = null

        when (type) {
            LIST -> url =
                "https://api.themoviedb.org/3/discover/movie?api_key=$API_KEY&language=en-US"
            SEARCH -> url =
                "https://api.themoviedb.org/3/search/movie?api_key=$API_KEY&language=en-US&query=\"$query\""
            RELEASE_TODAY -> url =
                "https://api.themoviedb.org/3/discover/movie?api_key=$API_KEY&primary_release_date.gte=${currentDate()}&primary_release_date.lte=${currentDate()}"
        }

        val listItems = ArrayList<Movie>()

        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try {
                    //parsing json
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("results")

                    for (i in 0 until list.length()) {
                        val movieItem = list.getJSONObject(i)
                        val movie = Movie()
                        val poster = movieItem.getString("poster_path")

                        movie.id = movieItem.getInt("id")
                        movie.title = movieItem.getString("title")
                        movie.poster = "https://image.tmdb.org/t/p/w154/$poster"
                        movie.description = movieItem.getString("overview")

                        listItems.add(movie)
                    }
                    listMovie.postValue(listItems)
                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }

    internal fun getMovie(): LiveData<ArrayList<Movie>> {
        return listMovie
    }

    private fun currentDate(): String {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return df.format(date)
    }
}