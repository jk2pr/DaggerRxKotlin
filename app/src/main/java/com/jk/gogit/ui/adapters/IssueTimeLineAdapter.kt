package com.jk.gogit.ui.adapters

import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.jk.gogit.R
import com.jk.gogit.extensions.loading
import com.jk.gogit.model.TimeLine
import com.jk.gogit.spans.IssueLabelSpan
import com.jk.gogit.ui.Type
import com.jk.gogit.utils.DateUtil
import kotlinx.android.synthetic.main.layout_item_event.view.*
import kotlinx.android.synthetic.main.layout_item_head_comments.view.*
import org.jetbrains.anko.displayMetrics

/**
 *  on 02/08/2017.
 */
class IssueTimeLineAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    private var datas = ArrayList<TimeLine>()
    private val TYPE_HEAD_COMMENT = 1
    private val TYPE_COMMENT = 2
    private val TYPE_EVENT = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder by lazy {
            when (viewType) {
                TYPE_HEAD_COMMENT -> {
                    val view = inflater.inflate(R.layout.layout_item_head_comments, parent, false)
                    HeadCommentViewHolder(view)
                }
                TYPE_COMMENT -> {
                    //val view = inflater.inflate(R.layout.layout_item_comments, parent, false)
                    val view = inflater.inflate(R.layout.layout_item_head_comments, parent, false)
                    CommentViewHolder(view)
                }
                TYPE_EVENT -> {
                    val view = inflater.inflate(R.layout.layout_item_event, parent, false)
                    EventViewHolder(view)
                }
                else -> {
                    val view = inflater.inflate(R.layout.layout_item_event, parent, false)
                    EventViewHolder(view)
                }
            }


        }
        return viewHolder
    }


    override fun getItemViewType(position: Int): Int {
        if (datas.get(position).parentIssue != null) {
            return TYPE_HEAD_COMMENT
        } else
            when (datas.get(position).event) {
                Type.commented -> return TYPE_COMMENT
                else -> return TYPE_EVENT
            }
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_HEAD_COMMENT -> {
                (holder as HeadCommentViewHolder).bind(datas[position])
            }
            TYPE_COMMENT -> {
                (holder as CommentViewHolder).bind(datas[position])
            }
            TYPE_EVENT -> {
                (holder as EventViewHolder).bind(datas[position])
            }
        }
    }


    fun addItems(items: List<TimeLine>) {
        datas.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        datas.clear()

    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class HeadCommentViewHolder(itemView: View) : CommentViewHolder(itemView) {
        override fun bind(item: TimeLine) = with(itemView) {
            super.bind(item)

        }
    }


    open inner class CommentViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        open fun bind(item: TimeLine) = with(itemView) {
            itemView.user_avatar.loading(item.actor?.avatarUrl!!)
            //itemView.user_name.text = item.actor?.login
            val time = DateUtil.getDateComparatively(item.createdAt!!)

            itemView.user_name.text = String.format(resources.getString(R.string.issue_commented), item.actor?.login, time)
            val body: String?
            if (item.body.isNullOrBlank())
                body = "No description provided."
            else
                body = item.body
            itemView.comment_desc.text = body
            /* Codeview.with(this.context)
                     .withCode(body)
                     .setAutoWrap(true)
                     .into(itemView.comment_desc, "baseUrl")*/
            //itemView.comment_desc.text = Html.fromHtml(item.body)
            //  else
            //  itemView.comment_desc.text = resources.getString(R.string.no_description_provided)


            /* itemView.txt_comment.settings.apply {
                 loadWithOverviewMode = true
                 javaScriptEnabled = true
                 layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                 setAppCachePath(itemView.context.cacheDir.path)
                 setAppCacheEnabled(true)
                 cacheMode = WebSettings.LOAD_NO_CACHE
                 loadWithOverviewMode = true
                 useWideViewPort = true
                 setSupportZoom(true)
                 builtInZoomControls = true
                 displayZoomControls = false
             }
             val ava = item.actor?.avatarUrl
             if (ava != null) {
                 itemView.img_actor?.loading(ava)
             }

             val t = if (!item.updatedAt.isNullOrBlank()) {
                 item.updatedAt
             } else if (!item.createdAt.isNullOrBlank()) {
                 item.createdAt
             } else
                 item.submittedAt

             if (t.isNullOrBlank()) {
                 //event is Commited
                 itemView.txt_issue_commentor.text = item.message
             } else {
                 val time = DateUtil.getDateComparatively(t!!)
                 itemView.txt_issue_commentor.text = String.format(resources.getString(R.string.issue_commented), item.actor?.login, time)
             }
             if (item.body.isNullOrBlank())
                 itemView.txt_comment.visibility = View.GONE
             else {
                 val t = Markdown4jProcessor().process(item.body)
                 itemView.txt_comment.loadDataWithBaseURL("", t, "text/html", "UTF-8", "")
             }
         */
        }
    }

    inner class EventViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(item: TimeLine) = with(itemView) {
            if (item.actor != null)
                itemView.img_actor?.loading(item.actor?.avatarUrl!!)
            setEventIcon(item)
            setDesc(item)
            setMargin(item, datas.indexOf(item))
        }

        fun setMargin(data: TimeLine, position: Int) {

            if (data.event != null) {
                var topMargin = 0
                var bottomMargin = 0
                if (position - 1 > 0 && data.event!!.equals(Type.commented)) {
                    topMargin = itemView.context.resources.getDimensionPixelSize(R.dimen.dp5)
                }
                if (position + 1 < datas.size && data.event!!.equals(Type.commented)) {
                    bottomMargin = itemView.context.getResources().getDimensionPixelSize(R.dimen.dp5)
                }
                if (data.event!!.equals(Type.closed)) {
                    bottomMargin = itemView.context.getResources().getDimensionPixelSize(R.dimen.dp5)
                }

                val layoutParams = itemView.root_lay.getLayoutParams() as androidx.recyclerview.widget.RecyclerView.LayoutParams
                layoutParams.setMargins(0, topMargin, 0, bottomMargin)
                itemView.root_lay.setLayoutParams(layoutParams)
            }
        }

        private fun setDesc(model: TimeLine) {
            val user = if (model.actor != null)
                model.actor?.login else
                if (model.author != null)
                    model.author?.name
                else {
                    model.user?.login
                }
            val text = SpannableStringBuilder(user)
            text.append(" ")
            val info: String
            val resources = itemView.context
            when (model.event) {
            /* Type.committed -> {
                    val s = String.format(resources.getString(R.string.issue_commited),
                            " #" + model.message)
                    text.append(s)
                }*/
                Type.reopened -> text.append(resources.getString(R.string.issue_reopened))
                Type.closed -> text.append(resources.getString(R.string.issue_close))
                Type.renamed -> text.append(resources.getString(R.string.issue_modified))
                Type.locked -> text.append(resources.getString(R.string.issue_locked_conversation))
                Type.unlocked -> text.append(resources.getString(R.string.issue_unlocked_conversation))
            /* Type.referenced -> {
                    info = String.format(resources.getString(R.string.issue_referenced),
                            " #" + model.commitId?.run { substring(0, 6) })
                    text.append(info)
                }
                Type.merged -> {
                    info = String.format(resources.getString(R.string.issue_merged),
                            " #" + model.commitId?.run { substring(0, 6) }, "master")
                    text.append(info)
                }Type.head_ref_deleted -> {
                    info = String.format(resources.getString(R.string.issue_head_ref_deleted))
                    text.append(info)
                }*/
            /*
            Type.assigned -> {
                info = String.format(resources.getString(R.string.issue_assigned), model.getAssignee().getLogin())
                text.append(info)
            }*/
                Type.unassigned -> text.append(resources.getString(R.string.issue_unassigned))
            /* Type.milestoned -> {
                 info = String.format(resources.getString(R.string.issue_added_to_milestone),
                         model.getMilestone().getTitle())
                 text.append(info)
             }
             Type.demilestoned -> {
                 info = String.format(resources.getString(R.string.issue_removed_from_milestone),
                         model.getMilestone().getTitle())
                 text.append(info)
             }*/
                Type.commentDeleted -> text.append(resources.getString(R.string.issue_delete_comment))
                Type.labeled -> {
                    info = String.format(resources.getString(R.string.issue_add_label), "[label]")
                    text.append(info)
                }
                Type.unlabeled -> {
                    info = String.format(resources.getString(R.string.issue_remove_label), "[label]")
                    text.append(info)
                }
                else -> {
                }
            }

            //eventIcon.setBackgroundColor(context.getResources().getColor(R.color.transparent));

            val labelPos = text.toString().indexOf("[label]")
            val label = model.label
            if (label != null && labelPos >= 0) {
                text.replace(labelPos, labelPos + 7, label.name)
                text.setSpan(IssueLabelSpan(itemView.context, label),
                        labelPos, labelPos + label.name.length, 0)
            }

            val timeStr = DateUtil.getDateComparatively(model.createdAt!!)
            text.append(" ").append(timeStr)
            itemView.desc.text = text
        }

        private fun setEventIcon(model: TimeLine) {
            itemView.apply {
                when (model.event) {
                    Type.reopened ->
                        eventIcon.setImageResource(R.drawable.ic_dot)

                    Type.closed ->
                        eventIcon.setImageResource(R.drawable.ic_block)

                    Type.renamed ->
                        eventIcon.setImageResource(R.drawable.ic_edit)

                    Type.locked ->
                        eventIcon.setImageResource(R.drawable.ic_lock)

                    Type.unlocked ->
                        eventIcon.setImageResource(R.drawable.ic_unlock)

                    Type.crossReferenced ->
                        eventIcon.setImageResource(R.drawable.ic_quote)

                    Type.assigned, Type.unassigned ->
                        eventIcon.setImageResource(R.drawable.ic_menu_person)

                    Type.milestoned, Type.demilestoned ->
                        eventIcon.setImageResource(R.drawable.ic_milestone)

                    Type.commentDeleted ->
                        eventIcon.setImageResource(R.drawable.ic_delete)

                    Type.labeled, Type.unlabeled ->
                        eventIcon.setImageResource(R.drawable.ic_label)
                    else ->
                        eventIcon.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.transparent))
                }
                recoverEventIconPadding()
            }
        }

        private fun recoverEventIconPadding() {
            setEventIconPadding(4.0f)
        }

        private fun setEventIconPadding(padding: Float) {
            val paddingInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, itemView.context.displayMetrics).toInt()
            itemView.eventIcon.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
        }
    }


}