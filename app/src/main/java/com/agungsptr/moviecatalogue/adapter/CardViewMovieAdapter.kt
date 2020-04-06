package com.agungsptr.moviecatalogue.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.db.DatabaseContract
import com.agungsptr.moviecatalogue.db.MovieHelper
import com.agungsptr.moviecatalogue.model.Movie
import com.agungsptr.moviecatalogue.view.movie.DetailMovieActivity
import com.agungsptr.moviecatalogue.widget.MovieFavoriteWidget
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_cardview.view.*

class CardViewMovieAdapter(private val context: Context) :
    RecyclerView.Adapter<CardViewMovieAdapter.CardViewViewHolder>() {

    private val mData = ArrayList<Movie>()

    fun setData(items: ArrayList<Movie>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<Movie> {
        return mData
    }

    fun addItem(item: Movie) {
        mData.add(item)
        notifyDataSetChanged()
    }

    fun clearData() {
        mData.clear()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CardViewViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_cardview, viewGroup, false)
        return CardViewViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            with(itemView) {
                Glide.with(itemView.context)
                    .load(movie.poster)
                    .apply(RequestOptions().override(160, 230))
                    .into(img_item_photo)

                tv_item_name.text = movie.title
                tv_item_description.text = movie.description

                btn_set_favorite.setOnClickListener {
                    val movieHelper = MovieHelper(itemView.context)
                    movieHelper.open()

                    val values = ContentValues()
                    values.put(DatabaseContract.MovieColumns.ID, movie.id)
                    values.put(DatabaseContract.MovieColumns.TITLE, movie.title)
                    values.put(DatabaseContract.MovieColumns.POSTER, movie.poster)

                    val result = movieHelper.insert(values)
                    if (result > 0) {
                        Toast.makeText(
                            itemView.context,
                            resources.getString(R.string.msg_fav_success, movie.title),
                            Toast.LENGTH_SHORT
                        ).show()
                        sendUpdate()
                    } else {
                        Toast.makeText(
                            itemView.context,
                            resources.getString(R.string.msg_fav_failed, movie.title),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    movieHelper.close()
                }

                itemView.setOnClickListener {
                    val moveIntent = Intent(context, DetailMovieActivity::class.java)
                    moveIntent.putExtra(DetailMovieActivity.EXTRA_MOVIE, movie.id)
                    startActivity(context, moveIntent, null)
                }
            }
        }
    }

    fun sendUpdate() {
        val intent = Intent(context, MovieFavoriteWidget::class.java)
        intent.action = MovieFavoriteWidget.WIDGET_UPDATE
        context.sendBroadcast(intent)
    }
}