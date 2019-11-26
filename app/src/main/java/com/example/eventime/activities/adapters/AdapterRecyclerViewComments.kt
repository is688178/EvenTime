package com.example.eventime.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventime.R
import com.example.eventime.activities.beans.Comment
import com.example.eventime.activities.utils.DateHourUtils
import org.jetbrains.anko.find

class AdapterRecyclerViewComments(private var comments: ArrayList<Comment>):
    RecyclerView.Adapter<CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)

        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    fun updateData (newComments: ArrayList<Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }
}

class CommentViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

    private val ivPersonPhoto: ImageView = view.find(R.id.item_comment_iv_person_photo)
    private val tvPersonName: TextView = view.find(R.id.item_comment_tv_person_name)
    private val tvCommentDate: TextView = view.find(R.id.item_comment_tv_comment_date)
    private val rbEventRating: RatingBar = view.find(R.id.item_comment_rb_event_rating)
    private val tvCommentDescription: TextView = view.find(R.id.item_comment_tv_comment_description)

    fun bind(comment: Comment) {
        Glide
            .with(view.context)
            .load(comment.person.photo)
            .circleCrop()
            .into(ivPersonPhoto)

        val strName = "${comment.person.name} ${comment.person.lastname}"
        tvPersonName.text = strName
        tvCommentDate.text = DateHourUtils.formatDateToShowFormat(comment.date)
        tvCommentDescription.text = comment.description

    }

}