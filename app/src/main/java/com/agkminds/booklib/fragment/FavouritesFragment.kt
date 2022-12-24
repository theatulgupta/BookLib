package com.agkminds.booklib.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.agkminds.booklib.adapter.DashboardRecyclerAdapter
import com.agkminds.booklib.adapter.FavouriteRecyclerAdapter
import com.agkminds.booklib.database.BookDatabase
import com.agkminds.booklib.database.BookEntity
import com.agkminds.booklib.databinding.FragmentFavouritesBinding

class FavouritesFragment : Fragment() {

    lateinit var binding: FragmentFavouritesBinding
    private var dbBookList = listOf<BookEntity>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        //  Retrieving the bookList from the Database
        dbBookList = RetrieveFavourites(activity as Context).execute().get()

        if (activity != null) {
            binding.progressBar.visibility = View.GONE

            // Setting adapter for the Recycler View
            binding.favRecyclerView.adapter =
                FavouriteRecyclerAdapter(activity as Context, dbBookList)

            // Specifying the LayoutManager of the Recycler View
            binding.favRecyclerView.layoutManager = GridLayoutManager(activity as Context, 2)
        }


        return binding.root
    }

    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<BookEntity>>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): List<BookEntity> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()
            return db.bookDao().getAllBooks()
        }

    }
}