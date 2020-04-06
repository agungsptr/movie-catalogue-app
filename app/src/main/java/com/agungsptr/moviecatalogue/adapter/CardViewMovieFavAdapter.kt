package com.agungsptr.moviecatalogue.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.agungsptr.moviecatalogue.CustomOnItemClickListener
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.db.MovieHelper
import com.agungsptr.moviecatalogue.model.Movie
import com.agungsptr.moviecatalogue.view.movie.DetailMovieActivity
import com.agungsptr.moviecatalogue.widget.MovieFavoriteWidget
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_fav_cardview.view.*

class CardViewMovieFavAdapter(private val context: Context) :
    RecyclerView.Adapter<CardViewMovieFavAdapter.CardViewViewHolder>() {

    private val mData = ArrayList<Movie>()

    fun setData(items: ArrayList<Movie>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<Movie> {
        return mData
    }

    fun removeItem(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mData.size)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CardViewViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_fav_cardview, viewGroup, false)
        return CardViewViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {

            val movieHelper = MovieHelper(itemView.context)
            movieHelper.open()

            with(itemView) {
                Glide.with(itemView.context)
                    .load(movie.poster)
                    .apply(RequestOptions().override(160, 230))
                    .into(img_item_photo)

                tv_item_name.text = movie.title

                btn_delete.setOnClickListener(CustomOnItemClickListener(
                    adapterPosition,
                    object : CustomOnItemClickListener.OnItemClickCallback {
                        override fun onItemClicked(view: View, position: Int) {
                            val result = movieHelper.deleteById(movie.id.toString()).toLong()
                            val msg: String

                            if (result > 0) {
                                msg = resources.getString(
                                    R.string.msg_delete_fav_success,
                                    movie.title
                                )
                                removeItem(position)
                                sendUpdate()
                            } else {
                                msg = resources.getString(
                                    R.string.msg_delete_fav_failed,
                                    movie.title
                                )
                            }

                            Toast.makeText(
                                context,
                                msg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ))

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