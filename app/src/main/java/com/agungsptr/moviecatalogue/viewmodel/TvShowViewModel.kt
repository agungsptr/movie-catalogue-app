package com.agungsptr.moviecatalogue.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agungsptr.moviecatalogue.model.TvShow
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.util.*

class TvShowViewModel : ViewModel() {

    companion object {
        private const val API_KEY = "eafd9e49c70f2f35274ec7c65d9a8816"
        const val SEARCH = 100
        const val LIST = 101
    }

    private val listTvShow = MutableLiveData<ArrayList<TvShow>>()

    fun setTvShow(type: Int, query: String = "") {
        var url: String? = null

        when (type) {
            LIST -> url = "https://api.themoviedb.org/3/discover/tv?api_key=$API_KEY&language=en-US"
            SEARCH -> url =
                "https://api.themoviedb.org/3/search/tv?api_key=$API_KEY&language=en-US&query=$query"
        }

        val listItems = ArrayList<TvShow>()

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
                        val tvShowItem = list.getJSONObject(i)
                        val tvShow = TvShow()

                        tvShow.id = tvShowItem.getInt("id")
                        tvShow.title = tvShowItem.getString("name")
                        tvShow.poster =
                            "https://image.tmdb.org/t/p/w154/" + tvShowItem.getString("poster_path")
                        tvShow.description = tvShowItem.getString("overview")

                        listItems.add(tvShow)
                    }
                    listTvShow.postValue(listItems)
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

    internal fun getMovie(): LiveData<ArrayList<TvShow>> {
        return listTvShow
    }
}