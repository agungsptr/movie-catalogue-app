package com.agungsptr.moviecatalogue.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.agungsptr.moviecatalogue.CustomOnItemClickListener
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.db.TvShowHelper
import com.agungsptr.moviecatalogue.model.TvShow
import com.agungsptr.moviecatalogue.view.tvshow.DetailTvShowActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_fav_cardview.view.*

class CardViewTvShowFavAdapter :
    RecyclerView.Adapter<CardViewTvShowFavAdapter.CardViewViewHolder>() {

    private val mData = ArrayList<TvShow>()

    fun setData(items: ArrayList<TvShow>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<TvShow> {
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
        fun bind(tvShow: TvShow) {

            val tvShowHelper = TvShowHelper(itemView.context)
            tvShowHelper.open()

            with(itemView) {
                Glide.with(itemView.context)
                    .load(tvShow.poster)
                    .apply(RequestOptions().override(160, 230))
                    .into(img_item_photo)

                tv_item_name.text = tvShow.title

                btn_delete.setOnClickListener(CustomOnItemClickListener(
                    adapterPosition,
                    object : CustomOnItemClickListener.OnItemClickCallback {
                        override fun onItemClicked(view: View, position: Int) {
                            val result = tvShowHelper.deleteById(tvShow.id.toString()).toLong()
                            val msg: String

                            if (result > 0) {
                                msg = resources.getString(
                                    R.string.msg_delete_fav_success,
                                    tvShow.title
                                )
                                removeItem(position)
                            } else {
                                msg = resources.getString(
                                    R.string.msg_delete_fav_failed,
                                    tvShow.title
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
                    val moveIntent = Intent(context, DetailTvShowActivity::class.java)
                    moveIntent.putExtra(DetailTvShowActivity.EXTRA_TVSHOW, tvShow.id)
                    startActivity(context, moveIntent, null)
                }
            }
        }
    }
}