package com.jk.gogit.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jk.gogit.R
import com.jk.gogit.model.Org
import kotlinx.android.synthetic.main.item_repo.view.*

/**
 *  on 02/08/2017.
 */
class OrgAdapter(val viewActions: OnViewSelectedListener?) : androidx.recyclerview.widget.RecyclerView.Adapter<OrgAdapter.ViewHolder>() {
    private var datas = ArrayList<Org>()

    interface OnViewSelectedListener {
        fun onItemSelected(txtRepo: TextView, id: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_repo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }


    fun addItems(items: List<Org>) {
        datas.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        datas.clear()

    }

    override fun getItemCount(): Int {
        return datas.size
    }


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(item: Org) = with(itemView) {
            txt_display_name.text = item.login
            //txt_lang.text = item.language
            txt_desc.text = item.description
            txt_repoForks.visibility=View.GONE
            txt_repoStars.visibility=View.GONE
            //txt_repoStars.text = item.stargazersCount.toString()
            //txt_repoForks.text = item.forksCount.toString()
            itemView.setOnClickListener {
                viewActions?.onItemSelected(txt_display_name, item.login!!)
            }
        }
    }
}