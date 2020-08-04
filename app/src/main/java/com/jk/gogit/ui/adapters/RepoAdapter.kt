package com.jk.gogit.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jk.gogit.R
import com.jk.gogit.model.Repo
import com.jk.gogit.utils.DateUtil
import kotlinx.android.synthetic.main.item_repo.view.*

/**
 *  on 02/08/2017.
 */
class RepoAdapter(val viewActions: OnViewSelectedListener?) : androidx.recyclerview.widget.RecyclerView.Adapter<RepoAdapter.ViewHolder>() {
    private var data = ArrayList<Repo>()

    interface OnViewSelectedListener {
        fun onItemSelected(txtRepo: TextView, owner: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_repo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }


    fun addItems(items: List<Repo>) {
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        data.clear()

    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(item: Repo) = with(itemView) {
            txt_display_name.text = item.fullName
            if (item.language.isNullOrEmpty()) txt_lang.visibility = View.GONE else {
                txt_lang.visibility = View.VISIBLE
                txt_lang.text = item.language
            }
            txt_desc.text = item.description
            txt_repoStars.text = item.stargazersCount.toString()
            txt_repoForks.text = item.forksCount.toString()
            txt_updated_at.text = DateUtil.getDateComparatively(item.updatedAt)
            itemView.setOnClickListener {
                viewActions?.onItemSelected(txt_display_name, item.fullName)
            }
        }
    }
}