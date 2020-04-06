package com.agungsptr.moviecatalogue.adapter

import android.content.ContentValues
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.db.DatabaseContract
import com.agungsptr.moviecatalogue.db.TvShowHelper
import com.agungsptr.moviecatalogue.model.TvShow
import com.agungsptr.moviecatalogue.view.tvshow.DetailTvShowActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_cardview.view.*

class CardViewTvShowAdapter : RecyclerView.Adapter<CardViewTvShowAdapter.CardViewViewHolder>() {

    private val mData = java.util.ArrayList<TvShow>()

    fun setData(items: java.util.ArrayList<TvShow>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: TvShow) {
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
        fun bind(tvShow: TvShow) {
            with(itemView) {
                Glide.with(itemView.context)
                    .load(tvShow.poster)
                    .apply(RequestOptions().override(160, 230))
                    .into(img_item_photo)

                tv_item_name.text = tvShow.title
                tv_item_description.text = tvShow.description

                btn_set_favorite.setOnClickListener {
                    val tvShowHelper = TvShowHelper(itemView.context)
                    tvShowHelper.open()

                    val values = ContentValues()
                    values.put(DatabaseContract.MovieColumns.ID, tvShow.id)
                    values.put(DatabaseContract.MovieColumns.TITLE, tvShow.title)
                    values.put(DatabaseContract.MovieColumns.POSTER, tvShow.poster)

                    val result = tvShowHelper.insert(values)
                    if (result > 0) {
                        Toast.makeText(
                            itemView.context,
                            resources.getString(R.string.msg_fav_success, tvShow.title),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            itemView.context,
                            resources.getString(R.string.msg_fav_failed, tvShow.title),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    tvShowHelper.close()
                }

                itemView.setOnClickListener {
                    val moveIntent = Intent(context, DetailTvShowActivity::class.java)
                    moveIntent.putExtra(DetailTvShowActivity.EXTRA_TVSHOW, tvShow.id)
                    startActivity(context, moveIntent, null)
                }
            }
        }
    }
}