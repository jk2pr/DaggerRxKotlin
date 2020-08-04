package com.jk.gogit.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jk.gogit.R
import com.jk.gogit.extensions.loading
import com.jk.gogit.model.search.User
import kotlinx.android.synthetic.main.item_search.view.*

/**
 *  on 02/08/2017.
 */
class DataAdapter(val viewActions: onViewSelectedListener?) : androidx.recyclerview.widget.RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    private var datas = ArrayList<User>()

    interface onViewSelectedListener {
        fun onItemSelected(user: User?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val view = inflator.inflate(R.layout.item_search, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }


    fun addItems(items: List<User>){
        datas.addAll(items)
        notifyDataSetChanged()
    }
    fun clearItems(){
        datas.clear()

    }
    override fun getItemCount(): Int {
        return datas.size
    }


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(item: User) = with(itemView) {
            img_thumbnail.loading(item.avatar_url)
            description.text = item.html_url
            author.text = item.login
            //comments.text = """${12} comments"""
            txt_time.text = item.type

            itemView.setOnClickListener {
                viewActions?.onItemSelected(item)
            }
        }
    }
}