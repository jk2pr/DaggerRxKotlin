package com.jk.gogit.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.jk.gogit.R
import com.jk.gogit.databinding.ItemFeedBinding
import com.jk.gogit.model.Feed
import com.jk.gogit.utils.DateUtil
import java.util.*


class FeedAdapter constructor(
        private var requestManager: RequestManager,
        val viewActions: OnViewSelectedListener) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil
                .inflate<ItemFeedBinding>(
                        LayoutInflater.from(viewGroup.context),
                        R.layout.item_feed,
                        viewGroup,
                        false
                )

        return ViewHolder(binding)

    }


    private var datas = ArrayList<Feed>()

    interface OnViewSelectedListener {
        fun onActorImageClick(imageView: View, loginId: String)
        fun onFeedItemClick(textView: View, owner: String)
    }


    fun addItems(items: List<Feed>) {
        datas.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        datas.clear()

    }

    override fun getItemCount(): Int {
        return datas.size
    }


    inner class ViewHolder(private val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Feed) = with(binding) {

            feed = item
            itemClickListener = viewActions
            requestManager = this@FeedAdapter.requestManager
            val charSequence = DateUtil.getDateComparatively(item.created_at)
            executePendingBindings()
            /* txt_time.text = charSequence
             img_actor.setOnClickListener {
                 viewActions.onActorNameClicked(img_actor, item.actor.login)
             }

             //txt_activity.movementMethod = LinkMovementMethod.getInstance()

             itemView.setOnClickListener {
                 viewActions.onRepNameClicked(txt_activity, item.repo.name)
             }*/

        }

    }


    internal companion object {

        private fun getEventNameByType(eventName: String): String {
            return when (eventName) {
                "CreateEvent" ->
                    "created a Repository"
                "ForkEvent" ->
                    "forked a Repository"
                "WatchEvent" ->
                    "started following"
                "PushEvent" ->
                    "pushed to"
                "PullRequestEvent" ->
                    "Closed pull request"
                "PublicEvent" ->
                    "Made public"
                else -> {
                    eventName
                }
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["feed"])
        fun setActivity(view: TextView, item: Feed) {
            val actorName = item.actor.display_login
            val repoName = item.repo.name
            val eventName = getEventNameByType(item.type)
            val longText = "$actorName $eventName $repoName"
            view.text = longText
        }

        @JvmStatic
        @BindingAdapter(value = ["requestManager", "profileImage"])
        fun loadImage(view: ImageView, requestManager: RequestManager, url: String) {
            requestManager
                    .load(url)
                    .into(view)
        }

    }
}