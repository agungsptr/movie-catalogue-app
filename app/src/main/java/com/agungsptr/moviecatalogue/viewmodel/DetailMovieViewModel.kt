package com.agungsptr.moviecatalogue.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agungsptr.moviecatalogue.model.Movie
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DetailMovieViewModel : ViewModel() {

    private val detailMovie = MutableLiveData<Movie>()

    fun setDetailMovie(id: Int) {
        val url =
            "https://api.themoviedb.org/3/movie/$id?api_key=eafd9e49c70f2f35274ec7c65d9a8816&language=en-US"

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
                    val detail = Movie()

                    detail.title = responseObject.getString("original_title")
                    detail.description = responseObject.getString("overview")
                    detail.poster =
                        "https://image.tmdb.org/t/p/w185/" + responseObject.getString("poster_path")
                    detail.year = responseObject.getString("release_date")
                    detail.score = responseObject.getInt("vote_average").toString() + "0%"
                    detail.backdrop =
                        "https://image.tmdb.org/t/p/w342/" + responseObject.getString("backdrop_path")

                    detailMovie.postValue(detail)
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

    internal fun getDetailMovie(): MutableLiveData<Movie> {
        return detailMovie
    }
}