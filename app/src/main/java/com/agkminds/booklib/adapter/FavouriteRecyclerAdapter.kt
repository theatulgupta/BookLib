package com.agkminds.booklib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.agkminds.booklib.R
import com.agkminds.booklib.database.BookEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(context: Context, private val bookList: List<BookEntity>) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View) : ViewHolder(view) {
        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
        val txtBookAuthor: TextView = view.findViewById(R.id.txtAuthorName)
        val txtBookPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtBookRating: TextView = view.findViewById(R.id.txtRating)
        val imgBookImage: ImageView = view.findViewById(R.id.imgBook)
        val bookItemLayout: LinearLayout = view.findViewById(R.id.bookItemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_favourite_single_book, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val book = bookList[position]

        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookRating.text = book.bookRating
        holder.txtBookPrice.text = book.bookPrice
        Picasso.get().load(book.bookImage).error(R.drawable.ic_book2).into(holder.imgBookImage)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}